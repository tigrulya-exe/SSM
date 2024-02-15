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
package org.smartdata.metastore.dao;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Use deque to accelerate remove operation.
 */
public class AccessCountTableDeque extends ArrayDeque<AccessCountTable> {
  private final TableAddOpListener listener;
  private final TableEvictor tableEvictor;

  public AccessCountTableDeque(TableEvictor tableEvictor) {
    this(tableEvictor, TableAddOpListener.noOp());
  }

  public AccessCountTableDeque(TableEvictor tableEvictor, TableAddOpListener listener) {
    super();
    this.listener = checkNotNull(
        listener, "listener should not be null");
    this.tableEvictor = checkNotNull(
        tableEvictor, "tableEvictor should not be null");
  }

  public void addAndNotifyListener(AccessCountTable table) {
    if (!isEmpty() && table.getEndTime() <= peekLast().getEndTime()) {
      throw new IllegalArgumentException("Overlapping access count table: " + table);
    }

    add(table);

    listener.tableAdded(this, table)
        .thenRun(() -> tableEvictor.evictTables(this, size()));
  }

  public List<AccessCountTable> getTables(Long start, Long end) {
    List<AccessCountTable> results = new ArrayList<>();
    for (AccessCountTable table : this) {
      if (table.getStartTime() >= start && table.getEndTime() <= end) {
        results.add(table);
      }
    }
    return results;
  }
}
