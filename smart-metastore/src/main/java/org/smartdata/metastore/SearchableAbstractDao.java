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
package org.smartdata.metastore;

import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.Searchable;
import org.smartdata.metastore.model.SearchResult;
import org.smartdata.metastore.queries.MetastoreQuery;
import org.smartdata.metastore.queries.MetastoreQueryExecutor;
import org.smartdata.metastore.queries.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class SearchableAbstractDao<RequestT, EntityT>
    extends AbstractDao
    implements Searchable<RequestT, EntityT> {
  protected final MetastoreQueryExecutor queryExecutor;

  public SearchableAbstractDao(
      DataSource dataSource,
      PlatformTransactionManager transactionManager,
      String tableName) {
    super(dataSource, tableName);

    this.queryExecutor = new MetastoreQueryExecutor(dataSource, transactionManager);
  }

  @Override
  public SearchResult<EntityT> search(RequestT searchRequest, PageRequest pageRequest) {
    MetastoreQuery query = searchQuery(searchRequest).withPagination(pageRequest);
    return queryExecutor.executePaged(query, this::mapRow);
  }

  @Override
  public List<EntityT> search(RequestT searchRequest) {
    return queryExecutor.execute(searchQuery(searchRequest), this::mapRow);
  }

  public Optional<EntityT> searchSingle(RequestT searchRequest) {
    return Optional.ofNullable(search(searchRequest))
        .filter(entities -> !entities.isEmpty())
        .map(entities -> entities.get(0));
  }

  public long count(RequestT searchRequest) {
    return queryExecutor.executeCount(searchQuery(searchRequest));
  }

  protected abstract MetastoreQuery searchQuery(RequestT searchRequest);

  protected abstract EntityT mapRow(ResultSet rs, int rowNum) throws SQLException;
}
