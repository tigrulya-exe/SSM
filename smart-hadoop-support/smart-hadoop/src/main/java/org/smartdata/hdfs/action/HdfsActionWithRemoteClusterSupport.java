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

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import static org.smartdata.utils.PathUtil.getRemoteFileSystem;
import static org.smartdata.utils.PathUtil.isAbsoluteRemotePath;

public abstract class HdfsActionWithRemoteClusterSupport extends HdfsAction {

  @Override
  protected void execute() throws Exception {
    preExecute();

    Path targetPath = new Path(getTargetFile());
    if (isRemoteMode()) {
      preRemoteExecute();
      execute(getRemoteFileSystem(targetPath));
    } else {
      preLocalExecute();
      execute(localFileSystem);
    }

    postExecute();
  }

  protected void preExecute() throws Exception {

  }

  protected void postExecute() throws Exception {

  }

  protected void preLocalExecute() throws Exception {

  }

  protected void preRemoteExecute() throws Exception {

  }

  protected String getTargetFile() {
    return getArguments().get(FILE_PATH);
  }

  protected boolean isRemoteMode() {
    return isAbsoluteRemotePath(getTargetFile());
  }

  protected abstract void execute(FileSystem fileSystem) throws Exception;
}
