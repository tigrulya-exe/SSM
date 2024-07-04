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
package org.smartdata.metastore.queries.column;

import org.smartdata.metastore.queries.expression.FilteredCountExpression;
import org.smartdata.metastore.queries.expression.MetastoreQueryExpression;

import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.and;

public class MetastoreQueryColumnDsl {
  private static final MetastoreQueryExpression COUNT_ALL_EXPRESSION =
      () -> "COUNT(*)";

  static MetastoreQueryColumn tableColumn(String columnName) {
    return new SimpleMetastoreQueryColumn(() -> columnName, columnName);
  }

  public static MetastoreQueryColumn countAll(String alias) {
    return new SimpleMetastoreQueryColumn(COUNT_ALL_EXPRESSION, alias);
  }

  public static MetastoreQueryColumn countFiltered(
      String alias, MetastoreQueryExpression... filters) {
    return countFiltered(alias, and(filters));
  }

  public static MetastoreQueryColumn countFiltered(
      String alias, MetastoreQueryExpression filter) {
    return new SimpleMetastoreQueryColumn(
        new FilteredCountExpression(filter), alias);
  }
}
