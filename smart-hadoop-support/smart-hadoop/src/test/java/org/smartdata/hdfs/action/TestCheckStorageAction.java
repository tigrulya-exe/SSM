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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.smartdata.hdfs.MiniClusterHarness;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for CheckStorageAction.
 */
public class TestCheckStorageAction extends MiniClusterHarness {
  @Test
  public void testCheckStorageAction() throws IOException {
    CheckStorageAction checkStorageAction = new CheckStorageAction();
    checkStorageAction.setLocalFileSystem(dfs);
    checkStorageAction.setContext(smartContext);
    final String file = "/testPath/file1";
    dfsClient.mkdirs("/testPath", null, true);
    dfsClient.setStoragePolicy("/testPath", "ONE_SSD");

    // write to HDFS
    String content = "This is a file containing two blocks......................";
    DFSTestUtil.writeFile(dfs, new Path(file), content);

    Map<String, String> args = new HashMap<>();
    args.put(CheckStorageAction.FILE_PATH, file);
    // do CheckStorageAction
    checkStorageAction.init(args);
    checkStorageAction.run();
    Assert.assertTrue(checkStorageAction.getExpectedAfterRun());
  }

  @Test
  public void testCheckStorageActionWithWrongFileName() throws IOException {
    CheckStorageAction checkStorageAction = new CheckStorageAction();
    checkStorageAction.setLocalFileSystem(dfs);
    checkStorageAction.setContext(smartContext);

    final String file = "/testPath/wrongfile";
    dfsClient.mkdirs("/testPath", null, true);

    Map<String, String> args = new HashMap<>();
    args.put(CheckStorageAction.FILE_PATH, file);
    // do CheckStorageAction
    checkStorageAction.init(args);
    checkStorageAction.run();
    Assert.assertNotNull(checkStorageAction.getActionStatus().getThrowable());
  }
}
