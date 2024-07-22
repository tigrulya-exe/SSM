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
package org.smartdata.metastore.partition;

import org.junit.Before;
import org.junit.Test;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.model.FileAccessPartition;
import org.smartdata.metastore.partition.cleanup.FileAccessPartitionRetentionPolicyExecutor;
import org.smartdata.metastore.partition.cleanup.impl.MonthCountFileAccessPartitionRetentionPolicyExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class FileAccessPartitionManagerImplTest extends TestDaoBase {
  private static final String FILE_ACCESS_PARTITION_NAME_TEMPLATE = "file_access_%s";
  private FileAccessPartitionManager fileAccessPartitionManager;
  private static final DateTimeFormatter PARTITION_DATE_TIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyy_MM_dd");

  @Before
  public void setUp() throws Exception {
    int retentionCount = 2;
    FileAccessPartitionRetentionPolicyExecutor retentionPolicyExecutor =
        new MonthCountFileAccessPartitionRetentionPolicyExecutor(metaStore.fileAccessPartitionDao(),
            retentionCount);
    fileAccessPartitionManager =
        new FileAccessPartitionManagerImpl(metaStore, retentionPolicyExecutor);
  }

  @Test
  public void testCreatePartitions() {
    LocalDate currentDate = LocalDate.now().withDayOfMonth(1);
    String currentMonthPartition =
        String.format(FILE_ACCESS_PARTITION_NAME_TEMPLATE,
            currentDate.format(PARTITION_DATE_TIME_FORMAT));
    String nextMonthPartition = String.format(FILE_ACCESS_PARTITION_NAME_TEMPLATE,
        currentDate.plusMonths(1).withDayOfMonth(1).format(PARTITION_DATE_TIME_FORMAT));
    fileAccessPartitionManager.createNewPartitions();
    List<FileAccessPartition> partitions = metaStore.fileAccessPartitionDao().getAll();
    assertEquals(Arrays.asList(currentMonthPartition, nextMonthPartition), partitions.stream().map(
        FileAccessPartition::getName).collect(Collectors.toList()));
  }

  @Test
  public void testRemovePartitions() throws MetaStoreException {
    LocalDateTime currentDateTime = LocalDateTime.now().withDayOfMonth(1);
    List<LocalDateTime> months = Arrays.asList(currentDateTime.plusMonths(2).withDayOfMonth(1),
        currentDateTime.plusMonths(3).withDayOfMonth(1),
        currentDateTime.plusMonths(4).withDayOfMonth(1));
    fileAccessPartitionManager.createNewPartitions();
    List<FileAccessPartition> expectedPartitions = metaStore.fileAccessPartitionDao().getAll();
    for (LocalDateTime month : months) {
      metaStore.fileAccessPartitionDao().create(month);
    }
    fileAccessPartitionManager.removeOldPartitions();
    List<FileAccessPartition> partitions = metaStore.fileAccessPartitionDao().getAll();
    assertEquals(expectedPartitions, partitions);
  }
}
