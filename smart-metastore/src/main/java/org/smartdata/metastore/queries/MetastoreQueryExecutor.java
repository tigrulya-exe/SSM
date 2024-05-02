/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.metastore.queries;

import org.smartdata.metastore.model.SearchResult;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

public class MetastoreQueryExecutor {
  private final NamedParameterJdbcTemplate namedJdbcTemplate;
  private final TransactionTemplate transactionTemplate;

  public MetastoreQueryExecutor(
      DataSource dataSource,
      PlatformTransactionManager transactionManager) {
    this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  public <T> SearchResult<T> executePaged(
      MetastoreQuery query, RowMapper<T> rowMapper) {
    return transactionTemplate.execute(
        status -> executePagedTransaction(query, rowMapper));
  }

  public <T> List<T> execute(MetastoreQuery query, RowMapper<T> rowMapper) {
    return namedJdbcTemplate.query(
        query.toSqlQuery(), query.getParameters(), rowMapper);
  }

  private <T> SearchResult<T> executePagedTransaction(
      MetastoreQuery query, RowMapper<T> rowMapper) {
    List<T> items = execute(query, rowMapper);
    Long total = namedJdbcTemplate.queryForObject(
        query.toSqlCountQuery(), query.getParameters(), Long.class);
    return SearchResult.of(items, Optional.ofNullable(total).orElse(0L));
  }
}
