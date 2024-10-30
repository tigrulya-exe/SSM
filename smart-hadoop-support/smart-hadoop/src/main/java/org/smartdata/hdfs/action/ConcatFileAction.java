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
import org.smartdata.action.Utils;
import org.smartdata.action.annotation.ActionSignature;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

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
public class ConcatFileAction extends HdfsAction {
  public static final String DEST_PATH = "-dest";

  private Deque<String> srcPathList;
  private String targetPath;

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    this.srcPathList = Optional.ofNullable(args.get(FILE_PATH))
        .map(paths -> paths.split(","))
        .map(Arrays::asList)
        .map(ArrayDeque::new)
        .orElseGet(ArrayDeque::new);
    this.targetPath = args.get(DEST_PATH);
  }

  @Override
  protected void execute() throws Exception {
    if (CollectionUtils.isEmpty(srcPathList)) {
      throw new IllegalArgumentException("Dest File parameter is missing.");
    }
    if (srcPathList.size() == 1) {
      throw new IllegalArgumentException("Don't accept only one source file");
    }
    if (StringUtils.isBlank(targetPath)) {
      throw new IllegalArgumentException("File parameter is missing.");
    }

    appendLog(
        String.format("Action starts at %s : Concat %s to %s",
            Utils.getFormatedCurrentTime(), srcPathList, targetPath));
    //Merge the files
    concatFiles(srcPathList, targetPath);
  }

  private void concatFiles(Deque<String> allFiles, String target) throws IOException {
    if (target.startsWith("hdfs")) {
      //merge in remote cluster
      //check if all of the source file
      // TODO read conf from files
      Configuration conf = new Configuration();
      FileSystem fs = FileSystem.get(URI.create(target), conf);
      for (String sourceFile : allFiles) {
        if (!fs.isFile(new Path(sourceFile))) {
          throw new IllegalArgumentException("File parameter is not file");
        }
      }
      Path firstFile = new Path(allFiles.pollFirst());
      Path[] restFile = new Path[allFiles.size()];

      int index = -1;
      for (String transFile : allFiles) {
        index++;
        restFile[index] = new Path(transFile);
      }

      fs.concat(firstFile, restFile);
      if (fs.exists(new Path(target))) {
        fs.delete(new Path(target), true);
      }
      fs.rename(firstFile, new Path(target));
      return;
    }


    for (String sourceFile : allFiles) {
      if (dfsClient.getFileInfo(sourceFile).isDir()) {
        throw new IllegalArgumentException("File parameter is not file");
      }
    }
    String firstFile = allFiles.removeFirst();
    String[] restFile = new String[allFiles.size()];
    allFiles.toArray(restFile);
    dfsClient.concat(firstFile, restFile);
    if (dfsClient.exists(target)) {
      dfsClient.delete(target, true);
    }
    dfsClient.rename(firstFile, target, Options.Rename.NONE);
  }
}
