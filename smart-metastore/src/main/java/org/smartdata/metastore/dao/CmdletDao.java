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

import org.smartdata.model.CmdletInfo;
import org.smartdata.model.CmdletState;

import java.util.List;

public interface CmdletDao {
  List<CmdletInfo> getAll();

  List<CmdletInfo> getAPageOfCmdlet(long start, long offset,
                                    List<String> orderBy, List<Boolean> isDesc);

  List<CmdletInfo> getAPageOfCmdlet(long start, long offset);

  List<CmdletInfo> getByIds(List<Long> aids);

  CmdletInfo getById(long cid);

  List<CmdletInfo> getByRid(long rid);

  long getNumByRid(long rid);

  List<CmdletInfo> getByRid(long rid, long start, long offset);

  List<CmdletInfo> getByRid(long rid, long start, long offset,
                            List<String> orderBy, List<Boolean> isDesc);

  List<CmdletInfo> getByState(CmdletState state);

  int getNumCmdletsInTerminiatedStates();

  List<CmdletInfo> getByCondition(
      String cidCondition, String ridCondition, CmdletState state);

  void delete(long cid);

  int[] batchDelete(List<Long> cids);

  int deleteBeforeTime(long timestamp);

  int deleteKeepNewCmd(long num);

  void deleteAll();

  void insert(CmdletInfo cmdletInfo);

  void insert(CmdletInfo[] cmdletInfos);

  int[] replace(CmdletInfo[] cmdletInfos);

  int update(long cid, int state);

  int update(long cid, String parameters, int state);

  int update(CmdletInfo cmdletInfo);

  int[] update(List<CmdletInfo> cmdletInfos);

  long getMaxId();
}
