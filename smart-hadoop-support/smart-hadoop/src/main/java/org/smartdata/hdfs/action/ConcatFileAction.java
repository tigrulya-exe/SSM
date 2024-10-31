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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.smartdata.action.annotation.ActionSignature;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static org.smartdata.utils.PathUtil.getRemoteFileSystem;

/**
 * An action to merge a list of file,
 * the source file is separated by comma,
 * and the target file will be over writen
 */
@ActionSignature(
    actionId = "concat",
    displayName = "concat",
    usage = HdfsAction.FILE_PATH + " $src " + ConcatFileAction.DEST_PATH + " $dest"
)
public class ConcatFileAction extends HdfsActionWithRemoteClusterSupport {
  public static final String DEST_PATH = "-dest";

  private Deque<String> srcFiles;
  private String targetFile;

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    this.srcFiles = Optional.ofNullable(args.get(FILE_PATH))
        .map(paths -> paths.split(","))
        .map(Arrays::asList)
        .map(ArrayDeque::new)
        .orElseGet(ArrayDeque::new);
    this.targetFile = args.get(DEST_PATH);
  }

  @Override
  protected void preRun() {
    if (CollectionUtils.isEmpty(srcFiles)) {
      throw new IllegalArgumentException("Dest File parameter is missing.");
    }
    if (srcFiles.size() == 1) {
      throw new IllegalArgumentException("Don't accept only one source file");
    }
    if (StringUtils.isBlank(targetFile)) {
      throw new IllegalArgumentException("File parameter is missing.");
    }
  }

  @Override
  protected String getPath() {
    return targetFile;
  }

  @Override
  protected void onLocalPath() throws Exception {
    for (String sourceFile : srcFiles) {
      if (dfsClient.getFileInfo(sourceFile).isDir()) {
        throw new IllegalArgumentException("File parameter is not file: " + sourceFile);
      }
    }

    String firstFile = srcFiles.removeFirst();
    String[] restFile = new String[srcFiles.size()];

    dfsClient.concat(firstFile, restFile);
    if (dfsClient.exists(targetFile)) {
      dfsClient.delete(targetFile, true);
    }
    dfsClient.rename(firstFile, targetFile, Options.Rename.NONE);
  }

  @Override
  protected void onRemotePath() throws Exception {
    Path targetPath = new Path(targetFile);
    FileSystem targetFs = getRemoteFileSystem(targetPath);

    for (String sourceFile : srcFiles) {
      if (!targetFs.getFileStatus(new Path(sourceFile))) {
        throw new IllegalArgumentException("File parameter is not file");
      }
    }

    Path firstFile = new Path(srcFiles.pollFirst());
    Path[] restFile = new Path[srcFiles.size()];

    int index = -1;
    for (String transFile : srcFiles) {
      index++;
      restFile[index] = new Path(transFile);
    }

    targetFs.concat(firstFile, restFile);
    if (targetFs.exists(new Path(targetFile))) {
      targetFs.delete(new Path(targetFile), true);
    }
    targetFs.rename(firstFile, new Path(targetFile));
  }
}
