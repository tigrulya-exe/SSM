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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartdata.conf.SmartConf;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.model.AccessCountTable;
import org.smartdata.metastore.utils.Constants;
import org.smartdata.metastore.utils.TimeGranularity;
import org.smartdata.metrics.FileAccessEvent;
import org.smartdata.model.FileAccessInfo;
import org.smartdata.model.FileInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.smartdata.conf.SmartConfKeys.SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT;
import static org.smartdata.metastore.utils.Constants.ONE_DAY_IN_MILLIS;
import static org.smartdata.metastore.utils.Constants.ONE_HOUR_IN_MILLIS;
import static org.smartdata.metastore.utils.Constants.ONE_MINUTE_IN_MILLIS;
import static org.smartdata.metastore.utils.Constants.ONE_SECOND_IN_MILLIS;


public class TestAccessCountTableManager extends TestDaoBase {

  private static final List<String> TEST_FILES = Arrays.asList(
      "/file0",
      "/file1",
      "/file2",
      "/file3",
      "/file4",
      "/file5"
  );

  private AccessCountTableManager accessCountTableManager;
  private InMemoryAccessCountTableManager inMemoryTableManager;
  private ExecutorService executorService;

  @Before
  public void setup() {
    executorService = Executors.newFixedThreadPool(4);
    accessCountTableManager = new AccessCountTableManager(
        metaStore, executorService, new SmartConf()
    );
    inMemoryTableManager = accessCountTableManager.getInMemoryTableManager();
  }

  @After
  public void shutDown() {
    executorService.shutdownNow();
  }

  @Test
  public void testCreateTable() throws Exception {
    long firstDayEnd = 24 * 60 * 60 * 1000L;
    AccessCountTable accessCountTable =
        new AccessCountTable(firstDayEnd - 5 * 1000, firstDayEnd);
    accessCountTableManager.createTable(accessCountTable);

    Thread.sleep(5000);

    Deque<AccessCountTable> second =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.SECOND);

    assertEquals(1, second.size());
    assertEquals(accessCountTable, second.peek());

    Deque<AccessCountTable> minute =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.MINUTE);
    AccessCountTable minuteTable =
        new AccessCountTable(firstDayEnd - 60 * 1000, firstDayEnd);
    assertEquals(1, minute.size());
    assertEquals(minuteTable, minute.peek());

    Deque<AccessCountTable> hour =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.HOUR);
    AccessCountTable hourTable =
        new AccessCountTable(firstDayEnd - 60 * 60 * 1000, firstDayEnd);
    assertEquals(1, hour.size());
    assertEquals(hourTable, hour.peek());

    Deque<AccessCountTable> day =
        inMemoryTableManager.getTablesOfGranularity(TimeGranularity.DAY);
    AccessCountTable dayTable = new AccessCountTable(0, firstDayEnd);
    assertEquals(1, day.size());
    assertEquals(dayTable, day.peek());
  }

  @Test
  public void testGetTables() throws MetaStoreException {
    AccessCountTable firstDay = new AccessCountTable(0L, Constants.ONE_DAY_IN_MILLIS);

    AccessCountTable firstHour =
        new AccessCountTable(23 * Constants.ONE_HOUR_IN_MILLIS,
            24 * Constants.ONE_HOUR_IN_MILLIS);
    AccessCountTable secondHour =
        new AccessCountTable(24 * Constants.ONE_HOUR_IN_MILLIS,
            25 * Constants.ONE_HOUR_IN_MILLIS);

    int numMins = 25 * 60;
    AccessCountTable firstMin =
        new AccessCountTable(
            (numMins - 1) * ONE_MINUTE_IN_MILLIS,
            numMins * ONE_MINUTE_IN_MILLIS);
    AccessCountTable secondMin =
        new AccessCountTable(
            numMins * ONE_MINUTE_IN_MILLIS,
            (numMins + 1) * ONE_MINUTE_IN_MILLIS);

    int numSeconds = (25 * 60 + 1) * 60;
    AccessCountTable firstFiveSeconds =
        new AccessCountTable(
            (numSeconds - 5) * Constants.ONE_SECOND_IN_MILLIS,
            numSeconds * Constants.ONE_SECOND_IN_MILLIS);
    AccessCountTable secondFiveSeconds =
        new AccessCountTable(
            numSeconds * Constants.ONE_SECOND_IN_MILLIS,
            (numSeconds + 5) * Constants.ONE_SECOND_IN_MILLIS);

    accessCountTableManager.getDbTableManager().createTable(firstDay);

    List<AccessCountTable> tablesToRecover = Arrays.asList(
        firstDay, firstHour, secondHour, firstMin, secondMin, firstFiveSeconds, secondFiveSeconds);
    inMemoryTableManager.recoverTables(tablesToRecover);

    List<AccessCountTable> firstResult =
        accessCountTableManager.getTablesForLast(
            (numSeconds + 5) * Constants.ONE_SECOND_IN_MILLIS);
    assertEquals(4, firstResult.size());
    assertEquals(firstDay, firstResult.get(0));
    assertEquals(secondHour, firstResult.get(1));
    assertEquals(secondMin, firstResult.get(2));
    assertEquals(secondFiveSeconds, firstResult.get(3));

    List<AccessCountTable> secondResult =
        accessCountTableManager.getTablesForLast(
            numSeconds * Constants.ONE_SECOND_IN_MILLIS);
    assertEquals(4, secondResult.size());

    AccessCountTable expectDay =
        new AccessCountTable(5 * Constants.ONE_SECOND_IN_MILLIS, Constants.ONE_DAY_IN_MILLIS);
    assertEquals(expectDay, secondResult.get(0));

    List<AccessCountTable> thirdResult =
        accessCountTableManager.getTablesForLast(
            secondFiveSeconds.getEndTime() - 23 * Constants.ONE_HOUR_IN_MILLIS);
    assertEquals(4, thirdResult.size());
    assertEquals(firstHour, thirdResult.get(0));

    List<AccessCountTable> fourthResult =
        accessCountTableManager.getTablesForLast(
            secondFiveSeconds.getEndTime() - 24 * Constants.ONE_HOUR_IN_MILLIS);
    assertEquals(3, fourthResult.size());
    assertEquals(secondHour, fourthResult.get(0));
  }

  @Test
  public void testGetTablesCornerCase() throws MetaStoreException {
    AccessCountTable firstFiveSeconds =
        new AccessCountTable(0L, 5 * Constants.ONE_SECOND_IN_MILLIS);
    AccessCountTable secondFiveSeconds =
        new AccessCountTable(5 * Constants.ONE_SECOND_IN_MILLIS,
            10 * Constants.ONE_SECOND_IN_MILLIS);

    List<AccessCountTable> tablesToRecover =
        Arrays.asList(firstFiveSeconds, secondFiveSeconds);
    inMemoryTableManager.recoverTables(tablesToRecover);

    List<AccessCountTable> result = accessCountTableManager.getTablesForLast(
        2 * ONE_MINUTE_IN_MILLIS);
    assertEquals(2, result.size());
    assertEquals(firstFiveSeconds, result.get(0));
    assertEquals(secondFiveSeconds, result.get(1));
  }

  @Test
  public void testGetTablesCornerCaseMinutes() throws MetaStoreException {
    AccessCountTable firstMinute =
        new AccessCountTable(0L, ONE_MINUTE_IN_MILLIS);
    AccessCountTable firstFiveSeconds =
        new AccessCountTable(
            55 * Constants.ONE_SECOND_IN_MILLIS, 60 * Constants.ONE_SECOND_IN_MILLIS);
    AccessCountTable secondFiveSeconds =
        new AccessCountTable(60 * Constants.ONE_SECOND_IN_MILLIS,
            65 * Constants.ONE_SECOND_IN_MILLIS);
    AccessCountTable thirdFiveSeconds =
        new AccessCountTable(110 * Constants.ONE_SECOND_IN_MILLIS,
            115 * Constants.ONE_SECOND_IN_MILLIS);

    List<AccessCountTable> tablesToRecover = Arrays.asList(
        firstMinute, firstFiveSeconds, secondFiveSeconds, thirdFiveSeconds);
    inMemoryTableManager.recoverTables(tablesToRecover);

    List<AccessCountTable> result = accessCountTableManager.getTablesForLast(
        ONE_MINUTE_IN_MILLIS);

    assertEquals(3, result.size());
    assertEquals(firstFiveSeconds, result.get(0));
    Assert.assertFalse(result.get(0).isEphemeral());
    assertEquals(secondFiveSeconds, result.get(1));
    assertEquals(thirdFiveSeconds, result.get(2));
  }

  @Test
  public void testGetAllHotFiles() throws MetaStoreException {
    createTestFiles();
    submitAccessEvents();

    List<FileAccessInfo> hotFiles = accessCountTableManager.getHotFiles(
        2 * Constants.ONE_DAY_IN_MILLIS, 100);

    List<FileAccessInfo> expectedFiles = Arrays.asList(
        new FileAccessInfo(3, TEST_FILES.get(3), 5,
            2 * ONE_DAY_IN_MILLIS - 1),
        new FileAccessInfo(1, TEST_FILES.get(1), 3,
            ONE_DAY_IN_MILLIS + 13 * ONE_HOUR_IN_MILLIS + 10 * ONE_SECOND_IN_MILLIS),
        new FileAccessInfo(2, TEST_FILES.get(2), 3,
            ONE_DAY_IN_MILLIS + 12 * ONE_HOUR_IN_MILLIS + 1),
        new FileAccessInfo(0, TEST_FILES.get(0), 1, 0),
        new FileAccessInfo(4, TEST_FILES.get(4), 1, ONE_DAY_IN_MILLIS
            + 17 * ONE_HOUR_IN_MILLIS),
        new FileAccessInfo(5, TEST_FILES.get(5), 1,
            ONE_DAY_IN_MILLIS + 18 * ONE_HOUR_IN_MILLIS + 10)
    );

    assertEquals(expectedFiles, hotFiles);
  }

  @Test
  public void testGetAllHotFilesDuringLastDay() throws MetaStoreException {
    createTestFiles();
    submitAccessEvents();

    List<FileAccessInfo> hotFiles = accessCountTableManager.getHotFiles(
        Constants.ONE_DAY_IN_MILLIS, 100);

    List<FileAccessInfo> expectedFiles = Arrays.asList(
        new FileAccessInfo(3, TEST_FILES.get(3), 3,
            2 * ONE_DAY_IN_MILLIS - 1),
        new FileAccessInfo(1, TEST_FILES.get(1), 1,
            ONE_DAY_IN_MILLIS + 13 * ONE_HOUR_IN_MILLIS + 10 * ONE_SECOND_IN_MILLIS),
        new FileAccessInfo(2, TEST_FILES.get(2), 1,
            ONE_DAY_IN_MILLIS + 12 * ONE_HOUR_IN_MILLIS + 1),
        new FileAccessInfo(4, TEST_FILES.get(4), 1, ONE_DAY_IN_MILLIS
            + 17 * ONE_HOUR_IN_MILLIS),
        new FileAccessInfo(5, TEST_FILES.get(5), 1,
            ONE_DAY_IN_MILLIS + 18 * ONE_HOUR_IN_MILLIS + 10)
    );

    assertEquals(expectedFiles, hotFiles);
  }

  @Test
  public void testGetHotFilesDuringLastHour() throws MetaStoreException {
    createTestFiles();
    submitAccessEvents();

    List<FileAccessInfo> hotFiles = accessCountTableManager.getHotFiles(
        ONE_HOUR_IN_MILLIS, 100);

    List<FileAccessInfo> expectedFiles = Collections.singletonList(
        new FileAccessInfo(3, TEST_FILES.get(3), 2,
            2 * ONE_DAY_IN_MILLIS - 1)
    );

    assertEquals(expectedFiles, hotFiles);
  }

  @Test
  public void testGetHotFilesFromPartialTable() throws MetaStoreException {
    createTestFiles();
    submitAccessEvents();

    List<FileAccessInfo> hotFiles = accessCountTableManager.getHotFiles(
        SMART_ACCESS_COUNT_AGGREGATION_INTERVAL_MS_DEFAULT / 2, 100);

    List<FileAccessInfo> expectedFiles = Collections.singletonList(
        new FileAccessInfo(3, TEST_FILES.get(3), 1,
            2 * ONE_DAY_IN_MILLIS - 1)
    );

    assertEquals(expectedFiles, hotFiles);
  }

  private void submitAccessEvents() {
    InMemoryAccessEventAggregator accessEventAggregator =
        accessCountTableManager.getAccessEventAggregator();

    List<FileAccessEvent> accessEvents = Arrays.asList(
        new FileAccessEvent(TEST_FILES.get(0), 0),
        new FileAccessEvent(TEST_FILES.get(1), 1),
        new FileAccessEvent(TEST_FILES.get(2),
            2 * ONE_MINUTE_IN_MILLIS + 1),
        new FileAccessEvent("/unknown",
            3 * ONE_MINUTE_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(1),
            4 * ONE_MINUTE_IN_MILLIS + 10 * ONE_SECOND_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(2),
            ONE_HOUR_IN_MILLIS + 5 * ONE_MINUTE_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(3),
            ONE_HOUR_IN_MILLIS + 9 * ONE_MINUTE_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(3),
            8 * ONE_HOUR_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(3),
            ONE_DAY_IN_MILLIS + 12 * ONE_HOUR_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(2),
            ONE_DAY_IN_MILLIS + 12 * ONE_HOUR_IN_MILLIS + 1),
        new FileAccessEvent(TEST_FILES.get(1),
            ONE_DAY_IN_MILLIS + 13 * ONE_HOUR_IN_MILLIS + 10 * ONE_SECOND_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(4),
            ONE_DAY_IN_MILLIS + 17 * ONE_HOUR_IN_MILLIS),
        new FileAccessEvent(TEST_FILES.get(5),
            ONE_DAY_IN_MILLIS + 18 * ONE_HOUR_IN_MILLIS + 10),
        new FileAccessEvent(TEST_FILES.get(3),
            2 * ONE_DAY_IN_MILLIS - 2),
        new FileAccessEvent(TEST_FILES.get(3),
            2 * ONE_DAY_IN_MILLIS - 1),
        new FileAccessEvent("", 2 * ONE_DAY_IN_MILLIS)
    );
    accessEventAggregator.aggregate(accessEvents);
  }

  private void createTestFiles() throws MetaStoreException {
    FileInfo[] fileInfos = IntStream.range(0, TEST_FILES.size())
        .mapToObj(id -> FileInfo.newBuilder()
            .setFileId(id)
            .setPath(TEST_FILES.get(id))
            .build())
        .toArray(FileInfo[]::new);

    metaStore.insertFiles(fileInfos);
  }
}
