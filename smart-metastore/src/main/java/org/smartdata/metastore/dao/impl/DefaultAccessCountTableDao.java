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

import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.accesscount.AccessCountTableDao;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.queries.MetastoreQuery;
import org.smartdata.metastore.queries.MetastoreQueryExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smartdata.metastore.queries.MetastoreQuery.select;
import static org.smartdata.metastore.queries.MetastoreQuery.selectAll;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.equal;
import static org.smartdata.metastore.queries.sort.Sorting.ascending;

public class DefaultAccessCountTableDao extends AbstractDao implements AccessCountTableDao {

  protected final MetastoreQueryExecutor queryExecutor;

  public DefaultAccessCountTableDao(
      DataSource dataSource, PlatformTransactionManager transactionManager) {
    super(dataSource, TABLE_NAME);

    this.queryExecutor = new MetastoreQueryExecutor(dataSource, transactionManager);
  }

  @Override
  public void insert(AccessCountTable table) {
    if (!tableExists(table.getTableName())) {
      insert(table, this::toMap);
    }
  }

  @Override
  public boolean tableExists(String name) {
    MetastoreQuery query = select("1")
        .from(TABLE_NAME)
        .where(
            equal("table_name", name)
        )
        .limit(1L);

    return queryExecutor.executeCount(query) != 0;
  }

  @Override
  public void delete(AccessCountTable table) {
    final String sql = "DELETE FROM access_count_table WHERE table_name = ?";
    jdbcTemplate.update(sql, table.getTableName());
  }

  @Override
  public List<AccessCountTable> getAllSortedTables() {
    MetastoreQuery query = selectAll()
        .from(TABLE_NAME)
        .orderBy(ascending("start_time"));
    return queryExecutor.execute(query, this::mapRow);
  }

  private Map<String, Object> toMap(AccessCountTable accessCountTable) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("table_name", accessCountTable.getTableName());
    parameters.put("start_time", accessCountTable.getStartTime());
    parameters.put("end_time", accessCountTable.getEndTime());
    return parameters;
  }

  private AccessCountTable mapRow(ResultSet resultSet, int i) throws SQLException {
    return new AccessCountTable(
        resultSet.getLong("start_time"),
        resultSet.getLong("end_time"));
  }
}
