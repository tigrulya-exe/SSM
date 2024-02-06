/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartdata.hdfs.action;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.action.annotation.ActionSignature;
import org.smartdata.model.FileInfoDiff;

/**
 * action to set MetaData of file
 */
@ActionSignature(
    actionId = "metadata",
    displayName = "metadata",
    usage = HdfsAction.FILE_PATH + " $src " + MetaDataAction.OWNER_NAME + " $owner " +
        MetaDataAction.GROUP_NAME + " $group " + MetaDataAction.BLOCK_REPLICATION + " $replication " +
        MetaDataAction.PERMISSION + " $permission " + MetaDataAction.MTIME + " $mtime " +
        MetaDataAction.ATIME + " $atime"
)
public class MetaDataAction extends HdfsAction {
  private static final Logger LOG = LoggerFactory.getLogger(MetaDataAction.class);
  public static final String OWNER_NAME = "-owner";
  public static final String GROUP_NAME = "-group";
  public static final String BLOCK_REPLICATION = "-replication";
  // only support input like 777
  public static final String PERMISSION = "-permission";
  public static final String MTIME = "-mtime";
  public static final String ATIME = "-atime";

  private String srcPath;

  private FileInfoDiff fileInfoDiff;

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    srcPath = args.get(FILE_PATH);

    fileInfoDiff = new FileInfoDiff()
        .setOwner(args.get(OWNER_NAME))
        .setGroup(args.get(GROUP_NAME))
        .setModificationTime(NumberUtils.createLong(args.get(MTIME)))
        .setAccessTime(NumberUtils.createLong(args.get(ATIME)));

    if (args.containsKey(BLOCK_REPLICATION)) {
      fileInfoDiff.setBlockReplication(Short.parseShort(args.get(BLOCK_REPLICATION)));
    }

    if (args.containsKey(PERMISSION)) {
      fileInfoDiff.setPermission(Short.parseShort(args.get(PERMISSION)));
    }
  }

  @Override
  protected void execute() throws Exception {
    if (srcPath == null) {
      throw new IllegalArgumentException("File src is missing.");
    }

    // TODO read conf from files
    changeFileMetadata(srcPath, fileInfoDiff, dfsClient, new Configuration());
  }

  static void changeFileMetadata(String srcFile, FileInfoDiff fileInfoDiff,
      DFSClient dfsClient, Configuration configuration) throws IOException {
    try {
      if (srcFile.startsWith("hdfs")) {
        changeRemoteClusterFileMetadata(srcFile, fileInfoDiff, configuration);
        return;
      }
      changeLocalClusterFileMetadata(srcFile, fileInfoDiff, dfsClient);
    } catch (
        Exception exception) {
      LOG.error("Metadata cannot be applied", exception);
      throw exception;
    }
  }

  private static void changeRemoteClusterFileMetadata(String srcFile,
      FileInfoDiff fileInfoDiff, Configuration configuration) throws IOException {
    // change file metadata in remote cluster
    FileSystem fs = FileSystem.get(URI.create(srcFile), configuration);
    Path srcPath = new Path(srcFile);
    FileStatus srcFileStatus = fs.getFileStatus(srcPath);

    String owner = srcFileStatus.getOwner();
    String group = srcFileStatus.getGroup();
    if (fileInfoDiff.getOwner() != null) {
      owner = fileInfoDiff.getOwner();
    }
    if (fileInfoDiff.getGroup() != null) {
      group = fileInfoDiff.getGroup();
    }
    if (fileInfoDiff.getOwner() != null
        || fileInfoDiff.getGroup() != null) {
      fs.setOwner(srcPath, owner, group);
    }

    if (fileInfoDiff.getBlockReplication() != null) {
      fs.setReplication(srcPath, fileInfoDiff.getBlockReplication());
    }

    if (fileInfoDiff.getPermission() != null) {
      fs.setPermission(srcPath, new FsPermission(fileInfoDiff.getPermission()));
    }

    long modificationTime = srcFileStatus.getModificationTime();
    long accessTime = srcFileStatus.getAccessTime();
    if (fileInfoDiff.getAccessTime() != null) {
      accessTime = fileInfoDiff.getAccessTime();
    }
    if (fileInfoDiff.getModificationTime() != null) {
      modificationTime = fileInfoDiff.getModificationTime();
    }
    if (fileInfoDiff.getAccessTime() != null
        || fileInfoDiff.getModificationTime() != null) {
      fs.setTimes(srcPath, modificationTime, accessTime);
    }
  }

  private static void changeLocalClusterFileMetadata(String srcFile,
      FileInfoDiff fileInfoDiff, DFSClient dfsClient)
      throws IOException {
    // change file metadata in local cluster
    HdfsFileStatus srcFileInfo = dfsClient.getFileInfo(srcFile);
    String owner = srcFileInfo.getOwner();
    String group = srcFileInfo.getGroup();

    if (fileInfoDiff.getOwner() != null) {
      owner = fileInfoDiff.getOwner();
    }
    if (fileInfoDiff.getGroup() != null) {
      group = fileInfoDiff.getGroup();
    }
    if (fileInfoDiff.getOwner() != null
        || fileInfoDiff.getGroup() != null) {
      dfsClient.setOwner(srcFile, owner, group);
    }

    if (fileInfoDiff.getBlockReplication() != null) {
      dfsClient.setReplication(srcFile, fileInfoDiff.getBlockReplication());
    }

    if (fileInfoDiff.getPermission() != null) {
      dfsClient.setPermission(srcFile, new FsPermission(fileInfoDiff.getPermission()));
    }

    long modificationTime = srcFileInfo.getModificationTime();
    long accessTime = srcFileInfo.getAccessTime();
    if (fileInfoDiff.getAccessTime() != null) {
      accessTime = fileInfoDiff.getAccessTime();
    }
    if (fileInfoDiff.getModificationTime() != null) {
      modificationTime = fileInfoDiff.getModificationTime();
    }
    if (fileInfoDiff.getAccessTime() != null
        || fileInfoDiff.getModificationTime() != null) {
      dfsClient.setTimes(srcFile, modificationTime, accessTime);
    }
  }
}
