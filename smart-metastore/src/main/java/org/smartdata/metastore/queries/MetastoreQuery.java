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
package org.smartdata.metastore.queries;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.smartdata.metastore.queries.expression.MetastoreQueryExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.and;

/**
 * Builds SQL SELECT query with named placeholders for later use by NamedJdbcTemplate.
 * */
public class MetastoreQuery {
  private final StringBuilder queryBuilder;
  private final StringBuilder filtersBuilder;
  private final Map<String, Object> parameters;
  private String table;

  private MetastoreQuery(String baseQuery) {
    this.queryBuilder = new StringBuilder();
    this.filtersBuilder = new StringBuilder();
    this.parameters = new HashMap<>();
    this.table = null;

    queryBuilder.append(baseQuery).append("\n");
  }

  public static MetastoreQuery selectAll() {
    return select("*");
  }

  public static MetastoreQuery select(String... fields) {
    return new MetastoreQuery("SELECT " + String.join(", ", fields));
  }

  public MetastoreQuery from(String table) {
    queryBuilder.append("FROM ")
        .append(table)
        .append("\n");
    this.table = table;
    return this;
  }

  public MetastoreQuery where(MetastoreQueryExpression operator) {
    where(operator.build(), operator.getParameters());
    return this;
  }

  private MetastoreQuery where(String filterSql, Map<String, Object> filterParameters) {
    if (StringUtils.isNotBlank(filterSql)) {
      filtersBuilder.append(filterSql);

      queryBuilder.append("WHERE ")
          .append(filtersBuilder)
          .append("\n");

      parameters.putAll(filterParameters);
    }

    return this;
  }

  public MetastoreQuery where(MetastoreQueryExpression... operators) {
    return where(and(operators));
  }

  public MetastoreQuery withPagination(PageRequest pageRequest) {
    if (pageRequest == null) {
      return this;
    }
    orderBy(pageRequest.getSortColumns());

    Optional.ofNullable(pageRequest.getLimit())
        .ifPresent(this::limit);

    Optional.ofNullable(pageRequest.getOffset())
        .ifPresent(this::offset);
    return this;
  }

  public MetastoreQuery limit(long limit) {
    queryBuilder.append("LIMIT ")
        .append(limit)
        .append("\n");
    return this;
  }

  public MetastoreQuery offset(long offset) {
    queryBuilder.append("OFFSET ")
        .append(offset)
        .append("\n");
    return this;
  }

  public MetastoreQuery orderBy(List<Sorting> sortColumns) {
    if (CollectionUtils.isEmpty(sortColumns)) {
      return this;
    }

    queryBuilder.append("ORDER BY ");

    String columnsSql = sortColumns.stream()
        .map(Sorting::toString)
        .collect(Collectors.joining(", "));
    queryBuilder.append(columnsSql).append("\n");

    return this;
  }

  public String toSqlQuery() {
    checkTableSet();
    return queryBuilder.toString();
  }

  public String toSqlCountQuery() {
    checkTableSet();
    return select("COUNT(*)")
        .from(table)
        .where(filtersBuilder.toString(), parameters)
        .toSqlQuery();
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  private void checkTableSet() {
    if (table == null) {
      throw new IllegalStateException("Table not provided");
    }
  }
}
