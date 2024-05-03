/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.smartdata.metastore.queries.PageRequest;
import org.smartdata.metastore.queries.Sorting;
import org.smartdata.server.generated.model.PageRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PageRequestMapper {

  String DESC_SORT_COLUMN_PREFIX = "-";

  @Mapping(source = "sort", target = "sortColumns")
  @Mapping(target = "sortByAsc", ignore = true)
  @Mapping(target = "sortByDesc", ignore = true)
  PageRequest toPageRequest(PageRequestDto dto);

  default Sorting toSorting(String sortColumn) {
    return sortColumn.startsWith(DESC_SORT_COLUMN_PREFIX)
        ? new Sorting(sortColumn.substring(1), Sorting.Order.DESC)
        : new Sorting(sortColumn, Sorting.Order.ASC);
  }
}
