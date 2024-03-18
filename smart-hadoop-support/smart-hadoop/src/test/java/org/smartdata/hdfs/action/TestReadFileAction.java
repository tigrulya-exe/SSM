package org.smartdata.hdfs.action;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.smartdata.hdfs.MiniClusterHarness;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestReadFileAction extends MiniClusterHarness {
  protected void writeFile(String filePath, int length) throws IOException {
    DFSTestUtil.createFile(dfs, new Path(filePath), length, (short) 1, 0L);
  }

  @Test
  public void testInit() {
    ReadFileAction readFileAction = new ReadFileAction();
    Map<String, String> args = new HashMap<>();
    args.put(ReadFileAction.FILE_PATH, "Test");
    readFileAction.init(args);
    args.put(ReadFileAction.BUF_SIZE, "4096");
    readFileAction.init(args);
  }

  @Test
  public void testExecute() throws IOException {
    String filePath = "/testWriteFile/file";
    int size = 66560;
    writeFile(filePath, size);
    ReadFileAction readFileAction = new ReadFileAction();
    readFileAction.setDfsClient(dfsClient);
    readFileAction.setContext(smartContext);
    Map<String, String> args = new HashMap<>();
    args.put(ReadFileAction.FILE_PATH, filePath);
    readFileAction.init(args);
    readFileAction.run();
    Assert.assertTrue(readFileAction.getExpectedAfterRun());
  }
}
