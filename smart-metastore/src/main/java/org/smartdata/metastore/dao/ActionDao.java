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

import org.smartdata.model.ActionInfo;

import java.util.List;

public interface ActionDao {
  List<ActionInfo> getAll();

  Long getCountOfAction();

  ActionInfo getById(long aid);

  List<ActionInfo> getByIds(List<Long> aids);

  List<ActionInfo> getByCid(long cid);

  List<ActionInfo> getByCondition(String aidCondition,
                                  String cidCondition);

  List<ActionInfo> getLatestActions(int size);

  List<ActionInfo> getLatestActions(String actionName, int size);

  List<ActionInfo> getLatestActions(String actionName, int size,
                                    boolean successful, boolean finished);

  List<ActionInfo> getLatestActions(String actionName, boolean successful,
                                    int size);

  List<ActionInfo> getAPageOfAction(long start, long offset, List<String> orderBy,
                                    List<Boolean> isDesc);

  List<ActionInfo> getAPageOfAction(long start, long offset);

  List<ActionInfo> searchAction(String path, long start, long offset, List<String> orderBy,
                                List<Boolean> isDesc, long[] retTotalNumActions);

  List<ActionInfo> getLatestActions(String actionType, int size,
                                    boolean finished);

  void delete(long aid);

  void deleteCmdletActions(long cid);

  int[] batchDeleteCmdletActions(List<Long> cids);

  void deleteAll();

  void insert(ActionInfo actionInfo);

  void insert(ActionInfo[] actionInfos);

  int[] replace(ActionInfo[] actionInfos);

  int update(ActionInfo actionInfo);

  int[] update(ActionInfo[] actionInfos);

  long getMaxId();
}
