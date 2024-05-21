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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.smartdata.metastore.queries.sort.Sorting;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PageRequest<T> {
  private final Long offset;
  private final Integer limit;

  private final List<Sorting<T>> sortColumns;

  public static class Builder<T> {
    private List<Sorting<T>> sortColumns = new ArrayList<>();

    public Builder<T> sortByAsc(T column) {
      return addSorting(column, Sorting.Order.ASC);
    }

    public Builder<T> sortByDesc(T column) {
      return addSorting(column, Sorting.Order.DESC);
    }

    public Builder<T> addSorting(T column, Sorting.Order order) {
      this.sortColumns.add(new Sorting<>(column, order));
      return this;
    }
  }
}
