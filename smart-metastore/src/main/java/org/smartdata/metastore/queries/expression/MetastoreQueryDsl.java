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
package org.smartdata.metastore.queries.expression;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DSL for convenient writing of SQL queries with multiple
 * (possibly optional) filters. if null value is provided
 * to any of the operator constructor methods, then such operator will be ignored.
 * */
public class MetastoreQueryDsl {

  private static final MetastoreQueryExpression EMPTY_EXPRESSION = () -> "";

  public static MetastoreQueryExpression or(MetastoreQueryExpression... expressions) {
    return operator("OR", expressions);
  }

  public static MetastoreQueryExpression and(MetastoreQueryExpression... expressions) {
    return operator("AND", expressions);
  }

  public static <T> MetastoreQueryExpression greaterThan(String column, T value) {
    return binaryOpWithPlaceholder(">", column, value);
  }

  public static <T> MetastoreQueryExpression greaterThanEqual(String column, T value) {
    return binaryOpWithPlaceholder(">=", column, value);
  }

  public static <T> MetastoreQueryExpression lessThan(String column, T value) {
    return binaryOpWithPlaceholder("<", column, value);
  }

  public static <T> MetastoreQueryExpression lessThanEqual(String column, T value) {
    return binaryOpWithPlaceholder("<=", column, value);
  }

  public static <T> MetastoreQueryExpression equal(String column, T value) {
    return binaryOpWithPlaceholder("=", column, value);
  }

  public static <T> MetastoreQueryExpression in(String column, List<T> values) {
    return binaryOpWithPlaceholder("IN", column, values);
  }

  public static <T> MetastoreQueryExpression inStrings(String column, List<T> values) {
    if (values == null) {
      return EMPTY_EXPRESSION;
    }

    List<String> strValues = values.stream()
        .map(Object::toString)
        .collect(Collectors.toList());

    return in(column, strValues);
  }

  public static <T> MetastoreQueryExpression like(String column, T value) {
    return Optional.ofNullable(value)
        .map(val -> binaryOpWithPlaceholder("LIKE", column, "%" + val + "%"))
        .orElse(EMPTY_EXPRESSION);
  }

  private static MetastoreQueryExpression operator(
      String operator, MetastoreQueryExpression... expressions) {
    List<MetastoreQueryExpression> nonEmptyExpressions = Arrays.stream(expressions)
        .filter(expression -> !EMPTY_EXPRESSION.equals(expression))
        .collect(Collectors.toList());

    if (nonEmptyExpressions.isEmpty()) {
      return EMPTY_EXPRESSION;
    }

    return new MetastoreQueryOperator(operator, nonEmptyExpressions);
  }

  private static <T> MetastoreQueryExpression binaryOpWithPlaceholder(
      String operator, String column, T value) {
    if (value == null) {
      return EMPTY_EXPRESSION;
    }

    return new MetastoreQueryOperator(operator,
        Arrays.asList(
            () -> column,
            new MetastoreQueryPlaceholder<>(column, value)
        )
    );
  }
}
