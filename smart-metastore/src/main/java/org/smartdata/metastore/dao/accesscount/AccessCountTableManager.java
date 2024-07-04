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
package org.smartdata.metastore.dao.accesscount;

import lombok.Getter;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.metastore.transaction.TransactionRunner;
import org.smartdata.metastore.utils.TimeGranularity;
import org.smartdata.model.FileAccessInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_KEY;
import static org.smartdata.metastore.utils.TimeGranularity.decreaseGranularity;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Getter
public class AccessCountTableManager {
  private final InMemoryAccessEventAggregator accessEventAggregator;
  private final InMemoryAccessCountTableManager inMemoryTableManager;
  private final DbAccessCountTableManager dbTableManager;
  private final TransactionRunner transactionRunner;
  private final int defaultHotFilesLimit;

  public static final Logger LOG =
      LoggerFactory.getLogger(AccessCountTableManager.class);

  public AccessCountTableManager(MetaStore metaStore,
                                 ExecutorService service,
                                 Configuration configuration) {
    int aggregationIntervalMs = configuration.getInt(
        SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS,
        SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT);
    AccessCountEventAggregatorFailover.Strategy eventAggregatorFailoverStrategy =
        configuration.getEnum(SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_KEY,
            AccessCountEventAggregatorFailover.Strategy.SUBMIT_NEW_FAILED_EVENTS_LATER);

    this.accessEventAggregator = new InMemoryAccessEventAggregator(
        metaStore.fileInfoDao(),
        this::onAggregationWindowFinish,
        AccessCountEventAggregatorFailover.of(eventAggregatorFailoverStrategy),
        aggregationIntervalMs);

    this.transactionRunner = new TransactionRunner(metaStore.transactionManager());
    transactionRunner.setIsolationLevel(SERIALIZABLE);

    this.dbTableManager = new DbAccessCountTableManager(
        metaStore.getDataSource(),
        transactionRunner,
        metaStore.accessCountTableDao(),
        metaStore.accessCountEventDao(),
        metaStore.cacheFileDao());
    this.inMemoryTableManager = new InMemoryAccessCountTableManager(
        dbTableManager, service, configuration);
    this.defaultHotFilesLimit = configuration.getInt(
        SmartConfKeys.SMART_TOP_HOT_FILES_NUM_KEY,
        SmartConfKeys.SMART_TOP_HOT_FILES_NUM_DEFAULT);

    recoverTables();
  }

  // todo ADH-4496 replace with searchable
  public List<FileAccessInfo> getHotFiles(long intervalMillis, int fileLimit)
      throws MetaStoreException {
    int realFileLimit = fileLimit == 0 ? defaultHotFilesLimit : fileLimit;

    return transactionRunner.inTransaction(() -> {
      List<AccessCountTable> tables = getTablesForLast(intervalMillis);
      return dbTableManager.getHotFiles(tables, realFileLimit);
    });
  }

  public List<AccessCountTable> getTablesForLast(long intervalMillis) throws MetaStoreException {
    Deque<AccessCountTable> secondDeque =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.SECOND);
    if (secondDeque.isEmpty()) {
      return Collections.emptyList();
    }

    long endTime = secondDeque.getLast().getEndTime();
    return getTablesForLast(endTime - intervalMillis, endTime);
  }

  public void createTable(AccessCountTable table) throws MetaStoreException {
    try {
      transactionRunner.inTransaction(() -> {
        dbTableManager.createTable(table);
        inMemoryTableManager.addTable(table);
      });
    } catch (MetaStoreException exception) {
      LOG.error("Error creating access count table {}", table, exception);
      throw exception;
    }
  }

  private void onAggregationWindowFinish(long windowStart, long windowEnd,
      Collection<AggregatedAccessCounts> aggregatedAccessCounts) {
    if (aggregatedAccessCounts.isEmpty()) {
      return;
    }

    AccessCountTable table = new AccessCountTable(windowStart, windowEnd);
    try {
      transactionRunner.inTransaction(() -> {
        createTable(table);
        dbTableManager.handleAggregatedEvents(table, aggregatedAccessCounts);
      });
    } catch (MetaStoreException exception) {
      LOG.error("Error creating access count table {}", table, exception);
      throw new RuntimeException(exception);
    }
  }

  private void recoverTables() {
    try {
      transactionRunner.inTransaction(() -> {
        List<AccessCountTable> tables = dbTableManager.getTables();
        inMemoryTableManager.recoverTables(tables);
      });
    } catch (MetaStoreException exception) {
      LOG.error("Error recovering existing access count tables", exception);
    }
  }

  private List<AccessCountTable> getTablesForLast(
      long startTime, long endTime) throws MetaStoreException {

    List<AccessCountTable> results = new ArrayList<>();
    TimeGranularity searchIntervalGranularity = TimeGranularity.of(endTime - startTime);
    do {
      // Here we assume that tables are sorted by time.
      for (AccessCountTable table : inMemoryTableManager.getTablesOfGranularity(
          searchIntervalGranularity)) {

        // skip interval if it ends before specified search start time
        if (table.getEndTime() <= startTime) {
          continue;
        }

        // the access count table interval is entirely in the search interval
        if (table.getStartTime() >= startTime) {
          results.add(table);
          startTime = table.getEndTime();
          continue;
        }

        // The access count table interval intersects with the search interval
        if (!inMemoryTableManager.tableExists(startTime, table.getEndTime())) {
          // Create ephemeral table with file access events whose last access time
          // is greater than or equal to the startTime. We also assume that file accesses
          // occurred evenly over time, so we can simply divide the number of file accesses
          // during the table interval by the ratio of the search interval and the table interval
          AccessCountTable partialTable = new AccessCountTable(startTime, table.getEndTime(), true);
          dbTableManager.createPartialTable(partialTable, table);
          results.add(partialTable);
          startTime = table.getEndTime();
        }
      }

      searchIntervalGranularity = decreaseGranularity(searchIntervalGranularity);
    } while (startTime != endTime && searchIntervalGranularity != null);

    return results;
  }
}
