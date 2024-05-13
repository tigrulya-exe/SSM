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
import org.smartdata.metastore.queries.sort.SortField;
import org.smartdata.metastore.queries.sort.Sorting;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SearchableAbstractDao<RequestT, EntityT, ColumnT extends SortField>
    extends AbstractDao
    implements Searchable<RequestT, EntityT, ColumnT> {
  protected final MetastoreQueryExecutor queryExecutor;

  public SearchableAbstractDao(
      DataSource dataSource,
      PlatformTransactionManager transactionManager,
      String tableName) {
    super(dataSource, tableName);

    this.queryExecutor = new MetastoreQueryExecutor(dataSource, transactionManager);
  }

  @Override
  public SearchResult<EntityT> search(RequestT searchRequest, PageRequest<ColumnT> pageRequest) {
    MetastoreQuery query = searchQuery(searchRequest)
        .withPagination(toRawPageRequest(pageRequest));
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

  private PageRequest<String> toRawPageRequest(PageRequest<ColumnT> pageRequest) {
    if (pageRequest == null) {
      return null;
    }

    List<Sorting<String>> rawSortings =
        Optional.ofNullable(pageRequest.getSortColumns())
            .map(this::toRawSortings)
            .orElse(null);

    return new PageRequest<>(
        pageRequest.getOffset(),
        pageRequest.getLimit(),
        rawSortings);
  }

  private List<Sorting<String>> toRawSortings(
      List<Sorting<ColumnT>> sortColumns) {
    return sortColumns == null
        ? null
        : sortColumns.stream()
        .flatMap(sorting -> toDbColumnSortings(
            sorting.getColumn(), sorting.getOrder()))
        .collect(Collectors.toList());
  }

  // returns Stream in order to allow daos to
  // map one logical sorting field to several physical ones
  protected Stream<Sorting<String>> toDbColumnSortings(
      ColumnT column, Sorting.Order order) {
    return Stream.of(toDbColumnSorting(column, order));
  }

  protected Sorting<String> toDbColumnSorting(
      ColumnT column, Sorting.Order order) {
    return new Sorting<>(column.getFieldName(), order);
  }

  protected abstract MetastoreQuery searchQuery(RequestT searchRequest);

  protected abstract EntityT mapRow(ResultSet rs, int rowNum) throws SQLException;
}
