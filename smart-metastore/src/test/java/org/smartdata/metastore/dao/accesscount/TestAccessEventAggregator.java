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
import org.junit.Before;
import org.junit.Test;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.dao.accesscount.InMemoryAccessEventAggregator.WindowClosedCallback;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.metrics.FileAccessEvent;
import org.smartdata.model.FileInfo;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAccessEventAggregator extends TestDaoBase {

  private static final long AGGREGATION_GRANULARITY_MS = 5000;
  private static final Map<String, Long> FILE_PATH_IDS = ImmutableMap.of(
      "/file1", 1L,
      "/file2", 2L,
      "/file3", 3L,
      "/file4", 4L
  );

  private InMemoryAccessEventAggregator aggregator;
  private MockWindowClosedCallback windowCallback;

  @Before
  public void setup() {
    windowCallback = new MockWindowClosedCallback();
    aggregator = new InMemoryAccessEventAggregator(
        metaStore.fileInfoDao(),
        windowCallback,
        AccessCountEventAggregatorFailover.dropEvents(),
        AGGREGATION_GRANULARITY_MS);

    metaStore.fileInfoDao().insert(testFileInfos());
  }

  @Test
  public void testAggregateEvents() {
    List<AccessCountTable> createdTables = windowCallback.getCreatedTables();
    List<AggregatedAccessCounts> collectedAccessCounts =
        windowCallback.getCollectedAccessCounts();

    aggregator.aggregate(Collections.singletonList(new FileAccessEvent("", 3000)));
    assertTrue(createdTables.isEmpty());
    assertTrue(collectedAccessCounts.isEmpty());

    aggregator.aggregate(Collections.singletonList
        (new FileAccessEvent("/file1", 4999)));
    aggregator.aggregate(Collections.singletonList(
        new FileAccessEvent("", 6000)));

    assertEquals(
        Collections.singletonList(new AccessCountTable(0, 5000)),
        createdTables);
    assertEquals(
        Collections.singletonList(
            new AggregatedAccessCounts(FILE_PATH_IDS.get("/file1"), 1, 4999)),
        collectedAccessCounts);

    aggregator.aggregate(
        Arrays.asList(
            new FileAccessEvent("/file1", 7900),
            new FileAccessEvent("/file1", 7999),
            new FileAccessEvent("/file1", 8000),
            new FileAccessEvent("/file2", 14000),
            new FileAccessEvent("/file3", 14000),
            new FileAccessEvent("/file3", 14001),
            new FileAccessEvent("/file3", 14002),
            new FileAccessEvent("/unknown_file", 14003),
            new FileAccessEvent("/file4", 16000),
            new FileAccessEvent("", 22000)));

    List<AccessCountTable> expectedTables = Arrays.asList(
        new AccessCountTable(0, 5000),
        new AccessCountTable(5000, 10000),
        new AccessCountTable(10000, 15000),
        new AccessCountTable(15000, 20000)
    );
    assertEquals(expectedTables, createdTables);

    List<AggregatedAccessCounts> expectedAccessCounts = Arrays.asList(
        new AggregatedAccessCounts(FILE_PATH_IDS.get("/file1"), 1, 4999),
        new AggregatedAccessCounts(FILE_PATH_IDS.get("/file1"), 3, 8000),
        new AggregatedAccessCounts(FILE_PATH_IDS.get("/file2"), 1, 14000),
        new AggregatedAccessCounts(FILE_PATH_IDS.get("/file3"), 3, 14002),
        new AggregatedAccessCounts(FILE_PATH_IDS.get("/file4"), 1, 16000)
    );

    collectedAccessCounts.sort(
        Comparator.comparingLong(AggregatedAccessCounts::getLastAccessedTimestamp));
    assertEquals(expectedAccessCounts, collectedAccessCounts);
  }

  private FileInfo[] testFileInfos() {
    return FILE_PATH_IDS.entrySet()
        .stream()
        .map(entry -> dummyFileInfo(entry.getKey(), entry.getValue()))
        .toArray(FileInfo[]::new);
  }

  private FileInfo dummyFileInfo(String path, long fileId) {
    return FileInfo.newBuilder()
        .setPath(path)
        .setFileId(fileId)
        .build();
  }

  @Getter
  private static class MockWindowClosedCallback implements WindowClosedCallback {

    private final List<AccessCountTable> createdTables = new ArrayList<>();
    private final List<AggregatedAccessCounts>
        collectedAccessCounts = new ArrayList<>();

    @Override
    public void onWindowClosed(long windowStart, long windowEnd,
                               Collection<AggregatedAccessCounts> aggregatedAccessCounts) {
      AccessCountTable table = new AccessCountTable(windowStart, windowEnd);
      createdTables.add(table);
      collectedAccessCounts.addAll(aggregatedAccessCounts);
    }
  }
}
