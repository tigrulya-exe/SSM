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

import java.util.ArrayList;
import java.util.List;

public class PageRequest {
  private final Long offset;
  private final Integer limit;

  private final List<Sorting> sortColumns;

  public PageRequest(Long offset, Integer limit, List<Sorting> sortColumns) {
    this.offset = offset;
    this.limit = limit;
    this.sortColumns = sortColumns;
  }

  public Long getOffset() {
    return offset;
  }

  public Integer getLimit() {
    return limit;
  }

  public List<Sorting> getSortColumns() {
    return sortColumns;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long offset;
    private Integer limit;
    private List<Sorting> sortColumns;

    public Builder() {
      this.sortColumns = new ArrayList<>();
    }

    public Builder offset(Long offset) {
      this.offset = offset;
      return this;
    }

    public Builder limit(Integer limit) {
      this.limit = limit;
      return this;
    }

    public Builder sortByAsc(String column) {
      return addSorting(column, Sorting.Order.ASC);
    }

    public Builder sortByDesc(String column) {
      return addSorting(column, Sorting.Order.DESC);
    }

    public Builder sortColumns(List<Sorting> sortColumns) {
      this.sortColumns = sortColumns;
      return this;
    }

    public Builder addSorting(String column, Sorting.Order order) {
      this.sortColumns.add(new Sorting(column, order));
      return this;
    }

    public PageRequest build() {
      return new PageRequest(offset, limit, sortColumns);
    }
  }
}
