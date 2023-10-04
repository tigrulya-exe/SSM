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
package org.smartdata.metastore.dao;

import org.smartdata.model.CompactFileState;
import org.smartdata.model.FileState;

import java.util.List;

public interface SmallFileDao {

  void insertUpdate(CompactFileState compactFileState);

  int[] batchInsertUpdate(CompactFileState[] fileStates);

  void deleteByPath(String path, boolean recursive);

  int[] batchDelete(List<String> paths);

  void deleteAll();

  FileState getFileStateByPath(String path);

  List<String> getSmallFilesByContainerFile(String containerFilePath);

  List<String> getAllContainerFiles();

  void renameFile(String oldPath, String newPath, boolean recursive);
}
