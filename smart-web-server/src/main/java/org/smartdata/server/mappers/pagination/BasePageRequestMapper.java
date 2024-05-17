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
package org.smartdata.server.mappers.pagination;

import org.mapstruct.Mapping;
import org.smartdata.metastore.queries.PageRequest;
import org.smartdata.metastore.queries.Sorting;
import org.smartdata.server.generated.model.PageRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BasePageRequestMapper<T extends Enum<T>> {

  String DESC_SORT_COLUMN_PREFIX = "-";

  @Mapping(target = "sortByAsc", ignore = true)
  @Mapping(target = "sortByDesc", ignore = true)
  PageRequest toPageRequest(PageRequestDto dto, List<T> sortColumns);

  default List<Sorting> toSortings(List<T> sortColumns) {
    return sortColumns == null
        ? Collections.emptyList()
        : sortColumns
        .stream()
        .flatMap(this::toSortings)
        .collect(Collectors.toList());
  }

  // returns stream in order to let child mappers
  // create several db sortings from one logical
  default Stream<Sorting> toSortings(T sortColumnEnum) {
    String rawSortColumn = sortColumnEnum.toString();
    String entityProperty = sortColumnToEntityProperty(sortColumnEnum);
    Sorting sorting = rawSortColumn.startsWith(DESC_SORT_COLUMN_PREFIX)
        ? new Sorting(entityProperty, Sorting.Order.DESC)
        : new Sorting(entityProperty, Sorting.Order.ASC);

    return Stream.of(sorting);
  }

  default String sortColumnToEntityProperty(T sortColumn) {
    String rawColumnName = sortColumn.toString();
    return rawColumnName.startsWith(DESC_SORT_COLUMN_PREFIX)
        ? rawColumnName.substring(1)
        : rawColumnName;
  }
}
