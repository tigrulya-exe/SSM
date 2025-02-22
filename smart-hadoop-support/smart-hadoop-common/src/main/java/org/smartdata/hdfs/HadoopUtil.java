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
package org.smartdata.hdfs;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.SmartConstants;
import org.smartdata.conf.SmartConf;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.model.FileInfo;
import org.smartdata.model.FileState;
import org.smartdata.model.NormalFileState;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Optional;

import static org.smartdata.utils.PathUtil.getRawPath;

/**
 * Contain utils related to hadoop cluster.
 */
public class HadoopUtil {
  public static final String HDFS_CONF_DIR = "hdfs";

  public static final Logger LOG =
      LoggerFactory.getLogger(HadoopUtil.class);

  /**
   * HDFS cluster's configuration should be placed in the following directory:
   *    ${SMART_CONF_DIR_KEY}/hdfs/${namenode host}
   *
   * For example, ${SMART_CONF_DIR_KEY}=/var/ssm/conf, nameNodeUrl="hdfs://nnhost:9000",
   *    then, its config files should be placed in "/var/ssm/conf/hdfs/nnhost".
   *
   * @param ssmConf
   * @param nameNodeUrl
   * @param conf
   */
  public static void loadHadoopConf(SmartConf ssmConf, URL nameNodeUrl, Configuration conf)
      throws IOException {
    String ssmConfDir = ssmConf.get(SmartConfKeys.SMART_CONF_DIR_KEY);
    if (ssmConfDir == null || ssmConfDir.equals("")) {
      return;
    }
    loadHadoopConf(ssmConfDir, nameNodeUrl, conf);
  }

  public static void loadHadoopConf(String ssmConfDir, URL nameNodeUrl, Configuration conf)
      throws IOException {
    if (nameNodeUrl == null || nameNodeUrl.getHost() == null) {
      return;
    }

    String dir;
    if (ssmConfDir.endsWith("/")) {
      dir = ssmConfDir + HDFS_CONF_DIR + "/" + nameNodeUrl.getHost();
    } else {
      dir = ssmConfDir + "/" + HDFS_CONF_DIR + "/" + nameNodeUrl.getHost();
    }
    loadHadoopConf(dir, conf);
  }

  /**
   * Load hadoop configure files in the given directory to 'conf'.
   *
   * @param hadoopConfPath directory that hadoop config files located.
   */
  public static void loadHadoopConf(String hadoopConfPath, Configuration conf)
      throws IOException {
    HdfsConfiguration hadoopConf = getHadoopConf(hadoopConfPath);
    if (hadoopConf != null) {
      for (Map.Entry<String, String> entry : hadoopConf) {
        String key = entry.getKey();
        if (conf.get(key) == null) {
          conf.set(key, entry.getValue());
        }
      }
    }
  }

  /**
   * Load hadoop configure files from path configured in conf and override
   * same key's value for Hadoop properties.
   *
   *  Only set the value of key not belonged to SSM. Hadoop conf can contain
   * 'smart.server.rpc.address' introduced by user for creating SmartDFSClient.
   * Its value is the hostname and port of active server. Using this value in
   * SmartConf can cause process failure when standby server is shifting to
   * active server. We reasonably assume that all SSM properties should be
   * determined by SSM's own config. So we exclude all SSM properties when
   * loading Hadoop conf.
   *
   * @param conf
   */
  public static void loadHadoopConf(Configuration conf) throws IOException {
    String hadoopConfPath = conf.get(SmartConfKeys.SMART_HADOOP_CONF_DIR_KEY);
    HdfsConfiguration hadoopConf = getHadoopConf(hadoopConfPath);
    if (hadoopConf != null) {
      for (Map.Entry<String, String> entry : hadoopConf) {
        String key = entry.getKey();
        if (!key.startsWith("smart")) {
          conf.set(key, entry.getValue());
        }
      }
    }
  }

  public static void setSmartConfByHadoop(SmartConf conf) {
    try {
      if (conf.get(SmartConfKeys.SMART_HADOOP_CONF_DIR_KEY) != null) {
        HadoopUtil.loadHadoopConf(conf);
        URI nnUri = HadoopUtil.getNameNodeUri(conf);
        if (nnUri != null) {
          conf.set(SmartConfKeys.SMART_DFS_NAMENODE_RPCSERVER_KEY,
              nnUri.toString());
        }
      }
    } catch (IOException ex) {
      LOG.error("Load hadoop conf in {} error", conf.get(SmartConfKeys.SMART_HADOOP_CONF_DIR_KEY));
    }
  }

  /**
   * Get hadoop configuration from the configure files in the given directory.
   *
   * @param hadoopConfPath directory that hadoop config files located.
   */
  public static HdfsConfiguration getHadoopConf(String hadoopConfPath)
      throws IOException {
    if (hadoopConfPath == null || hadoopConfPath.isEmpty()) {
      LOG.warn("Hadoop configuration path is not set");
      return null;
    } else {
      URL hadoopConfDir;
      HdfsConfiguration hadoopConf = new HdfsConfiguration();
      try {
        if (!hadoopConfPath.endsWith("/")) {
          hadoopConfPath += "/";
        }
        try {
          hadoopConfDir = new URL(hadoopConfPath);
        } catch (MalformedURLException e) {
          hadoopConfDir = new URL("file://" + hadoopConfPath);
        }
        Path hadoopConfDirPath = Paths.get(hadoopConfDir.toURI());
        if (Files.exists(hadoopConfDirPath) &&
            Files.isDirectory(hadoopConfDirPath)) {
          LOG.debug("Hadoop configuration path = " + hadoopConfPath);
        } else {
          throw new IOException("Hadoop configuration path [" + hadoopConfPath
              + "] doesn't exist or is not a directory");
        }

        try {
          URL coreConfFile = new URL(hadoopConfDir, "core-site.xml");
          Path coreFilePath = Paths.get(coreConfFile.toURI());
          if (Files.exists(coreFilePath)) {
            hadoopConf.addResource(coreConfFile);
            LOG.debug("Hadoop configuration file [" +
                coreConfFile.toExternalForm() + "] is loaded");
          } else {
            throw new IOException("Hadoop configuration file [" +
                coreConfFile.toExternalForm() + "] doesn't exist");
          }
        } catch (MalformedURLException e1) {
          throw new IOException("Access hadoop configuration file core-site.xml failed", e1);
        }

        try {
          URL hdfsConfFile = new URL(hadoopConfDir, "hdfs-site.xml");
          Path hdfsFilePath = Paths.get(hdfsConfFile.toURI());
          if (Files.exists(hdfsFilePath)) {
            hadoopConf.addResource(hdfsConfFile);
            LOG.debug("Hadoop configuration file [" +
                hdfsConfFile.toExternalForm() + "] is loaded");
          } else {
            throw new IOException("Hadoop configuration file [" +
                hdfsConfFile.toExternalForm() + "] doesn't exist");
          }
        } catch (MalformedURLException e1) {
          throw new IOException("Access hadoop configuration file hdfs-site.xml failed", e1);
        }
      } catch (URISyntaxException e) {
        throw new IOException("Access hadoop configuration path [" + hadoopConfPath
            + "] failed" + e);
      }
      return hadoopConf;
    }
  }

  public static URI getNameNodeUri(Configuration conf)
      throws IOException {
    String nnRpcAddr = null;

    String[] rpcAddrKeys = {
        SmartConfKeys.SMART_DFS_NAMENODE_RPCSERVER_KEY,
        DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY,
        DFSConfigKeys.DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY,
        // Keep this last, haven't find a predefined key for this property
        "fs.defaultFS"
    };

    String[] nnRpcAddrs = new String[rpcAddrKeys.length];

    int lastNotNullIdx = 0;
    for (int index = 0; index < rpcAddrKeys.length; index++) {
      nnRpcAddrs[index] = conf.get(rpcAddrKeys[index]);
      LOG.debug("Get namenode URL, key: " + rpcAddrKeys[index] + ", value:" + nnRpcAddrs[index]);
      lastNotNullIdx = nnRpcAddrs[index] == null ? lastNotNullIdx : index;
      nnRpcAddr = nnRpcAddr == null ? nnRpcAddrs[index] : nnRpcAddr;
    }

    if (nnRpcAddr == null || nnRpcAddr.equalsIgnoreCase("file:///")) {
      throw new IOException("Can not find NameNode RPC server address. "
          + "Please configure it through '"
          + SmartConfKeys.SMART_DFS_NAMENODE_RPCSERVER_KEY + "'.");
    }

    if (lastNotNullIdx == 0 && rpcAddrKeys.length > 1) {
      conf.set(rpcAddrKeys[1], nnRpcAddr);
    }

    try {
      return new URI(nnRpcAddr);
    } catch (URISyntaxException e) {
      throw new IOException("Invalid URI Syntax: " + nnRpcAddr, e);
    }
  }

  public static FileInfo convertFileStatus(HdfsFileStatus status, String path) {
    return FileInfo.newBuilder()
        .setPath(path)
        .setFileId(status.getFileId())
        .setLength(status.getLen())
        .setIsdir(status.isDir())
        .setBlockReplication(status.getReplication())
        .setBlocksize(status.getBlockSize())
        .setModificationTime(status.getModificationTime())
        .setAccessTime(status.getAccessTime())
        .setPermission(status.getPermission().toShort())
        .setOwner(status.getOwner())
        .setGroup(status.getGroup())
        .setStoragePolicy(status.getStoragePolicy())
        .setErasureCodingPolicy(CompatibilityHelperLoader.getHelper().getErasureCodingPolicy(status))
        .build();
  }

  public static <T> T doAsCurrentUser(PrivilegedExceptionAction<T> action) throws IOException {
    try {
      return UserGroupInformation.getCurrentUser().doAs(action);
    } catch (InterruptedException e) {
      throw new IOException("Privileged action interrupted", e);
    }
  }

  public static DFSClient getDFSClient(final URI nnUri, final Configuration conf)
      throws IOException {
    return doAsCurrentUser(() -> new DFSClient(nnUri, conf));
  }

  public static DistributedFileSystem getDistributedFileSystem(
      final URI nnUri, final Configuration conf) throws IOException {
    return doAsCurrentUser(() -> {
      DistributedFileSystem fileSystem = new DistributedFileSystem();
      fileSystem.initialize(nnUri, conf);
      return fileSystem;
    });
  }

  /**
   * Return FileState, like SmartDFSClient#getFileState().
   * @param   dfsClient
   * @param   filePath
   * @return a FileState
   * @throws IOException
   */
  public static FileState getFileState(DFSClient dfsClient, String filePath)
      throws IOException {
    try {
      byte[] fileState = dfsClient.getXAttr(filePath,
          SmartConstants.SMART_FILE_STATE_XATTR_NAME);
      if (fileState != null) {
        return (FileState) SerializationUtils.deserialize(fileState);
      }
    } catch (RemoteException e) {
      return new NormalFileState(filePath);
    }
    return new NormalFileState(filePath);
  }

  public static FileState getFileState(
      DistributedFileSystem fileSystem,
      org.apache.hadoop.fs.Path filePath)
      throws IOException {
    try {
      byte[] fileState = fileSystem.getXAttr(
          filePath, SmartConstants.SMART_FILE_STATE_XATTR_NAME);
      return Optional.ofNullable(fileState)
          .map(SerializationUtils::deserialize)
          .map(FileState.class::cast)
          .orElseGet(() -> new NormalFileState(getRawPath(filePath)));
    } catch (RemoteException e) {
      return new NormalFileState(getRawPath(filePath));
    }
  }
}
