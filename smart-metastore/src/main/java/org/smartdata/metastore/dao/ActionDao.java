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

import org.smartdata.metastore.model.SearchResult;
import org.smartdata.metastore.queries.sort.ActionSortField;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.request.ActionSearchRequest;

import java.util.List;

public interface ActionDao
    extends Searchable<ActionSearchRequest, ActionInfo, ActionSortField> {
  // todo delete after zeppelin removal
  Long getCountOfAction();

  ActionInfo getById(long aid);

  List<ActionInfo> getByIds(List<Long> aids);

  // todo do we need it at rpc?
  List<ActionInfo> getLatestActions(int size);

  // todo delete after zeppelin removal
  List<ActionInfo> getLatestActions(String actionName, int size);

  // todo delete after zeppelin removal
  List<ActionInfo> getAPageOfAction(long start, long offset, List<String> orderBy,
                                    List<Boolean> isDesc);

  // todo delete after zeppelin removal
  List<ActionInfo> getAPageOfAction(long start, long offset);

  // todo delete after zeppelin removal
  SearchResult<ActionInfo> searchAction(String path, long start, long offset, List<String> orderBy,
                                        List<Boolean> isDesc);

  void delete(long aid);

  void deleteCmdletActions(long cid);

  int[] batchDeleteCmdletActions(List<Long> cids);

  void insert(ActionInfo actionInfo);

  void insert(ActionInfo... actionInfos);

  void upsert(List<ActionInfo> actionInfos);

  int update(ActionInfo actionInfo);

  int[] update(ActionInfo[] actionInfos);

  long getMaxId();
}
