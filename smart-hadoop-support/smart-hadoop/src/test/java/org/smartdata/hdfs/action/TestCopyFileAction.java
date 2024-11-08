/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.hdfs.action;

import com.google.common.collect.Sets;
import lombok.Setter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.FailingDfsInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.smartdata.hdfs.MultiClusterHarness;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.smartdata.hdfs.action.CopyPreservedAttributesAction.PreserveAttribute.MODIFICATION_TIME;
import static org.smartdata.hdfs.action.CopyPreservedAttributesAction.PreserveAttribute.OWNER;
import static org.smartdata.hdfs.action.CopyPreservedAttributesAction.PreserveAttribute.REPLICATION_NUMBER;

/**
 * Test for CopyFileAction.
 */
public class TestCopyFileAction extends MultiClusterHarness {

  private static final String FILE_TO_COPY_CONTENT = "testContent 112";

  private void copyFile(Path src, Path dest, long length,
                        long offset) throws Exception {
    copyFile(src, dest, length, offset, action -> {});
  }

  private void copyFile(Path src, Path dest, long length, long offset,
                        Consumer<CopyFileAction> actionConfigurer,
                        CopyPreservedAttributesAction.PreserveAttribute... preserveAttributes
  ) throws Exception {
    CopyFileAction copyFileAction = new CopyFileAction();
    copyFileAction.setDfsClient(dfsClient);
    copyFileAction.setContext(smartContext);

    Map<String, String> args = new HashMap<>();
    args.put(CopyFileAction.FILE_PATH, src.toUri().getPath());
    args.put(CopyFileAction.DEST_PATH, dest.toString());
    args.put(CopyFileAction.LENGTH, String.valueOf(length));
    args.put(CopyFileAction.OFFSET_INDEX, String.valueOf(offset));

    if (preserveAttributes.length != 0) {
      String attributesOption = Sets.newHashSet(preserveAttributes)
          .stream()
          .map(Object::toString)
          .collect(Collectors.joining(","));
      args.put(CopyFileAction.PRESERVE, attributesOption);
    }
    actionConfigurer.accept(copyFileAction);

    copyFileAction.init(args);
    copyFileAction.run();

    if (!copyFileAction.getExpectedAfterRun()) {
      throw new RuntimeException("Action failed", copyFileAction.getThrowable());
    }
  }

  @Test
  public void testFileCopy() throws Exception {
    Path srcPath = new Path("/testCopy/file1");
    Path destPath = anotherClusterPath("/backup", srcPath.getName());

    DFSTestUtil.writeFile(dfs, srcPath, "testCopy1");

    copyFile(srcPath, destPath, 0, 0);

    assertFileContent(destPath, "testCopy1");
  }

  @Test
  public void testCopyWithOffset() throws Exception {
    Path srcPath = new Path("/testCopy/testCopyWithOffset");
    Path destPath = anotherClusterPath("/backup", srcPath.getName());

    byte[] srcFileContent = {0, 1, 2, 3, 4, 5, 6, 7};
    DFSTestUtil.writeFile(dfs, srcPath, srcFileContent);

    byte[] destFileContent = {0, 1, 2, 3};
    DFSTestUtil.writeFile(anotherDfs, destPath, destFileContent);

    copyFile(srcPath, destPath, 4, 4);

    Assert.assertTrue(anotherDfs.exists(destPath));

    byte[] actualDestContent = DFSTestUtil.readFileAsBytes(anotherDfs, destPath);
    Assert.assertArrayEquals(srcFileContent, actualDestContent);
  }

  @Test
  public void testAppend() throws Exception {
    Path srcPath = new Path("/testCopy/testAppend");
    Path destPath = anotherClusterPath("/backup", srcPath.getName());

    DFSTestUtil.createFile(dfs, srcPath, 100, (short) 3, 0xFEED);
    DFSTestUtil.createFile(anotherDfs, destPath, 50, (short) 3, 0xFEED);

    copyFile(srcPath, destPath, 50, 50);

    Assert.assertTrue(anotherDfs.exists(destPath));
    Assert.assertEquals(100, anotherDfs.getFileStatus(destPath).getLen());
  }

  @Test
  public void testPreserveAllAttributes() throws Exception {
    Path srcPath = createFileWithAttributes("/test/src/fileToCopy");
    Path destPath = anotherClusterPath("/dest", srcPath.getName());

    copyFileWithAttributes(srcPath, destPath,
        CopyPreservedAttributesAction.PreserveAttribute.values());

    FileStatus destFileStatus = anotherDfs.getFileStatus(destPath);
    Assert.assertEquals(new FsPermission("777"), destFileStatus.getPermission());
    Assert.assertEquals("newUser", destFileStatus.getOwner());
    Assert.assertEquals("newGroup", destFileStatus.getGroup());
    Assert.assertEquals(2, destFileStatus.getReplication());
    Assert.assertEquals(0L, destFileStatus.getModificationTime());
  }

  @Test
  public void testPreserveSomeAttributes() throws Exception {
    Path srcPath = createFileWithAttributes("/test/src/anotherFileToCopy");
    Path destPath = anotherClusterPath("/dest", srcPath.getName());

    copyFileWithAttributes(srcPath, destPath,
        OWNER, MODIFICATION_TIME, REPLICATION_NUMBER);

    FileStatus destFileStatus = anotherDfs.getFileStatus(destPath);
    Assert.assertEquals("newUser", destFileStatus.getOwner());
    Assert.assertEquals(0L, destFileStatus.getModificationTime());
    Assert.assertEquals(2, destFileStatus.getReplication());
    Assert.assertNotEquals(new FsPermission("777"), destFileStatus.getPermission());
    Assert.assertNotEquals("newGroup", destFileStatus.getGroup());
  }

  @Test
  public void testRetryUnsuccessfulAppend() throws Exception {
    Path srcPath = new Path("/testCopy/testRetryAppend");
    Path destPath = anotherClusterPath("/backup", srcPath.getName());

    DFSTestUtil.createFile(dfs, srcPath, 100, (short) 3, 0xFEED);
    DFSTestUtil.createFile(anotherDfs, destPath, 50, (short) 3, 0xFEED);

    try (FailingDfsClient failingDfsClient =
             new FailingDfsClient(cluster.getConfiguration(0), true)) {
      try {
        copyFile(srcPath, destPath, 50, 50, action -> action.setDfsClient(failingDfsClient));
      } catch (Exception e) {
        // it should fail at first time after writing 1 byte
      }

      failingDfsClient.setShouldFail(false);

      copyFile(srcPath, destPath, 50, 50, action -> action.setDfsClient(failingDfsClient));

      Assert.assertTrue(anotherDfs.exists(destPath));
      Assert.assertEquals(100, anotherDfs.getFileStatus(destPath).getLen());
    }
  }

  @Setter
  private static class FailingDfsClient extends DFSClient {
    private boolean shouldFail;

    private FailingDfsClient(Configuration config, boolean shouldFail) throws IOException {
      super(DFSUtilClient.getNNAddress(config), config);
      this.shouldFail = shouldFail;
    }

    @Override
    public DFSInputStream open(String src) throws IOException {
      return new FailingDfsInputStream(super.open(src), shouldFail, 1);
    }
  }

  private Path createFileWithAttributes(String path) throws IOException {
    Path srcFilePath = new Path(path);

    DFSTestUtil.writeFile(dfs, srcFilePath, FILE_TO_COPY_CONTENT);

    FsPermission newPermission = new FsPermission("777");
    dfs.setOwner(srcFilePath, "newUser", "newGroup");
    dfs.setPermission(srcFilePath, newPermission);
    dfs.setReplication(srcFilePath, (short) 2);
    dfs.setTimes(srcFilePath, 0L, 1L);

    return srcFilePath;
  }

  private void copyFileWithAttributes(Path srcFilePath, Path destPath,
                                      CopyPreservedAttributesAction.PreserveAttribute... preserveAttributes)
      throws Exception {
    copyFile(srcFilePath, destPath, 0, 0, action -> {}, preserveAttributes);
    assertFileContent(destPath, FILE_TO_COPY_CONTENT);
  }

  private void assertFileContent(Path filePath, String expectedContent) throws Exception {
    Assert.assertTrue(anotherDfs.exists(filePath));
    String actualContent = DFSTestUtil.readFile(anotherDfs, filePath);
    Assert.assertEquals(expectedContent, actualContent);
  }
}
