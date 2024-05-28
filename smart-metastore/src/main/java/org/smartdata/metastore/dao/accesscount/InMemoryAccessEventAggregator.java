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

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.metastore.dao.FileInfoDao;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.metrics.FileAccessEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryAccessEventAggregator implements AccessEventAggregator {
  public static final Logger LOG = LoggerFactory.getLogger(InMemoryAccessEventAggregator.class);

  private final FileInfoDao fileInfoDao;
  private final WindowClosedCallback windowClosedCallback;
  private final AccessCountEventAggregatorFailover failover;
  private final List<FileAccessEvent> eventBuffer;
  private final long aggregationGranularity;
  private Window currentWindow;
  private Map<String, AggregatedAccessCounts> unmergedAccessCounts = new HashMap<>();

  public InMemoryAccessEventAggregator(
      FileInfoDao fileInfoDao,
      WindowClosedCallback windowClosedCallback,
      AccessCountEventAggregatorFailover failover,
      long aggregationGranularity) {
    this.fileInfoDao = fileInfoDao;
    this.windowClosedCallback = windowClosedCallback;
    this.aggregationGranularity = aggregationGranularity;
    this.failover = failover;
    this.eventBuffer = new ArrayList<>();
  }

  @Override
  public void aggregate(List<FileAccessEvent> events) {
    if (currentWindow == null && !events.isEmpty()) {
      currentWindow = assignWindow(events.get(0).getTimestamp());
    }
    for (FileAccessEvent event : events) {
      if (!currentWindow.contains(event.getTimestamp())) {
        // New Window occurs
        closeCurrentWindow();
        currentWindow = assignWindow(event.getTimestamp());
        eventBuffer.clear();
      }
      // Exclude watermark event
      if (!StringUtils.isBlank(event.getPath())) {
        eventBuffer.add(event);
      }
    }
  }

  private void closeCurrentWindow() {
    Map<String, AggregatedAccessCounts> aggregatedAccessCounts = aggregateWindowEvents();

    try {
      windowClosedCallback.onWindowClosed(
          currentWindow.start, currentWindow.end, aggregatedAccessCounts.values());
    } catch (Exception exception) {
      unmergedAccessCounts = failover.handleError(
          aggregatedAccessCounts, unmergedAccessCounts, exception);
    }
  }

  private Map<String, AggregatedAccessCounts> aggregateWindowEvents() {
    if (eventBuffer.isEmpty() && unmergedAccessCounts.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, AggregatedAccessCounts> allAccessesCounts = aggregateAccessCounts(eventBuffer);
    Set<String> newAccessPaths = new HashSet<>(allAccessesCounts.keySet());
    mergeMapsInPlace(allAccessesCounts, unmergedAccessCounts);

    Map<String, Long> pathToIds = getFileIds(allAccessesCounts);

    unmergedAccessCounts = new HashMap<>();
    Map<String, AggregatedAccessCounts> accessCountsWithFileId = new HashMap<>();

    allAccessesCounts.forEach((path, accessesCounts) -> {
      Long fileId = pathToIds.get(path);

      if (fileId != null) {
        accessCountsWithFileId.put(path, accessesCounts.withFileId(fileId));
        return;
      }

      if (newAccessPaths.contains(path)) {
        // save only files from event buffer, i.e. drop unsuccessful files
        // from previous unmergedAccessCounts
        unmergedAccessCounts.put(path, accessesCounts);
      }
    });
    return accessCountsWithFileId;
  }

  private Map<String, Long> getFileIds(Map<String, AggregatedAccessCounts> allAccessesCounts) {
    try {
      return fileInfoDao.getPathFids(allAccessesCounts.keySet());
    } catch (Exception exception) {
      LOG.error("Error fetching file ids for paths {}", allAccessesCounts.keySet(), exception);
      return Collections.emptyMap();
    }
  }

  private void mergeMapsInPlace(Map<String, AggregatedAccessCounts> resultMap,
                                Map<String, AggregatedAccessCounts> mapToMerge) {
    mapToMerge.forEach((key, value) -> resultMap.merge(key, value, AggregatedAccessCounts::merge));
  }

  private Map<String, AggregatedAccessCounts> aggregateAccessCounts(List<FileAccessEvent> events) {
    return events.stream()
        .collect(Collectors.toMap(
            FileAccessEvent::getPath,
            AggregatedAccessCounts::fromEvent,
            AggregatedAccessCounts::merge
        ));
  }

  private Window assignWindow(long time) {
    long start = time - (time % aggregationGranularity);
    return new Window(start, start + aggregationGranularity);
  }

  @Data
  public static class Window {
    private final long start;
    private final long end;

    // [start, end)
    public boolean contains(long time) {
      return this.start <= time && this.end > time;
    }
  }

  public interface WindowClosedCallback {
    void onWindowClosed(long windowStart, long windowEnd,
                        Collection<AggregatedAccessCounts> aggregatedAccessCounts);
  }
}
