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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.metastore.MetaStoreException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class AggregatingTableAddOpListener implements TableAddOpListener {
  static final Logger LOG = LoggerFactory.getLogger(AggregatingTableAddOpListener.class);

  private final AccessCountTableDeque coarseGrainedTableDeque;
  private final AccessCountTableAggregator tableAggregator;
  private final ExecutorService executorService;
  private final long millisPerGranularity;

  AggregatingTableAddOpListener(
      AccessCountTableDeque deque,
      AccessCountTableAggregator aggregator,
      ExecutorService executorService,
      long millisPerGranularity) {
    this.coarseGrainedTableDeque = deque;
    this.tableAggregator = aggregator;
    this.executorService = executorService;
    this.millisPerGranularity = millisPerGranularity;
  }

  @Override
  public CompletableFuture<?> tableAdded(
      AccessCountTableDeque fineGrainedTableDeque, AccessCountTable table) {
    final AccessCountTable lastCoarseGrainedTable = lastCoarseGrainedTableFor(table.getStartTime());

    // Todo: optimize contains
    if (!coarseGrainedTableDeque.contains(lastCoarseGrainedTable)) {
      final List<AccessCountTable> tablesToAggregate =
          fineGrainedTableDeque.getTables(
              lastCoarseGrainedTable.getStartTime(), lastCoarseGrainedTable.getEndTime());

      if (!tablesToAggregate.isEmpty()) {
        return CompletableFuture.runAsync(
            () -> aggregateTable(lastCoarseGrainedTable, tablesToAggregate), executorService);
      }
    }

    return TableAddOpListener.COMPLETED_FUTURE;
  }

  private void aggregateTable(AccessCountTable lastCoarseGrainedTable,
                              List<AccessCountTable> tablesToAggregate) {
    try {
      tableAggregator.aggregate(lastCoarseGrainedTable, tablesToAggregate);
      coarseGrainedTableDeque.addAndNotifyListener(lastCoarseGrainedTable);
    } catch (MetaStoreException e) {
      LOG.error(
          "Add AccessCount Table {} error",
          lastCoarseGrainedTable.getTableName(), e);
    }
  }

  protected AccessCountTable lastCoarseGrainedTableFor(Long startTime) {
    long lastStart = startTime - (startTime % millisPerGranularity);
    long lastEnd = lastStart + millisPerGranularity;
    return new AccessCountTable(lastStart, lastEnd);
  }
  // Todo: WeekTableListener, MonthTableListener, YearTableListener
}
