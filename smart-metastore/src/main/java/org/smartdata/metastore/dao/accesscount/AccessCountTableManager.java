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
import org.smartdata.metastore.dao.Searchable;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.metastore.model.SearchResult;
import org.smartdata.metastore.queries.PageRequest;
import org.smartdata.metastore.queries.sort.FileAccessInfoSortField;
import org.smartdata.metastore.transaction.TransactionRunner;
import org.smartdata.metastore.utils.TimeGranularity;
import org.smartdata.model.FileAccessInfo;
import org.smartdata.model.TimeInterval;
import org.smartdata.model.request.FileAccessInfoSearchRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATOR_FAILOVER_KEY;
import static org.smartdata.metastore.utils.TimeGranularity.decreaseGranularity;
import static org.smartdata.metastore.utils.TimeGranularity.increaseGranularity;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Getter
public class AccessCountTableManager implements
    Searchable<FileAccessInfoSearchRequest, FileAccessInfo, FileAccessInfoSortField> {
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

  public List<AccessCountTable> getTablesForLast(long intervalMillis) throws MetaStoreException {
    Deque<AccessCountTable> secondDeque =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.SECOND);
    if (secondDeque.isEmpty()) {
      return Collections.emptyList();
    }

    long endTime = secondDeque.getLast().getEndTime();
    return getAccessCountTables(endTime - intervalMillis, endTime);
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

  private void onAggregationWindowFinish(
      long windowStart,
      long windowEnd,
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

  private List<AccessCountTable> getAccessCountTables(long startTime,
                                                      long endTime) throws MetaStoreException {
    return getAccessCountTables(startTime, endTime, null);
  }

  private List<AccessCountTable> getAccessCountTables(long startTime,
                                                      long endTime,
                                                      AccessCountTable parentTable)
      throws MetaStoreException {
    List<AccessCountTable> result = new ArrayList<>();
    final TimeGranularity startGranularity = TimeGranularity.of(endTime - startTime);
    TimeGranularity searchIntervalGranularity = startGranularity;
    boolean decreaseOnly = false;
    do {
      long finalStartTime = startTime;
      Collection<AccessCountTable> tables = inMemoryTableManager.getTablesOfGranularity(
              searchIntervalGranularity).stream()
          .filter(t -> t.getStartTime() <= endTime)
          .collect(Collectors.toList());
      boolean foundTables = !tables.isEmpty();
      if (parentTable == null && !decreaseOnly) {
        //when we didn't find any tables of current granularity,
        // we have to try to find tables of bigger granularity
        while (!foundTables) {
          searchIntervalGranularity = increaseGranularity(searchIntervalGranularity);
          if (searchIntervalGranularity == null) {
            tables = Collections.emptyList();
            searchIntervalGranularity = startGranularity;
            foundTables = true;
          } else {
            tables = inMemoryTableManager.getTablesOfGranularity(
                searchIntervalGranularity);
            Long accessCountTableStartTime = tables.stream()
                .findFirst()
                .map(AccessCountTable::getStartTime)
                .orElse(Long.MAX_VALUE);
            foundTables = accessCountTableStartTime < startTime;
          }
        }
        decreaseOnly = true;
      }
      tables = tables.stream()
          .filter(t -> {
            if (parentTable != null) {
              //include child tables which has endTime after startTime
              // and includes into parent table
              return t.getEndTime() > finalStartTime && t.getEndTime() <= parentTable.getEndTime();
            }
            return t.getEndTime() > finalStartTime;
          })
          .collect(Collectors.toList());
      int n = 0;
      for (AccessCountTable table : tables) {
        n++;
        // skip tables if it ends before specified search start time
        if (table.getEndTime() < startTime) {
          continue;
        }
        //skip tables if it starts after specified startTime
        if (table.getStartTime() > endTime) {
          continue;
        }
        if (table.getStartTime() <= startTime) {
          if (table.getStartTime() == startTime) {
            if (table.getEndTime() <= endTime) {
              result.add(table);
              startTime = table.getEndTime();
            } else {
              //table.endTime > endTime
              if (searchIntervalGranularity != TimeGranularity.SECOND) {
                List<AccessCountTable> childTables =
                    getAccessCountTables(startTime, endTime, table);
                result.addAll(childTables);
              } else {
                addPartialTable(table, table.getStartTime(), endTime)
                    .ifPresent(result::add);
              }
              startTime = endTime;
            }
          } else {
            if (table.getEndTime() <= endTime) {
              if (searchIntervalGranularity != TimeGranularity.SECOND) {
                List<AccessCountTable> childTables =
                    getAccessCountTables(startTime, table.getEndTime(), table);
                result.addAll(childTables);
              } else {
                addPartialTable(table, startTime, table.getEndTime())
                    .ifPresent(result::add);
              }
              startTime = table.getEndTime();
            } else {
              if (searchIntervalGranularity != TimeGranularity.SECOND) {
                List<AccessCountTable> childTables =
                    getAccessCountTables(startTime, endTime, table);
                result.addAll(childTables);
              } else {
                addPartialTable(table, startTime, endTime)
                    .ifPresent(result::add);
              }
              startTime = endTime;
            }
          }
          continue;
        }
        //table.startTime > startTime
        if (table.getEndTime() <= endTime) {
          //if table is child and the first, we have to create partial table for this interval
          if (parentTable != null && n <= 1) {
            //create partial table based on parent table from startTime to table.startTime
            addPartialTable(parentTable, startTime, table.getStartTime())
                .ifPresent(result::add);
          }
          result.add(table);
          startTime = table.getEndTime();
        } else {
          addPartialTable(table, table.getStartTime(), endTime)
              .ifPresent(result::add);
          startTime = endTime;
        }
      }
      searchIntervalGranularity = decreaseGranularity(searchIntervalGranularity);
      if (searchIntervalGranularity == null && startTime != endTime) {
        if (parentTable != null) {
          //create partial table based on parent table from startTime to endTime
          addPartialTable(parentTable, startTime, endTime)
              .ifPresent(result::add);
        }
        startTime = endTime;
      }
    } while (startTime != endTime);
    return result;
  }

  private Optional<AccessCountTable> addPartialTable(AccessCountTable sourceTable,
                                                     long startTime,
                                                     long endTime) throws MetaStoreException {
    // The access count table interval intersects with the search interval
    if (!inMemoryTableManager.tableExists(startTime, endTime)) {
      // Create ephemeral table with file access events whose last access time
      // is greater than or equal to the startTime. We also assume that file accesses
      // occurred evenly over time, so we can simply divide the number of file accesses
      // during the table interval by the ratio of the search interval and the table interval
      AccessCountTable partialTable =
          new AccessCountTable(startTime, endTime, true);
      dbTableManager.createPartialTable(partialTable, sourceTable);
      return Optional.of(partialTable);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public SearchResult<FileAccessInfo> search(FileAccessInfoSearchRequest searchRequest,
                                             PageRequest<FileAccessInfoSortField> pageRequest) {
    try {
      return transactionRunner.inTransaction(() -> {
        Set<String> tables = getAccessCountTables(searchRequest.getLastAccessedTime());
        if (tables.isEmpty()) {
          return SearchResult.of(Collections.emptyList(), 0);
        }
        return dbTableManager.getFileAccessInfoList(searchRequest.withAccessCountTables(tables),
            pageRequest);
      });
    } catch (MetaStoreException e) {
      LOG.error("Failed to get file access count information with pagination", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<FileAccessInfo> search(FileAccessInfoSearchRequest searchRequest) {
    try {
      return transactionRunner.inTransaction(() -> {
        Set<String> tables = getAccessCountTables(searchRequest.getLastAccessedTime());
        if (tables.isEmpty()) {
          return Collections.emptyList();
        }
        return dbTableManager.getFileAccessInfoList(searchRequest.withAccessCountTables(tables));
      });
    } catch (MetaStoreException e) {
      LOG.error("Failed to get file access count information", e);
      throw new RuntimeException(e);
    }
  }

  private Set<String> getAccessCountTables(TimeInterval timeInterval) throws MetaStoreException {
    long startTime = (timeInterval != null && timeInterval.getFrom() != null)
        ? timeInterval.getFrom().toEpochMilli() : 0L;
    long endTime = (timeInterval != null && timeInterval.getTo() != null)
        ? timeInterval.getTo().toEpochMilli() : System.currentTimeMillis();
    return getAccessCountTables(startTime, endTime).stream()
        .map(AccessCountTable::getTableName)
        .collect(Collectors.toSet());
  }
}
