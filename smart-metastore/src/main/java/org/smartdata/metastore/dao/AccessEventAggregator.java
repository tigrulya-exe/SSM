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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.dao.impl.DefaultAccessCountDao;
import org.smartdata.metrics.FileAccessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccessEventAggregator {
  private final MetaStore adapter;
  private final long aggregationGranularity;
  private final AccessCountTableManager accessCountTableManager;
  private final List<FileAccessEvent> eventBuffer;
  private Window currentWindow;
  private Map<String, Integer> unmergedAccessCounts = new HashMap<>();
  public static final Logger LOG =
      LoggerFactory.getLogger(AccessEventAggregator.class);

  public AccessEventAggregator(MetaStore adapter, AccessCountTableManager manager) {
    this(adapter, manager, 5 * 1000L);
  }

  public AccessEventAggregator(MetaStore adapter,
                               AccessCountTableManager manager, long aggregationGranularity) {
    this.adapter = adapter;
    this.accessCountTableManager = manager;
    this.aggregationGranularity = aggregationGranularity;
    this.eventBuffer = new ArrayList<>();
  }

  public void addAccessEvents(List<FileAccessEvent> eventList) {
    if (currentWindow == null && !eventList.isEmpty()) {
      currentWindow = assignWindow(eventList.get(0).getTimestamp());
    }
    for (FileAccessEvent event : eventList) {
      if (!currentWindow.contains(event.getTimestamp())) {
        // New Window occurs
        createTable();
        currentWindow = assignWindow(event.getTimestamp());
        eventBuffer.clear();
      }
      // Exclude watermark event
      if (!event.getPath().isEmpty()) {
        eventBuffer.add(event);
      }
    }
  }

  private void createTable() {
    AccessCountTable table = new AccessCountTable(currentWindow.start, currentWindow.end);
    String createTable = AccessCountDao.createAccessCountTableSQL(table.getTableName());
    try {
      if (adapter.tableExists(table.getTableName())) {
        adapter.dropTable(table.getTableName());
      }
      adapter.execute(createTable);
      adapter.insertAccessCountTable(table);
    } catch (MetaStoreException e) {
      LOG.error("Create table error: " + table, e);
      return;
    }

    if (!eventBuffer.isEmpty() || !unmergedAccessCounts.isEmpty()) {
      Map<String, Integer> accessCounts = getAccessCountMap(eventBuffer);
      Set<String> accessedFiles = new HashSet<>(accessCounts.keySet());
      mergeMapsInPlace(accessCounts, unmergedAccessCounts);

      final Map<String, Long> pathToIDs;
      try {
        pathToIDs = adapter.getFileIDs(accessCounts.keySet());
      } catch (MetaStoreException e) {
        // TODO: dirty handle here
        LOG.error("Create Table " + table.getTableName(), e);
        return;
      }

      Map<String, Integer> accessCountsNotHandledBySSM = accessedFiles.stream()
              .filter(file -> !pathToIDs.containsKey(file))
              .collect(Collectors.toMap(
                  Function.identity(),
                  accessCounts::get
              ));

      List<String> sqlInsertValues = new ArrayList<>();
      for (String key : pathToIDs.keySet()) {
        sqlInsertValues.add(String.format("(%d, %d)", pathToIDs.get(key),
            accessCounts.get(key)));
      }

      if (LOG.isDebugEnabled() && !unmergedAccessCounts.isEmpty()) {
        Set<String> non = unmergedAccessCounts.keySet();
        non.removeAll(pathToIDs.keySet());
        if (!non.isEmpty()) {
          StringBuilder result = new StringBuilder("Access events ignored for file:\n");
          for (String p : non) {
            result.append(p).append(" --> ").append(unmergedAccessCounts.get(p)).append("\n");
          }
          LOG.debug(result.toString());
        }
      }
      unmergedAccessCounts = accessCountsNotHandledBySSM;

      if (!sqlInsertValues.isEmpty()) {
        String insertValue = String.format(
            "INSERT INTO %s (%s, %s) VALUES %s",
            table.getTableName(),
            DefaultAccessCountDao.FILE_FIELD,
            DefaultAccessCountDao.ACCESSCOUNT_FIELD,
            StringUtils.join(sqlInsertValues, ", "));
        try {
          adapter.execute(insertValue);
          adapter.updateCachedFiles(pathToIDs, eventBuffer);
          if (LOG.isDebugEnabled()) {
            LOG.debug("Table created: " + table);
          }
        } catch (MetaStoreException e) {
          LOG.error("Create table error: " + table, e);
        }
      }
    }
    accessCountTableManager.addTable(table);
  }

  private void mergeMapsInPlace(Map<String, Integer> resultMap, Map<String, Integer> mapToMerge) {
    mapToMerge.forEach((key, value) -> resultMap.merge(key, value, Integer::sum));
  }

  private Map<String, Integer> getAccessCountMap(List<FileAccessEvent> events) {
    return events.stream()
        .collect(Collectors.toMap(
            FileAccessEvent::getPath,
            event -> 1,
            Integer::sum
        ));
  }

  private Window assignWindow(long time) {
    long start = time - (time % aggregationGranularity);
    return new Window(start, start + aggregationGranularity);
  }

  private static class Window {
    private final long start;
    private final long end;

    public Window(long start, long end) {
      this.start = start;
      this.end = end;
    }

    // [start, end)
    public boolean contains(long time) {
      return this.start <= time && this.end > time;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Window)) {
        return false;
      } else {
        Window other = (Window) o;
        return this.start == other.start && this.end == other.end;
      }
    }
  }
}
