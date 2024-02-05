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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.action.ActionException;
import org.smartdata.action.Utils;
import org.smartdata.action.annotation.ActionSignature;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.hdfs.CompatibilityHelperLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import org.smartdata.model.FileInfoDiff;

import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.MODIFICATION_TIME;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.GROUP;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.OWNER;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.PERMISSIONS;
import static org.smartdata.hdfs.action.CopyFileAction.PreserveAttribute.REPLICATION_NUMBER;

/**
 * An action to copy a single file from src to destination.
 * If dest doesn't contains "hdfs" prefix, then destination will be set to
 * current cluster, i.e., copy between dirs in current cluster.
 * Note that destination should contains filename.
 */
@ActionSignature(
    actionId = "copy",
    displayName = "copy",
    usage = HdfsAction.FILE_PATH + " $src "
        + CopyFileAction.DEST_PATH + " $dest "
        + CopyFileAction.OFFSET_INDEX + " $offset "
        + CopyFileAction.LENGTH + " $length "
        + CopyFileAction.BUF_SIZE + " $size "
        + CopyFileAction.PRESERVE + " $attributes"
)
public class CopyFileAction extends HdfsAction {
  private static final Logger LOG =
      LoggerFactory.getLogger(CopyFileAction.class);
  public static final String BUF_SIZE = "-bufSize";
  public static final String DEST_PATH = "-dest";
  public static final String OFFSET_INDEX = "-offset";
  public static final String LENGTH = "-length";
  public static final String PRESERVE = "-preserve";

  private String srcPath;
  private String destPath;
  private long offset = 0;
  private long length = 0;
  private int bufferSize = 64 * 1024;
  private List<String> rawPreserveAttributes = Collections.emptyList();
  private Configuration conf;

  @Override
  public void init(Map<String, String> args) {
    try {
      this.conf = getContext().getConf();
      String nameNodeURL =
          this.conf.get(SmartConfKeys.SMART_DFS_NAMENODE_RPCSERVER_KEY);
      conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, nameNodeURL);
    } catch (NullPointerException e) {
      this.conf = new Configuration();
      appendLog("Conf error!, NameNode URL is not configured!");
    }
    super.init(args);
    this.srcPath = args.get(FILE_PATH);
    if (args.containsKey(DEST_PATH)) {
      this.destPath = args.get(DEST_PATH);
    }
    if (args.containsKey(BUF_SIZE)) {
      bufferSize = Integer.parseInt(args.get(BUF_SIZE));
    }
    if (args.containsKey(OFFSET_INDEX)) {
      offset = Long.parseLong(args.get(OFFSET_INDEX));
    }
    if (args.containsKey(LENGTH)) {
      length = Long.parseLong(args.get(LENGTH));
    }
    if (StringUtils.isNotBlank(args.get(PRESERVE))) {
      rawPreserveAttributes = Arrays.asList(args.get(PRESERVE).split(","));
    }
  }

  @Override
  protected void execute() throws Exception {
    if (srcPath == null) {
      throw new IllegalArgumentException("File parameter is missing.");
    }
    if (destPath == null) {
      throw new IllegalArgumentException("Dest File parameter is missing.");
    }
    Set<PreserveAttribute> preserveAttributes = parsePreserveAttributes();
    appendLog(
        String.format("Action starts at %s : Read %s",
            Utils.getFormatedCurrentTime(), srcPath));
    if (!dfsClient.exists(srcPath)) {
      throw new ActionException("CopyFile Action fails, file doesn't exist!");
    }
    appendLog(
        String.format("Copy from %s to %s", srcPath, destPath));
    if (offset == 0 && length == 0) {
      copySingleFile(srcPath, destPath);
    }
    if (length != 0) {
      copyWithOffset(srcPath, destPath, bufferSize, offset, length);
    }
    copyAttributes(preserveAttributes);
    appendLog("Copy Successfully!!");
  }

  private boolean copySingleFile(String src, String dest) throws IOException {
    //get The file size of source file
    long fileSize = getFileStatus(src).getLen();
    appendLog(
        String.format("Copy the whole file with length %s", fileSize));
    return copyWithOffset(src, dest, bufferSize, 0, fileSize);
  }

  private boolean copyWithOffset(String src, String dest, int bufferSize,
      long offset, long length) throws IOException {
    appendLog(
        String.format("Copy with offset %s and length %s", offset, length));
    InputStream in = null;
    OutputStream out = null;

    try {
      in = getSrcInputStream(src);
      out = getDestOutPutStream(dest, offset);
      //skip offset
      in.skip(offset);
      byte[] buf = new byte[bufferSize];
      long bytesRemaining = length;

      while (bytesRemaining > 0L) {
        int bytesToRead =
            (int) (bytesRemaining < (long) buf.length ? bytesRemaining :
                (long) buf.length);
        int bytesRead = in.read(buf, 0, bytesToRead);
        if (bytesRead == -1) {
          break;
        }
        out.write(buf, 0, bytesRead);
        bytesRemaining -= (long) bytesRead;
      }
      return true;
    } finally {
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
    }
  }

  private FileStatus getFileStatus(String fileName) throws IOException {
    FileSystem fs = FileSystem.get(URI.create(fileName), conf);
    if (fileName.startsWith("hdfs")) {
      // Get InputStream from URL
      return fs.getFileStatus(new Path(fileName));
    } else {
      return (FileStatus) dfsClient.getFileInfo(fileName);
    }
  }

  private InputStream getSrcInputStream(String src) throws IOException {
    if (src.startsWith("hdfs")) {
      // Copy between different remote clusters
      // Get InputStream from URL
      FileSystem fs = FileSystem.get(URI.create(src), conf);
      return fs.open(new Path(src));
    } else {
      return dfsClient.open(src);
    }
  }

  private OutputStream getDestOutPutStream(String dest, long offset) throws IOException {
    if (dest.startsWith("hdfs")) {
      // Copy between different clusters
      // Copy to remote HDFS
      // Get OutPutStream from URL
      FileSystem fs = FileSystem.get(URI.create(dest), conf);
      int replication = DFSConfigKeys.DFS_REPLICATION_DEFAULT;
      try {
        replication = fs.getServerDefaults(new Path(dest)).getReplication();
        if (replication != DFSConfigKeys.DFS_REPLICATION_DEFAULT) {
          appendLog("Remote Replications =" + replication);
        }
      } catch (IOException e) {
        LOG.debug("Get Server default replication error!", e);
      }
      if (fs.exists(new Path(dest)) && offset != 0) {
        appendLog("Append to existing file " + dest);
        return fs.append(new Path(dest));
      } else {
        return fs.create(new Path(dest), true, (short) replication);
      }
    } else if (dest.startsWith("s3")) {
      // Copy to s3
      FileSystem fs = FileSystem.get(URI.create(dest), conf);
      return fs.create(new Path(dest), true);
    } else {
      return CompatibilityHelperLoader.getHelper()
          .getDFSClientAppend(dfsClient, dest, bufferSize, offset);
    }
  }

  private Set<PreserveAttribute> parsePreserveAttributes() {
    Set<PreserveAttribute> attributesFromOptions = rawPreserveAttributes
        .stream()
        .map(PreserveAttribute::fromOption)
        .collect(Collectors.toSet());

    return attributesFromOptions.isEmpty()
        // preserve file owner, group and permissions by default
        ? Sets.newHashSet(OWNER, GROUP, PERMISSIONS)
        : attributesFromOptions;
  }

  private void copyAttributes(Set<PreserveAttribute> preserveAttributes) throws IOException {
    FileStatus srcFileStatus = getFileStatus(srcPath);
    FileInfoDiff fileInfoDiff = new FileInfoDiff();

    if (preserveAttributes.contains(PERMISSIONS)) {
      fileInfoDiff.setPermission(srcFileStatus.getPermission().toShort());
    }

    if (preserveAttributes.contains(OWNER)) {
      fileInfoDiff.setOwner(srcFileStatus.getOwner());
    }

    if (preserveAttributes.contains(GROUP)) {
      fileInfoDiff.setGroup(srcFileStatus.getGroup());
    }

    if (preserveAttributes.contains(REPLICATION_NUMBER)) {
      fileInfoDiff.setBlockReplication(srcFileStatus.getReplication());
    }

    if (preserveAttributes.contains(MODIFICATION_TIME)) {
      fileInfoDiff.setModificationTime(srcFileStatus.getModificationTime());
    }

    MetaDataAction.changeFileMetadata(destPath, fileInfoDiff, dfsClient, conf);
    appendLog("Successfully transferred file attributes: " + preserveAttributes);
  }

  public enum PreserveAttribute {
    OWNER("owner"),
    GROUP("group"),
    PERMISSIONS("permissions"),
    REPLICATION_NUMBER("replication"),
    MODIFICATION_TIME("modification-time");

    private final String name;

    PreserveAttribute(String name) {
      this.name = name;
    }

    public static PreserveAttribute fromOption(String option) {
      return Arrays.stream(PreserveAttribute.values())
          .filter(attr -> attr.name.equals(option))
          .findFirst()
          .orElseThrow(() ->
              new IllegalArgumentException("Wrong preserve attribute: " + option));
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
