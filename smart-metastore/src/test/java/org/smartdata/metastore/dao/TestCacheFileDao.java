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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.model.AggregatedAccessCounts;
import org.smartdata.model.CachedFileStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCacheFileDao extends TestDaoBase {

  private CacheFileDao cacheFileDao;

  @Before
  public void initCacheFileDao() {
    cacheFileDao = daoProvider.cacheFileDao();
  }

  @Test
  public void testUpdateCachedFiles() {
    CachedFileStatus first = new CachedFileStatus(80L,
        "testPath", 1000L, 2000L, 100);
    cacheFileDao.insert(first);
    CachedFileStatus second = new CachedFileStatus(90L,
        "testPath2", 2000L, 3000L, 200);
    cacheFileDao.insert(second);
    List<AggregatedAccessCounts> accessCounts = new ArrayList<>();
    accessCounts.add(new AggregatedAccessCounts(80L, 2, 4000L));
    accessCounts.add(new AggregatedAccessCounts(90L, 2, 5000L));
    accessCounts.add(new AggregatedAccessCounts(100L, 2, 9000L));

    // Sync status
    first.setLastAccessTime(4000L);
    first.setNumAccessed(first.getNumAccessed() + 2);
    second.setLastAccessTime(5000L);
    second.setNumAccessed(second.getNumAccessed() + 2);
    cacheFileDao.update(accessCounts);
    List<CachedFileStatus> statuses = cacheFileDao.getAll();
    Assert.assertEquals(2, statuses.size());
    Map<Long, CachedFileStatus> statusMap = new HashMap<>();
    for (CachedFileStatus status : statuses) {
      statusMap.put(status.getFid(), status);
    }
    Assert.assertTrue(statusMap.containsKey(80L));
    CachedFileStatus dbFirst = statusMap.get(80L);
    Assert.assertEquals(first, dbFirst);
    Assert.assertTrue(statusMap.containsKey(90L));
    CachedFileStatus dbSecond = statusMap.get(90L);
    Assert.assertEquals(second, dbSecond);
  }

  @Test
  public void testInsertDeleteCachedFiles() {
    cacheFileDao
        .insert(80L,
            "testPath", 123456L, 234567L, 456);
    Assert.assertEquals(123456L, cacheFileDao.getById(
        80L).getFromTime());
    // Update record with 80l id
    cacheFileDao.update(80L,
        123455L, 460);
    Assert.assertEquals(123455L, cacheFileDao
        .getAll().get(0)
        .getLastAccessTime());
    CachedFileStatus[] cachedFileStatuses = new CachedFileStatus[] {
        new CachedFileStatus(321L, "testPath",
            113334L, 222222L, 222)};
    cacheFileDao.insert(cachedFileStatuses);
    Assert.assertEquals(cachedFileStatuses[0], cacheFileDao.getById(321L));
    Assert.assertEquals(2, cacheFileDao.getAll().size());
    // Delete one record
    cacheFileDao.deleteById(321L);
    Assert.assertEquals(1, cacheFileDao.getAll().size());
    // Clear all records
    cacheFileDao.deleteAll();
    Assert.assertTrue(cacheFileDao.getAll().isEmpty());
  }

  @Test
  public void testGetCachedFileStatus() {
    cacheFileDao.insert(6L, "testPath", 1490918400000L,
        234567L, 456);
    CachedFileStatus cachedFileStatus = new CachedFileStatus(6L, "testPath", 1490918400000L,
        234567L, 456);
    cacheFileDao.insert(19L, "testPath", 1490918400000L,
        234567L, 456);
    cacheFileDao.insert(23L, "testPath", 1490918400000L,
        234567L, 456);
    CachedFileStatus dbcachedFileStatus = cacheFileDao.getById(6);
    Assert.assertEquals(cachedFileStatus, dbcachedFileStatus);
    List<CachedFileStatus> cachedFileList = cacheFileDao.getAll();
    List<Long> fids = cacheFileDao.getFids();
    Assert.assertEquals(3, fids.size());
    Assert.assertEquals(6, cachedFileList.get(0).getFid());
    Assert.assertEquals(19, cachedFileList.get(1).getFid());
    Assert.assertEquals(23, cachedFileList.get(2).getFid());
  }
}
