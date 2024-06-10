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
package org.smartdata.metastore.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.FileInfoDao;
import org.smartdata.metastore.dao.accesscount.AccessCountEventDao;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.model.FileAccessInfo;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultAccessCountEventDao
    extends AbstractDao
    implements AccessCountEventDao {

  private static final String TABLE_NAME = "access_count_table";

  public DefaultAccessCountEventDao(DataSource dataSource) {
    super(dataSource, TABLE_NAME);
  }

  @Override
  public void insert(AccessCountTable table,
                     Collection<AggregatedAccessCounts> aggregatedAccessCounts) {
    insert(simpleJdbcInsert(table), aggregatedAccessCounts, this::toMap);
  }

  protected Map<String, Object> toMap(AggregatedAccessCounts accessCounts) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(AccessCountEventDao.FILE_ID_FIELD, accessCounts.getFileId());
    parameters.put(AccessCountEventDao.ACCESS_COUNT_FIELD, accessCounts.getAccessCount());
    parameters.put(AccessCountEventDao.LAST_ACCESSED_TIME_FIELD,
        accessCounts.getLastAccessedTimestamp());
    return parameters;
  }

  // todo ADH-4496 replace with searchable
  @Override
  public List<FileAccessInfo> getHotFiles(List<AccessCountTable> tables, int topNum) {
    if (CollectionUtils.isEmpty(tables)) {
      return Collections.emptyList();
    }

    String statement = hotFilesQueryString(tables)
        + " ORDER BY count DESC, access_counts.fid ASC LIMIT " + topNum;
    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(statement);

    List<FileAccessInfo> accessCounts = new ArrayList<>();
    while (sqlRowSet.next()) {
      accessCounts.add(
          new FileAccessInfo(
              sqlRowSet.getLong(AccessCountEventDao.FILE_ID_FIELD),
              sqlRowSet.getString(FileInfoDao.FILE_PATH_FIELD),
              sqlRowSet.getInt(AccessCountEventDao.ACCESS_COUNT_FIELD),
              sqlRowSet.getLong(AccessCountEventDao.LAST_ACCESSED_TIME_FIELD)));
    }
    return accessCounts;
  }

  @Override
  public void updateFileIds(List<AccessCountTable> accessCountTables,
                            long srcFileId, long destFileId) throws MetaStoreException {
    Exception lastException = null;
    int failedNum = 0;

    for (AccessCountTable table : accessCountTables) {
      String statement = "UPDATE " + table.getTableName()
          + " SET fid = " + destFileId
          + " WHERE fid = " + srcFileId;

      try {
        jdbcTemplate.execute(statement);
      } catch (Exception exception) {
        lastException = exception;
        failedNum++;
      }
    }
    // Otherwise, ignore the exception because table evictor can evict access
    // count tables, which are not synchronized. Even so, there is no impact on
    // the measurement for data temperature.
    if (failedNum == accessCountTables.size()) {
      // Throw exception if all tables are not updated.
      throw new MetaStoreException("Failed to update fid!", lastException);
    }
  }

  private String hotFilesQueryString(List<AccessCountTable> tables) {
    return "SELECT access_counts.fid, SUM(access_counts.count) AS count, "
        + "MAX(access_counts.last_accessed_time) as last_accessed_time, file.path "
        + "FROM (" + AccessCountEventDao.unionTablesQuery(tables) + ") access_counts "
        + "JOIN file ON access_counts.fid = file.fid "
        + "GROUP BY access_counts.fid, file.path";
  }

  private SimpleJdbcInsert simpleJdbcInsert(AccessCountTable table) {
    return new SimpleJdbcInsert(dataSource)
        .withTableName(table.getTableName());
  }
}
