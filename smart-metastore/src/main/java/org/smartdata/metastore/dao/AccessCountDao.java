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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AccessCountDao {
  String FILE_FIELD = "fid";
  String ACCESSCOUNT_FIELD = "count";

  static String createAccessCountTableSQL(String tableName) {
    return String.format(
        "CREATE TABLE %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL)",
        tableName, FILE_FIELD, ACCESSCOUNT_FIELD);
  }

  void insert(AccessCountTable accessCountTable);

  void insert(AccessCountTable[] accessCountTables);

  List<AccessCountTable> getAccessCountTableByName(String name);

  void delete(Long startTime, Long endTime);

  void delete(AccessCountTable table);

  List<AccessCountTable> getAllSortedTables();

  void aggregateTables(
      AccessCountTable destinationTable, List<AccessCountTable> tablesToAggregate);

  Map<Long, Integer> getHotFiles(List<AccessCountTable> tables, int topNum)
      throws SQLException;

  void createProportionTable(AccessCountTable dest, AccessCountTable source)
      throws SQLException;

  void updateFid(long fidSrc, long fidDest) throws SQLException;
}
