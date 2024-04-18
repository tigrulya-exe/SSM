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
package org.smartdata.metastore.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchResult<T> {
  private final List<T> items;
  private final long total;

  private SearchResult(List<T> items, long total) {
    this.items = items;
    this.total = total;
  }

  public List<T> getItems() {
    return items;
  }

  public long getTotal() {
    return total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResult<?> that = (SearchResult<?>) o;
    return total == that.total && Objects.equals(items, that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, total);
  }

  @Override
  public String toString() {
    return "SearchResult{"
        + "items=" + items
        + ", total=" + total
        + '}';
  }

  public static <T> SearchResult<T> of(List<T> items, long total) {
    return new SearchResult<>(items, total);
  }

  public static <T> SearchResult<T> emptyResult() {
    return new SearchResult<>(Collections.emptyList(), 0);
  }
}
