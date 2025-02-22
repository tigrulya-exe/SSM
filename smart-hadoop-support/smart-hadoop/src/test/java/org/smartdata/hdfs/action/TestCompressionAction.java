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

import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartdata.hdfs.MiniClusterHarness;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestCompressionAction extends MiniClusterHarness {

  @Override
  @Before
  public void init() throws Exception {
    DEFAULT_BLOCK_SIZE = 1024 * 1024;
    super.init();
  }

  protected void compression(String filePath, String bufferSize) throws IOException {
    CompressionAction compressionAction = new CompressionAction();
    compressionAction.setLocalFileSystem(dfs);
    compressionAction.setContext(smartContext);
    Map<String, String> args = new HashMap<>();
    args.put(compressionAction.FILE_PATH, filePath);
    args.put(compressionAction.BUF_SIZE, bufferSize);
    // set a tmp dir for compression
    String COMPRESS_DIR = "/system/ssm/compress_tmp";
    String tempPath = COMPRESS_DIR + filePath + "_" + "aid" + compressionAction.getActionId()
        + "_" + System.currentTimeMillis();
    args.put(compressionAction.COMPRESS_TMP, tempPath);
//    args.put(CompressionAction.COMPRESS_IMPL, "Lz4");
//    args.put(CompressionAction.COMPRESS_IMPL,"Bzip2");
//    args.put(CompressionAction.COMPRESS_IMPL,"Zlib");
    compressionAction.init(args);
    compressionAction.run();
  }

  @Test
  public void testInit() throws IOException {
    Map<String, String> args = new HashMap<>();
    args.put(CompressionAction.FILE_PATH, "/Test");
    args.put(CompressionAction.BUF_SIZE, "1024");
//    args.put(CompressionAction.COMPRESS_IMPL, "Lz4");
//    args.put(CompressionAction.COMPRESS_IMPL,"Bzip2");
//    args.put(CompressionAction.COMPRESS_IMPL,"Zlib");
    CompressionAction compressionAction = new CompressionAction();
    compressionAction.setLocalFileSystem(dfs);
    compressionAction.setContext(smartContext);
    compressionAction.init(args);
  }

  @Test
  public void testExecute() throws Exception {
    String filePath = "/testCompressFile/fadsfa/213";
    int bufferSize = 1024 * 128;
//    String compressionImpl = "Lz4";
//    String compressionImpl = "Bzip2";
//    String compressionImpl = "Zlib";
    byte[] bytes = TestCompressionAction.BytesGenerator.get(bufferSize);
    short replication = 3;
    long blockSize = DEFAULT_BLOCK_SIZE;

    // Create HDFS file
    OutputStream outputStream = dfsClient.create(filePath, true,
      replication, blockSize);
    outputStream.write(bytes);
    outputStream.close();
    dfsClient.setStoragePolicy(filePath, "COLD");
    HdfsFileStatus srcFileStatus = dfsClient.getFileInfo(filePath);

    // Generate compressed file
    String bufferSizeForCompression = "10MB";
    compression(filePath, bufferSizeForCompression);

    // Check HdfsFileStatus
    HdfsFileStatus fileStatus = dfsClient.getFileInfo(filePath);
    Assert.assertEquals(replication, fileStatus.getReplication());
    Assert.assertEquals(blockSize, fileStatus.getBlockSize());

    // 0 means unspecified.
    if (srcFileStatus.getStoragePolicy() != 0) {
      // To make sure the consistency of storage policy
      Assert.assertEquals(srcFileStatus.getStoragePolicy(),
          fileStatus.getStoragePolicy());
    }
  }

  static final class BytesGenerator {
    private static final byte[] CACHE = new byte[] { 0x0, 0x1, 0x2, 0x3, 0x4,
      0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF };
    private static final Random rnd = new Random(12345l);
    private BytesGenerator() {
    }

    public static byte[] get(int size) {
      byte[] array = (byte[]) Array.newInstance(byte.class, size);
      for (int i = 0; i < size; i++)
        array[i] = CACHE[rnd.nextInt(CACHE.length - 1)];
      return array;
    }
  }
}
