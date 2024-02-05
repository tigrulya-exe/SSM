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
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.smartdata.hdfs.MiniClusterHarness;

import java.util.HashMap;
import java.util.Map;

import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.MODIFICATION_TIME;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.OWNER;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.REPLICATION_NUMBER;

/**
 * Test for CopyFileAction.
 */
@RunWith(Parameterized.class)
public class TestCopyFileAction extends MiniClusterHarness {

  private static final String FILE_TO_COPY_CONTENT = "testContent 112";

  @Parameterized.Parameter()
  public boolean isRemoteCopy;

  private String pathPrefix;

  @Parameterized.Parameters(name = "Remote copy - {0}")
  public static Object[] parameters() {
    return new Object[] {true, false};
  }

  @Before
  public void setUp() {
    pathPrefix = isRemoteCopy ? dfs.getUri().toString() : "";
  }

  private void copyFile(String src, String dest, long length,
      long offset) throws Exception {
    copyFile(src, dest, length, offset, Collections.emptySet());
  }

  private void copyFile(String src, String dest, long length,
      long offset, Set<CopyFileAction.PreserveAttribute> preserveAttributes) throws Exception {
    CopyFileAction copyFileAction = new CopyFileAction();
    copyFileAction.setDfsClient(dfsClient);
    copyFileAction.setContext(smartContext);
    Map<String, String> args = new HashMap<>();
    args.put(CopyFileAction.FILE_PATH, src);
    args.put(CopyFileAction.DEST_PATH, dest);
    args.put(CopyFileAction.LENGTH, "" + length);
    args.put(CopyFileAction.OFFSET_INDEX, "" + offset);

    if (!preserveAttributes.isEmpty()) {
      String attributesOption = preserveAttributes.stream()
          .map(Object::toString)
          .collect(Collectors.joining(","));
      args.put(CopyFileAction.PRESERVE, attributesOption);
    }

    copyFileAction.init(args);
    copyFileAction.run();
    Assert.assertTrue(copyFileAction.getExpectedAfterRun());
  }

  @Test
  public void testFileCopy() throws Exception {
    final String srcPath = "/testCopy";
    final String file1 = "file1";
    final String destPath = pathPrefix + "/backup";
    Path srcDir = new Path(srcPath);
    dfs.mkdirs(srcDir);
    dfs.mkdirs(new Path(destPath));
    // write to DISK
    final FSDataOutputStream out1 = dfs.create(new Path(srcPath + "/" + file1));
    out1.writeChars("testCopy1");
    out1.close();
    copyFile(srcPath + "/" + file1, destPath + "/" + file1, 0, 0);
    // Check if file exists
    Assert.assertTrue(dfsClient.exists("/backup/" + file1));
    final FSDataInputStream in1 = dfs.open(new Path(destPath + "/" + file1));
    StringBuilder readString = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      readString.append(in1.readChar());
    }
    Assert.assertTrue(readString.toString().equals("testCopy1"));
  }

  @Test
  public void testCopyWithOffset() throws Exception {
    final String srcPath = "/testCopy";
    final String file1 = "file1";
    final String destPath = pathPrefix + "/backup";
    dfs.mkdirs(new Path(srcPath));
    dfs.mkdirs(new Path(destPath));
    // write to DISK
    final FSDataOutputStream out1 = dfs.create(new Path(srcPath + "/" + file1));
    for (int i = 0; i < 50; i++) {
      out1.writeByte(1);
    }
    for (int i = 0; i < 50; i++) {
      out1.writeByte(2);
    }
    out1.close();
    copyFile(srcPath + "/" + file1, destPath + "/" + file1, 50, 50);
    // Check if file exists
    Assert.assertTrue(dfsClient.exists("/backup/" + file1));
    final FSDataInputStream in1 = dfs.open(new Path(destPath + "/" + file1));
    for (int i = 0; i < 50; i++) {
      Assert.assertTrue(in1.readByte() == 2);
    }
  }

  @Test
  public void testAppend() throws Exception {
    final String srcPath = "/testCopy";
    final String file1 = "file1";
    final String destPath = pathPrefix + "/backup";
    dfs.mkdirs(new Path(srcPath));
    dfs.mkdirs(new Path(destPath));
    // write to DISK
    DFSTestUtil.createFile(dfs, new Path(srcPath + "/" + file1), 100, (short) 3,
        0xFEED);
    DFSTestUtil.createFile(dfs, new Path(destPath + "/" + file1), 50, (short) 3,
        0xFEED);
    copyFile(srcPath + "/" + file1, destPath + "/" + file1, 50, 50);
    // Check if file exists
    Assert.assertTrue(dfsClient.exists("/backup/" + file1));
    FileStatus fileStatus = dfs.getFileStatus(new Path(destPath + "/" + file1));
    Assert.assertEquals(100, fileStatus.getLen());
  }

  @Test
  public void testPreserveAllAttributes() throws Exception {
    Path srcFilePath = createFileWithAttributes("/test/src/fileToCopy");
    String destPath = pathPrefix + "/dest/fileToCopy";

    Path copiedFilePath = copyFileWithAttributes(srcFilePath, destPath,
        Sets.newHashSet(CopyFileAction.PreserveAttribute.values()));

    FileStatus destFileStatus = dfs.getFileStatus(copiedFilePath);
    Assert.assertEquals(new FsPermission("777"), destFileStatus.getPermission());
    Assert.assertEquals("newUser", destFileStatus.getOwner());
    Assert.assertEquals("newGroup", destFileStatus.getGroup());
    Assert.assertEquals(2, destFileStatus.getReplication());
    Assert.assertEquals(0L, destFileStatus.getModificationTime());
  }

  @Test
  public void testPreserveSomeAttributes() throws Exception {
    Path srcFilePath = createFileWithAttributes("/test/src/fileToCopy");
    String destPath = pathPrefix + "/dest/fileToCopy";

    Path copiedFilePath = copyFileWithAttributes(srcFilePath, destPath,
        Sets.newHashSet(OWNER, MODIFICATION_TIME, REPLICATION_NUMBER));

    FileStatus destFileStatus = dfs.getFileStatus(copiedFilePath);
    Assert.assertEquals("newUser", destFileStatus.getOwner());
    Assert.assertEquals(0L, destFileStatus.getModificationTime());
    Assert.assertEquals(2, destFileStatus.getReplication());
    Assert.assertNotEquals(new FsPermission("777"), destFileStatus.getPermission());
    Assert.assertNotEquals("newGroup", destFileStatus.getGroup());
  }

  private Path createFileWithAttributes(String path) throws IOException {
    Path srcFilePath = new Path(path);
    dfs.mkdirs(srcFilePath.getParent());

    try (FSDataOutputStream srcOutStream = dfs.create(srcFilePath)) {
      srcOutStream.writeUTF(FILE_TO_COPY_CONTENT);
    }

    FsPermission newPermission = new FsPermission("777");
    dfs.setOwner(srcFilePath, "newUser", "newGroup");
    dfs.setPermission(srcFilePath, newPermission);
    dfs.setReplication(srcFilePath, (short) 2);
    dfs.setTimes(srcFilePath, 0L, 1L);

    return srcFilePath;
  }

  private Path copyFileWithAttributes(Path srcFilePath, String destPath,
      Set<CopyFileAction.PreserveAttribute> preserveAttributes) throws Exception {

    copyFile(srcFilePath.toUri().getPath(), destPath,
        0, 0, preserveAttributes);
    Assert.assertTrue(dfsClient.exists(URI.create(destPath).getPath()));

    Path destFilePath = new Path(destPath);
    try (FSDataInputStream destInputStream = dfs.open(destFilePath)) {
      String actualContent = destInputStream.readUTF();
      Assert.assertEquals(FILE_TO_COPY_CONTENT, actualContent);
    }

    return destFilePath;
  }
}
