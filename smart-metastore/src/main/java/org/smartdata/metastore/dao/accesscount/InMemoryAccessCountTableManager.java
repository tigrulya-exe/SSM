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

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.utils.TimeGranularity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_DAY_TABLES_TO_KEEP_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_DAY_TABLES_TO_KEEP_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_HOUR_TABLES_TO_KEEP_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_HOUR_TABLES_TO_KEEP_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_HOUR_TABLES_TO_KEEP_MIN;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_MINUTE_TABLES_TO_KEEP_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_MINUTE_TABLES_TO_KEEP_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_MINUTE_TABLES_TO_KEEP_MIN;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_SECOND_TABLES_TO_KEEP_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_NUM_SECOND_TABLES_TO_KEEP_KEY;
import static org.smartdata.metastore.utils.Constants.ONE_MINUTE_IN_MILLIS;

public class InMemoryAccessCountTableManager {
  private final Map<TimeGranularity, AccessCountTableDeque> tableDeques;
  private final Configuration configuration;
  private AccessCountTableDeque secondTableDeque;

  public static final Logger LOG =
      LoggerFactory.getLogger(InMemoryAccessCountTableManager.class);

  public InMemoryAccessCountTableManager(
      AccessCountTableHandler tableHandler,
      ExecutorService executorService,
      Configuration configuration) {
    this.configuration = configuration;
    this.tableDeques = new HashMap<>();

    initTables(tableHandler, executorService);
  }

  public void recoverTables(List<AccessCountTable> tables) {
    if (tables.isEmpty()) {
      LOG.info("No existing access count tables to recover.");
      return;
    }

    LOG.info("Loading existing access count tables: {}", tables);
    for (AccessCountTable table : tables) {
      if (tableDeques.containsKey(table.getGranularity())) {
        tableDeques.get(table.getGranularity()).add(table);
      }
    }

    // aggregate old tables if needed
    AccessCountTable lastOldTable = tables.get(tables.size() - 1);
    Optional.ofNullable(tableDeques.get(lastOldTable.getGranularity()))
        .ifPresent(deque -> deque.notifyListener(lastOldTable));

    LOG.info("Existing access count tables were successfully loaded");
  }

  public void addTable(AccessCountTable accessCountTable) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(accessCountTable.toString());
    }
    secondTableDeque.addAndNotifyListener(accessCountTable);
  }

  public Deque<AccessCountTable> getTablesOfGranularity(TimeGranularity timeGranularity) {
    return Optional.<Deque<AccessCountTable>>ofNullable(
            tableDeques.get(timeGranularity))
        .orElseGet(ArrayDeque::new);
  }

  public boolean tableExists(long start, long end) {
    TimeGranularity granularity = TimeGranularity.of(end - start);
    AccessCountTable fakeTable = new AccessCountTable(start, end);
    return tableDeques.containsKey(granularity)
        && tableDeques.get(granularity).contains(fakeTable);
  }

  private void initTables(
      AccessCountTableHandler tableHandler,
      ExecutorService executorService) {

    TableAddOpListener dayTableListener =
        createPerDayTableListener(tableHandler, executorService);

    TableAddOpListener hourTableListener =
        createPerHourTableListener(tableHandler, executorService, dayTableListener);

    TableAddOpListener minuteTableListener =
        createPerMinuteTableListener(tableHandler, executorService, hourTableListener);

    createSecondTableDeque(tableHandler, minuteTableListener);
  }

  private TableAddOpListener createPerDayTableListener(
      AccessCountTableHandler tableHandler, ExecutorService executorService) {
    int perDayAccessTablesCount = configuration.getInt(
        SMART_NUM_DAY_TABLES_TO_KEEP_KEY,
        SMART_NUM_DAY_TABLES_TO_KEEP_DEFAULT);
    AccessCountTableDeque dayTableDeque = new AccessCountTableDeque(
        new CountTableEvictor(tableHandler, perDayAccessTablesCount));

    tableDeques.put(TimeGranularity.DAY, dayTableDeque);
    return TableAddOpListener.perDay(dayTableDeque, tableHandler, executorService);
  }

  private TableAddOpListener createPerHourTableListener(
      AccessCountTableHandler tableHandler,
      ExecutorService executorService,
      TableAddOpListener dayTableListener) {
    int perHourAccessTablesCount = getAccessTablesCount(
        SMART_NUM_HOUR_TABLES_TO_KEEP_KEY,
        SMART_NUM_HOUR_TABLES_TO_KEEP_DEFAULT,
        SMART_NUM_HOUR_TABLES_TO_KEEP_MIN);
    AccessCountTableDeque hourTableDeque = new AccessCountTableDeque(
        new CountTableEvictor(tableHandler, perHourAccessTablesCount), dayTableListener);

    tableDeques.put(TimeGranularity.HOUR, hourTableDeque);
    return TableAddOpListener.perHour(hourTableDeque, tableHandler, executorService);
  }

  private TableAddOpListener createPerMinuteTableListener(
      AccessCountTableHandler tableHandler,
      ExecutorService executorService,
      TableAddOpListener hourTableListener) {
    int perMinuteAccessTablesCount = getAccessTablesCount(
        SMART_NUM_MINUTE_TABLES_TO_KEEP_KEY,
        SMART_NUM_MINUTE_TABLES_TO_KEEP_DEFAULT,
        SMART_NUM_MINUTE_TABLES_TO_KEEP_MIN);
    AccessCountTableDeque minuteTableDeque = new AccessCountTableDeque(
        new CountTableEvictor(tableHandler, perMinuteAccessTablesCount), hourTableListener);

    tableDeques.put(TimeGranularity.MINUTE, minuteTableDeque);
    return TableAddOpListener.perMinute(minuteTableDeque, tableHandler, executorService);
  }

  private void createSecondTableDeque(
      AccessCountTableHandler tableHandler,
      TableAddOpListener minuteTableListener) {
    int aggregationIntervalMs = configuration.getInt(
        SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS,
        SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT);
    int minimalSecondAccessTablesCount =
        (int) (ONE_MINUTE_IN_MILLIS / aggregationIntervalMs);

    int perSecondAccessTablesCount = getAccessTablesCount(
        SMART_NUM_SECOND_TABLES_TO_KEEP_KEY,
        SMART_NUM_SECOND_TABLES_TO_KEEP_DEFAULT,
        minimalSecondAccessTablesCount);
    this.secondTableDeque = new AccessCountTableDeque(
        new CountTableEvictor(tableHandler, perSecondAccessTablesCount), minuteTableListener);

    tableDeques.put(TimeGranularity.SECOND, secondTableDeque);
  }

  private int getAccessTablesCount(String configKey, int defaultValue, int minimalCount) {
    int tableCount = configuration.getInt(configKey, defaultValue);

    if (tableCount < minimalCount) {
      String errorMessage = String.format(
          "Wrong value for option %s. It should be at least %d", configKey, minimalCount);
      LOG.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    return tableCount;
  }
}
