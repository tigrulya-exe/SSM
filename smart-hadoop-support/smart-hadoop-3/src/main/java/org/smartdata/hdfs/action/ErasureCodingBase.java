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

import lombok.Setter;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.smartdata.action.ActionException;
import org.smartdata.hdfs.StreamCopyHandler;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import static org.smartdata.utils.PathUtil.getRawPath;

/**
 * An abstract base class for ErasureCodingAction & UnErasureCodingAction.
 */
abstract public class ErasureCodingBase extends HdfsAction {
  public static final String BUF_SIZE = "-bufSize";
  // The value for -ecTmp is assigned by ErasureCodingScheduler.
  public static final String EC_TMP = "-ecTmp";
  public static final String REPLICATION_POLICY_NAME =
      SystemErasureCodingPolicies.getReplicationPolicy().getName();

  protected Path srcPath;
  protected Path ecTmpPath;

  protected int bufferSize = 1024 * 1024;
  @Setter
  protected float progress;
  protected String ecPolicyName;

  protected void convert(HdfsFileStatus srcFileStatus) throws Exception {
    FSDataInputStream in = null;
    DFSOutputStream out = null;

    long blockSize = getConf().getLong(
        DFSConfigKeys.DFS_BLOCK_SIZE_KEY,
        DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT);
    short replication = (short) getConf().getInt(
        DFSConfigKeys.DFS_REPLICATION_KEY,
        DFSConfigKeys.DFS_REPLICATION_DEFAULT);
    try {

      in = localFileSystem.open(srcPath, bufferSize);

      // use the same FsPermission as srcPath
      FsPermission permission = srcFileStatus.getPermission();
      out = getLocalDfsClient().create(
          getRawPath(ecTmpPath), permission, EnumSet.of(CreateFlag.CREATE), true,
          replication, blockSize, null, bufferSize, null, null, ecPolicyName);

      // Keep storage policy according to original file except UNDEF storage policy
      String storagePolicyName = localFileSystem.getStoragePolicy(srcPath).getName();
      if (!storagePolicyName.equals("UNDEF")) {
        localFileSystem.setStoragePolicy(ecTmpPath, storagePolicyName);
      }

      StreamCopyHandler.of(in, out)
          .count(srcFileStatus.getLen())
          .progressConsumer(this::setProgress)
          .closeStreams(false)
          .build()
          .runCopy();
    }
  }

  // set attributes for dest to keep them consistent with their counterpart of src
  protected void setAttributes(HdfsFileStatus fileStatus)
      throws IOException {
    localFileSystem.setOwner(ecTmpPath,
        fileStatus.getOwner(), fileStatus.getGroup());
    localFileSystem.setPermission(ecTmpPath,
        fileStatus.getPermission());
    localFileSystem.setStoragePolicy(ecTmpPath,
        localFileSystem.getStoragePolicy(srcPath).getName());
    localFileSystem.setTimes(ecTmpPath,
        fileStatus.getModificationTime(), fileStatus.getAccessTime());
    boolean aclsEnabled = getConf().getBoolean(
        DFSConfigKeys.DFS_NAMENODE_ACLS_ENABLED_KEY,
        DFSConfigKeys.DFS_NAMENODE_ACLS_ENABLED_DEFAULT);
    if (aclsEnabled) {
      localFileSystem.setAcl(ecTmpPath,
          localFileSystem.getAclStatus(srcPath).getEntries());
    }
    //TODO: check ec related record to avoid paradox
    for (Map.Entry<String, byte[]> entry : localFileSystem.getXAttrs(srcPath).entrySet()) {
      localFileSystem.setXAttr(ecTmpPath, entry.getKey(), entry.getValue(),
          EnumSet.of(XAttrSetFlag.CREATE, XAttrSetFlag.REPLACE));
    }
  }
}