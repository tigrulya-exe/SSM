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
package org.smartdata.metastore;

import org.junit.Assert;
import org.junit.Test;
import org.smartdata.metrics.FileAccessEvent;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.BackUpInfo;
import org.smartdata.model.CachedFileStatus;
import org.smartdata.model.ClusterConfig;
import org.smartdata.model.ClusterInfo;
import org.smartdata.model.CmdletInfo;
import org.smartdata.model.CmdletState;
import org.smartdata.model.CompressionFileState;
import org.smartdata.model.DataNodeInfo;
import org.smartdata.model.DataNodeStorageInfo;
import org.smartdata.model.FileInfo;
import org.smartdata.model.FileState;
import org.smartdata.model.GlobalConfig;
import org.smartdata.model.NormalFileState;
import org.smartdata.model.RuleInfo;
import org.smartdata.model.RuleState;
import org.smartdata.model.StorageCapacity;
import org.smartdata.model.StoragePolicy;
import org.smartdata.model.SystemInfo;
import org.smartdata.model.XAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.smartdata.utils.StringUtil.ssmPatternToRegex;

public class TestMetaStore extends TestDaoBase {
  @Test
  public void testHighConcurrency() throws Exception {
    // Multiple threads
    Thread th1 = new InsertThread(metaStore);
    Thread th2 = new SelectUpdateThread(metaStore);
    th1.start();
    Thread.sleep(1000);
    th2.start();
    th2.join();
  }

  @Test
  public void testThreadSleepConcurrency() throws Exception {
    // Multiple threads
    Thread th1 = new InsertThread(metaStore);
    Thread th2 = new SleepSelectUpdateThread(metaStore);
    th1.start();
    Thread.sleep(1000);
    th2.start();
    th2.join();
  }

  static class SleepSelectUpdateThread extends Thread {
    private final MetaStore metaStore;

    public SleepSelectUpdateThread(MetaStore metaStore) {
      this.metaStore = metaStore;
    }

    public void run() {
      for (int i = 0; i < 100; i++) {
        try {
          List<ActionInfo> actionInfoList =
              metaStore.getActions(Collections.singletonList((long) i));
          actionInfoList.get(0).setFinished(true);
          actionInfoList.get(0).setFinishTime(System.currentTimeMillis());
          sleep(5);
          metaStore.updateActions(actionInfoList.toArray(new ActionInfo[0]));
          metaStore.getActions(null, null);
        } catch (MetaStoreException | InterruptedException e) {
          System.out.println(e.getMessage());
          Assert.fail();
        }
      }
    }
  }

  static class InsertThread extends Thread {
    private final MetaStore metaStore;

    public InsertThread(MetaStore metaStore) {
      this.metaStore = metaStore;
    }

    public void run() {
      Map<String, String> args = new HashMap<>();
      ActionInfo actionInfo =
          new ActionInfo(1, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
              100);
      for (int i = 0; i < 100; i++) {
        actionInfo.setActionId(i);
        try {
          metaStore.insertAction(actionInfo);
        } catch (MetaStoreException e) {
          System.out.println(e.getMessage());
          Assert.fail();
        }
      }
    }
  }

  static class SelectUpdateThread extends Thread {
    private final MetaStore metaStore;

    public SelectUpdateThread(MetaStore metaStore) {
      this.metaStore = metaStore;
    }

    public void run() {
      for (int i = 0; i < 100; i++) {
        try {
          List<ActionInfo> actionInfoList =
              metaStore.getActions(Collections.singletonList((long) i));
          actionInfoList.get(0).setFinished(true);
          actionInfoList.get(0).setFinishTime(System.currentTimeMillis());
          metaStore.updateActions(actionInfoList.toArray(new ActionInfo[0]));
          metaStore.getActions(null, null);
        } catch (MetaStoreException e) {
          System.out.println(e.getMessage());
          Assert.fail();
        }
      }
    }
  }

  @Test
  public void testGetFiles() throws Exception {
    String pathString = "/tmp/des";
    long length = 123L;
    boolean isDir = false;
    int blockReplication = 1;
    long blockSize = 128 * 1024L;
    long modTime = 123123123L;
    long accessTime = 123123120L;
    String owner = "root";
    String group = "admin";
    long fileId = 56L;
    byte storagePolicy = 0;
    byte erasureCodingPolicy = 0;
    FileInfo fileInfo =
        new FileInfo(
            pathString,
            fileId,
            length,
            isDir,
            (short) blockReplication,
            blockSize,
            modTime,
            accessTime,
            (short) 1,
            owner,
            group,
            storagePolicy,
            erasureCodingPolicy);
    metaStore.insertFile(fileInfo);
    FileInfo dbFileInfo = metaStore.getFile(56);
    Assert.assertEquals(fileInfo, dbFileInfo);
    dbFileInfo = metaStore.getFile("/tmp/des");
    Assert.assertEquals(fileInfo, dbFileInfo);
  }

  @Test
  public void testGetNonExistFile() throws Exception {
    FileInfo info = metaStore.getFile("/non_exist_file_path");
    Assert.assertNull(info);
  }

  @Test
  public void testInsertStoragesTable() throws Exception {
    StorageCapacity storage1 = new StorageCapacity("Flash", 12343333L, 2223333L);
    StorageCapacity storage2 = new StorageCapacity("RAM", 12342233L, 2223663L);
    StorageCapacity[] storages = {storage1, storage2};
    metaStore.insertUpdateStoragesTable(storages);
    StorageCapacity storageCapacity1 = metaStore.getStorageCapacity("Flash");
    StorageCapacity storageCapacity2 = metaStore.getStorageCapacity("RAM");
    Assert.assertEquals(storageCapacity1, storage1);
    Assert.assertEquals(storageCapacity2, storage2);
    Assert.assertTrue(metaStore.updateStoragesTable("Flash", 123456L, 4562233L));
    Assert.assertEquals(123456L, (long) metaStore.getStorageCapacity("Flash").getCapacity());
  }

  @Test
  public void testGetStoreCapacityOfDifferentStorageType() throws Exception {
    DataNodeStorageInfo info1 = new DataNodeStorageInfo("1", "ssd", 1, "1", true, 1, 1, 1, 1);
    DataNodeStorageInfo info2 = new DataNodeStorageInfo("2", "ssd", 2, "2", true, 2, 2, 2, 2);

    metaStore.insertDataNodeStorageInfo(info1);
    metaStore.insertDataNodeStorageInfo(info2);

    long capacity = metaStore.getStoreCapacityOfDifferentStorageType("ssd");
    Assert.assertEquals(3, capacity);
  }

  @Test
  public void testGetStoreFreeOfDifferentStorageType() throws Exception {
    DataNodeStorageInfo info1 = new DataNodeStorageInfo("1", "ssd", 1, "1", false, 1, 1, 1, 1);
    DataNodeStorageInfo info2 = new DataNodeStorageInfo("2", "ssd", 2, "2", true, 2, 2, 2, 2);

    metaStore.insertDataNodeStorageInfo(info1);
    metaStore.insertDataNodeStorageInfo(info2);

    long free = metaStore.getStoreFreeOfDifferentStorageType("ssd");
    Assert.assertEquals(3, free);
  }

  @Test
  public void testGetStorageCapacity() throws Exception {
    StorageCapacity storage1 = new StorageCapacity("HDD", 12343333L, 2223333L);
    StorageCapacity storage2 = new StorageCapacity("RAM", 12342233L, 2223663L);
    StorageCapacity[] storages = {storage1, storage2};
    metaStore.insertUpdateStoragesTable(storages);
    Assert.assertEquals(storage1, metaStore.getStorageCapacity("HDD"));
    Assert.assertEquals(storage2, metaStore.getStorageCapacity("RAM"));

    StorageCapacity storage3 = new StorageCapacity("HDD", 100L, 10L);
    metaStore.insertUpdateStoragesTable(storage3);
    Assert.assertEquals(storage3, metaStore.getStorageCapacity("HDD"));
  }

  @Test
  public void testInsertRule() throws Exception {
    String rule = "file : accessCount(10m) > 20 \n\n" + "and length() > 3 | cache";
    long submitTime = System.currentTimeMillis();
    RuleInfo info1 = new RuleInfo(0, submitTime, rule, RuleState.ACTIVE, 0, 0, 0);
    Assert.assertTrue(metaStore.insertNewRule(info1));
    RuleInfo info11 = metaStore.getRuleInfo(info1.getId());
    Assert.assertEquals(info1, info11);

    long now = System.currentTimeMillis();
    metaStore.updateRuleInfo(info1.getId(), RuleState.DELETED, now, 1, 1);
    info1.setState(RuleState.DELETED);
    info1.setLastCheckTime(now);
    info1.setNumChecked(1);
    info1.setNumCmdsGen(1);
    RuleInfo info12 = metaStore.getRuleInfo(info1.getId());
    Assert.assertEquals(info12, info1);

    RuleInfo info2 = new RuleInfo(0, submitTime, rule, RuleState.ACTIVE, 0, 0, 0);
    Assert.assertTrue(metaStore.insertNewRule(info2));
    RuleInfo info21 = metaStore.getRuleInfo(info2.getId());
    Assert.assertNotEquals(info11, info21);

    List<RuleInfo> infos = metaStore.getRuleInfo();
    Assert.assertEquals(2, infos.size());
  }

  @Test
  public void testMoveSyncRules() throws Exception {
    String pathString = "/src/1";
    long length = 123L;
    boolean isDir = false;
    int blockReplication = 1;
    long blockSize = 128 * 1024L;
    long modTime = 123123123L;
    long accessTime = 123123120L;
    String owner = "root";
    String group = "admin";
    long fileId = 56L;
    byte storagePolicy = 0;
    byte erasureCodingPolicy = 0;
    FileInfo fileInfo =
        new FileInfo(
            pathString,
            fileId,
            length,
            isDir,
            (short) blockReplication,
            blockSize,
            modTime,
            accessTime,
            (short) 1,
            owner,
            group,
            storagePolicy,
            erasureCodingPolicy);
    metaStore.insertFile(fileInfo);
    Map<String, String> args = new HashMap<>();
    args.put("-file", "/src/1");
    String rule = "file : accessCount(10m) > 20 \n\n" + "and length() > 3 | ";
    long submitTime = System.currentTimeMillis();
    RuleInfo ruleInfo =
        new RuleInfo(0, submitTime, rule + "sync -dest /dest/", RuleState.ACTIVE, 0, 0, 0);
    metaStore.insertNewRule(ruleInfo);
    metaStore.insertBackUpInfo(new BackUpInfo(ruleInfo.getId(), "/src/", "/dest/", 100));
    metaStore.insertNewRule(
        new RuleInfo(1, submitTime, rule + "allssd", RuleState.ACTIVE, 0, 0, 0));
    metaStore.insertNewRule(
        new RuleInfo(2, submitTime, rule + "archive", RuleState.ACTIVE, 0, 0, 0));
    metaStore.insertNewRule(
        new RuleInfo(2, submitTime, rule + "onessd", RuleState.ACTIVE, 0, 0, 0));
    metaStore.insertNewRule(new RuleInfo(2, submitTime, rule + "cache", RuleState.ACTIVE, 0, 0, 0));
    Assert.assertEquals(3, metaStore.listMoveRules().size());
    Assert.assertEquals(1, metaStore.listSyncRules().size());
    CmdletInfo cmdletInfo =
        new CmdletInfo(1, ruleInfo.getId(), CmdletState.EXECUTING, "test", 123123333L,
            232444444L);
    cmdletInfo.setAids(Collections.singletonList(1L));
    metaStore.insertCmdlet(cmdletInfo);
    metaStore.insertAction(
        new ActionInfo(1, 1, "allssd", args, "Test", "Test", true, 123213213L, true, 123123L,
            100));
    Assert.assertTrue(metaStore.listFileActions(ruleInfo.getId(), 0).size() >= 0);
  }

  @Test
  public void testUpdateCachedFiles() throws Exception {
    metaStore.insertCachedFiles(80L, "testPath", 1000L, 2000L, 100);
    metaStore.insertCachedFiles(90L, "testPath2", 2000L, 3000L, 200);
    Map<String, Long> pathToId = new HashMap<>();
    pathToId.put("testPath", 80L);
    pathToId.put("testPath2", 90L);
    pathToId.put("testPath3", 100L);
    List<FileAccessEvent> events = new ArrayList<>();
    events.add(new FileAccessEvent("testPath", 3000L));
    events.add(new FileAccessEvent("testPath", 4000L));
    events.add(new FileAccessEvent("testPath2", 4000L));
    events.add(new FileAccessEvent("testPath2", 5000L));

    events.add(new FileAccessEvent("testPath3", 8000L));
    events.add(new FileAccessEvent("testPath3", 9000L));

    metaStore.updateCachedFiles(pathToId, events);
    List<CachedFileStatus> statuses = metaStore.getCachedFileStatus();
    Assert.assertEquals(2, statuses.size());
    Map<Long, CachedFileStatus> statusMap = new HashMap<>();
    for (CachedFileStatus status : statuses) {
      statusMap.put(status.getFid(), status);
    }
    Assert.assertTrue(statusMap.containsKey(80L));
    CachedFileStatus first = statusMap.get(80L);
    Assert.assertEquals(4000L, first.getLastAccessTime());
    Assert.assertEquals(102, first.getNumAccessed());

    Assert.assertTrue(statusMap.containsKey(90L));
    CachedFileStatus second = statusMap.get(90L);
    Assert.assertEquals(5000L, second.getLastAccessTime());
    Assert.assertEquals(202, second.getNumAccessed());
  }

  @Test
  public void testInsertDeleteCachedFiles() throws Exception {
    metaStore.insertCachedFiles(80L, "testPath", 123456L, 234567L, 456);
    Assert.assertEquals(123456L, metaStore.getCachedFileStatus(80L).getFromTime());
    // Update record with 80l id
    Assert.assertTrue(metaStore.updateCachedFiles(80L, 234568L, 460));
    Assert.assertEquals(234568L, metaStore.getCachedFileStatus().get(0).getLastAccessTime());
    List<CachedFileStatus> list = new LinkedList<>();
    list.add(new CachedFileStatus(321L, "testPath", 113334L, 222222L, 222));
    metaStore.insertCachedFiles(list);
    Assert.assertEquals(222, metaStore.getCachedFileStatus(321L).getNumAccessed());
    Assert.assertEquals(2, metaStore.getCachedFileStatus().size());
    // Delete one record
    metaStore.deleteCachedFile(321L);
    Assert.assertEquals(1, metaStore.getCachedFileStatus().size());
    // Clear all records
    metaStore.deleteAllCachedFile();
    Assert.assertEquals(0, metaStore.getCachedFileStatus().size());
    metaStore.insertCachedFiles(80L, "testPath", 123456L, 234567L, 456);
  }

  @Test
  public void testGetCachedFileStatus() throws Exception {
    metaStore.insertCachedFiles(6L, "testPath", 1490918400000L, 234567L, 456);
    metaStore.insertCachedFiles(19L, "testPath", 1490918400000L, 234567L, 456);
    metaStore.insertCachedFiles(23L, "testPath", 1490918400000L, 234567L, 456);
    CachedFileStatus cachedFileStatus = metaStore.getCachedFileStatus(6);
    Assert.assertEquals(1490918400000L, cachedFileStatus.getFromTime());
    List<CachedFileStatus> cachedFileList = metaStore.getCachedFileStatus();
    List<Long> fids = metaStore.getCachedFids();
    Assert.assertEquals(3, fids.size());
    Assert.assertEquals(6, cachedFileList.get(0).getFid());
    Assert.assertEquals(19, cachedFileList.get(1).getFid());
    Assert.assertEquals(23, cachedFileList.get(2).getFid());
  }

  @Test
  public void testInsetFiles() throws Exception {
    String pathString = "/tmp/testFile";
    long length = 123L;
    boolean isDir = false;
    int blockReplication = 1;
    long blockSize = 128 * 1024L;
    long modTime = 123123123L;
    long accessTime = 123123120L;
    String owner = "root";
    String group = "admin";
    long fileId = 312321L;
    byte storagePolicy = 0;
    byte erasureCodingPolicy = 0;
    FileInfo[] files = {
        new FileInfo(
            pathString,
            fileId,
            length,
            isDir,
            (short) blockReplication,
            blockSize,
            modTime,
            accessTime,
            (short) 1,
            owner,
            group,
            storagePolicy,
            erasureCodingPolicy)
    };
    metaStore.insertFiles(files);
    FileInfo dbFileInfo = metaStore.getFile("/tmp/testFile");
    Assert.assertEquals(files[0], dbFileInfo);
  }

  @Test
  public void testInsertCmdletsTable() throws Exception {
    CmdletInfo command1 =
        new CmdletInfo(0, 1, CmdletState.EXECUTING, "test", 123123333L, 232444444L);
    metaStore.insertCmdlet(command1);
    CmdletInfo command2 = new CmdletInfo(1, 78, CmdletState.PAUSED, "tt", 123178333L, 232444994L);
    metaStore.insertCmdlet(command2);
    Assert.assertEquals(command1, metaStore.getCmdletById(command1.getCid()));
    Assert.assertEquals(command2, metaStore.getCmdletById(command2.getCid()));
    metaStore.updateCmdlet(command1.getCid(), "TestParameter", CmdletState.EXECUTING);
    Assert.assertEquals("TestParameter",
        metaStore.getCmdletById(command1.getCid()).getParameters());
    Assert.assertEquals(CmdletState.EXECUTING,
        metaStore.getCmdletById(command1.getCid()).getState());
  }

  @Test
  public void testdeleteFinishedCmdletsWithGenTimeBefore() throws Exception {
    Map<String, String> args = new HashMap<>();
    CmdletInfo command1 =
        new CmdletInfo(0, 78, CmdletState.CANCELLED, "test", 123L, 232444444L);
    metaStore.insertCmdlet(command1);
    CmdletInfo command2 = new CmdletInfo(1, 78, CmdletState.DONE, "tt", 128L, 232444994L);
    metaStore.insertCmdlet(command2);
    ActionInfo actionInfo =
        new ActionInfo(1, 0, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo);
    ActionInfo actionInfo2 =
        new ActionInfo(2, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo2);
    ActionInfo actionInfo3 =
        new ActionInfo(3, 0, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo3);
    metaStore.deleteFinishedCmdletsWithGenTimeBefore(125);
    Assert.assertNull(metaStore.getCmdletById(0));
    Assert.assertNull(metaStore.getActionById(1));
    Assert.assertNotNull(metaStore.getActionById(2));
  }

  @Test
  public void testdeleteKeepNewCmdlets() throws Exception {
    Map<String, String> args = new HashMap<>();
    CmdletInfo command1 =
        new CmdletInfo(0, 78, CmdletState.CANCELLED, "test", 123L, 232444444L);
    metaStore.insertCmdlet(command1);
    CmdletInfo command2 = new CmdletInfo(1, 78, CmdletState.DONE, "tt", 128L, 232444994L);
    metaStore.insertCmdlet(command2);
    ActionInfo actionInfo =
        new ActionInfo(1, 0, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo);
    ActionInfo actionInfo2 =
        new ActionInfo(2, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo2);
    ActionInfo actionInfo3 =
        new ActionInfo(3, 0, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo3);
    metaStore.deleteKeepNewCmdlets(1);
    Assert.assertNull(metaStore.getCmdletById(0));
    Assert.assertNull(metaStore.getActionById(1));
    Assert.assertNotNull(metaStore.getActionById(2));
  }

  @Test
  public void testUpdateDeleteCommand() throws Exception {
    long commandId;
    commandId = metaStore.getMaxCmdletId();
    System.out.printf("CommandID = %d\n", commandId);
    CmdletInfo command1 = new CmdletInfo(0, 1, CmdletState.PENDING, "test", 123123333L, 232444444L);
    CmdletInfo command2 = new CmdletInfo(1, 78, CmdletState.PENDING, "tt", 123178333L, 232444994L);
    List<CmdletInfo> commands = Arrays.asList(command1, command2);
    metaStore.upsertCmdlets(commands);
    List<CmdletInfo> com = metaStore.getCmdlets(CmdletState.PENDING);
    commandId = metaStore.getMaxCmdletId();
    Assert.assertEquals(commandId, commands.size());
    for (CmdletInfo cmd : com) {
      metaStore.updateCmdlet(cmd.getCid(), CmdletState.DONE);
    }
    List<CmdletInfo> com1 = metaStore.getCmdlets(CmdletState.DONE);
    Assert.assertEquals(2, com1.size());

    Assert.assertEquals(CmdletState.DONE, com1.get(0).getState());
    metaStore.deleteCmdlet(command2.getCid());
    com1 = metaStore.getCmdlets(CmdletState.DONE);
    Assert.assertEquals(1, com1.size());
  }

  @Test
  public void testInsertListActions() throws Exception {
    Map<String, String> args = new HashMap<>();
    ActionInfo actionInfo =
        new ActionInfo(1, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertActions(new ActionInfo[]{actionInfo});
    List<ActionInfo> actionInfos = metaStore.getActions(null, null);
    Assert.assertEquals(1, actionInfos.size());
    actionInfo.setResult("Finished");
    metaStore.updateActions(new ActionInfo[]{actionInfo});
    actionInfos = metaStore.getActions(null, null);
    Assert.assertEquals(actionInfo, actionInfos.get(0));
  }

  @Test
  public void testGetNewCreatedActions() throws Exception {
    Map<String, String> args = new HashMap<>();
    List<ActionInfo> actionInfos;
    ActionInfo actionInfo =
        new ActionInfo(1, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertAction(actionInfo);
    actionInfo.setActionId(2);
    metaStore.insertAction(actionInfo);
    actionInfos = metaStore.getNewCreatedActions(1);
    Assert.assertEquals(1, actionInfos.size());
    actionInfos = metaStore.getNewCreatedActions("cache", 1, true, true);
    Assert.assertEquals(1, actionInfos.size());
    actionInfos = metaStore.getNewCreatedActions(2);
    Assert.assertEquals(2, actionInfos.size());
  }

  @Test
  public void testGetMaxActionId() throws Exception {
    long currentId = metaStore.getMaxActionId();
    Map<String, String> args = new HashMap<>();
    Assert.assertEquals(0, currentId);
    ActionInfo actionInfo =
        new ActionInfo(
            currentId, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertActions(new ActionInfo[]{actionInfo});
    currentId = metaStore.getMaxActionId();
    Assert.assertEquals(1, currentId);
    actionInfo =
        new ActionInfo(
            currentId, 1, "cache", args, "Test", "Test", true, 123213213L, true, 123123L,
            100);
    metaStore.insertActions(new ActionInfo[]{actionInfo});
    currentId = metaStore.getMaxActionId();
    Assert.assertEquals(2, currentId);
  }

  @Test
  public void testInsertStoragePolicyTable() throws Exception {
    metaStore.insertStoragePolicy(new StoragePolicy((byte) 53, "COOL"));
    metaStore.insertStoragePolicy(new StoragePolicy((byte) 52, "COLD"));
    String value = metaStore.getStoragePolicyName(53);
    Assert.assertEquals(metaStore.getStoragePolicyName(52), "COLD");
    int key = metaStore.getStoragePolicyID("COOL");
    Assert.assertEquals(value, "COOL");
    Assert.assertEquals(key, 53);
  }

  @Test
  public void testInsertXattrTable() throws Exception {
    long fid = 567L;
    List<XAttribute> attributes = new ArrayList<>();
    Random random = new Random();
    byte[] value1 = new byte[1024];
    byte[] value2 = new byte[1024];
    random.nextBytes(value1);
    random.nextBytes(value2);
    attributes.add(new XAttribute("user", "a1", value1));
    attributes.add(new XAttribute("raw", "you", value2));
    Assert.assertTrue(metaStore.insertXattrList(fid, attributes));
    List<XAttribute> result = metaStore.getXattrList(fid);
    Assert.assertEquals(result.size(), attributes.size());
    Assert.assertTrue(result.containsAll(attributes));
  }

  @Test
  public void testSetClusterConfig() throws MetaStoreException {
    ClusterConfig clusterConfig = new ClusterConfig(1, "test", "test1");
    metaStore.setClusterConfig(clusterConfig);
    List<ClusterConfig> list = new LinkedList<>();
    list.add(clusterConfig);
    Assert.assertEquals(list, metaStore.listClusterConfig());
    list.get(0).setConfig_path("test2");

    metaStore.setClusterConfig(list.get(0));
    Assert.assertEquals(list, metaStore.listClusterConfig());
  }

  @Test
  public void testDelClusterConfig() throws MetaStoreException {
    ClusterConfig clusterConfig = new ClusterConfig(1, "test", "test1");
    metaStore.setClusterConfig(clusterConfig);
    metaStore.delClusterConfig(clusterConfig);
    Assert.assertTrue(metaStore.listClusterConfig().isEmpty());
  }

  @Test
  public void testSetGlobalConfig() throws MetaStoreException {
    GlobalConfig globalConfig = new GlobalConfig(1, "test", "test1");
    metaStore.setGlobalConfig(globalConfig);
    Assert.assertEquals(globalConfig, metaStore.getDefaultGlobalConfigByName("test"));
    globalConfig.setPropertyValue("test2");

    metaStore.setGlobalConfig(globalConfig);
    Assert.assertEquals(globalConfig, metaStore.getDefaultGlobalConfigByName("test"));
  }

  @Test
  public void testInsertDataNodeInfo() throws Exception {
    DataNodeInfo insertInfo1 = new DataNodeInfo("UUID1", "hostname", "www.ssm.com", 100, 50, "lab");
    metaStore.insertDataNodeInfo(insertInfo1);
    List<DataNodeInfo> getInfo1 = metaStore.getDataNodeInfoByUuid("UUID1");
    Assert.assertEquals(insertInfo1, getInfo1.get(0));

    DataNodeInfo insertInfo2 = new DataNodeInfo("UUID2", "HOSTNAME", "www.ssm.com", 0, 0, null);
    DataNodeInfo insertInfo3 = new DataNodeInfo("UUID3", "HOSTNAME", "www.ssm.com", 0, 0, null);
    metaStore.insertDataNodeInfos(new DataNodeInfo[]{insertInfo2, insertInfo3});
    List<DataNodeInfo> getInfo2 = metaStore.getDataNodeInfoByUuid("UUID2");
    Assert.assertEquals(insertInfo2, getInfo2.get(0));
    List<DataNodeInfo> getInfo3 = metaStore.getDataNodeInfoByUuid("UUID3");
    Assert.assertEquals(insertInfo3, getInfo3.get(0));
  }

  @Test
  public void testDeleteDataNodeInfo() throws Exception {
    DataNodeInfo insertInfo1 = new DataNodeInfo("UUID1", "hostname", "www.ssm.com", 100, 50, "lab");
    DataNodeInfo insertInfo2 = new DataNodeInfo("UUID2", "HOSTNAME", "www.ssm.com", 0, 0, null);
    DataNodeInfo insertInfo3 = new DataNodeInfo("UUID3", "HOSTNAME", "www.ssm.com", 0, 0, null);
    metaStore.insertDataNodeInfos(new DataNodeInfo[]{insertInfo1, insertInfo2, insertInfo3});

    List<DataNodeInfo> infos = metaStore.getAllDataNodeInfo();
    Assert.assertEquals(3, infos.size());

    metaStore.deleteDataNodeInfo(insertInfo1.getUuid());
    infos = metaStore.getAllDataNodeInfo();
    Assert.assertEquals(2, infos.size());

    metaStore.deleteAllDataNodeInfo();
    infos = metaStore.getAllDataNodeInfo();
    Assert.assertTrue(infos.isEmpty());
  }

  @Test
  public void testInsertDataNodeStorageInfo() throws Exception {
    DataNodeStorageInfo insertInfo1 =
        new DataNodeStorageInfo("UUID1", 10, 10, "storageid1", false, 0, 0, 0, 0);
    metaStore.insertDataNodeStorageInfo(insertInfo1);
    List<DataNodeStorageInfo> getInfo1 = metaStore.getDataNodeStorageInfoByUuid("UUID1");
    Assert.assertEquals(insertInfo1, getInfo1.get(0));

    DataNodeStorageInfo insertInfo2 =
        new DataNodeStorageInfo("UUID2", 10, 10, "storageid2", false, 0, 0, 0, 0);
    DataNodeStorageInfo insertInfo3 =
        new DataNodeStorageInfo("UUID3", 10, 10, "storageid2", false, 0, 0, 0, 0);
    metaStore.insertDataNodeStorageInfos(new DataNodeStorageInfo[]{insertInfo2, insertInfo3});
    List<DataNodeStorageInfo> getInfo2 = metaStore.getDataNodeStorageInfoByUuid("UUID2");
    Assert.assertEquals(insertInfo2, getInfo2.get(0));
    List<DataNodeStorageInfo> getInfo3 = metaStore.getDataNodeStorageInfoByUuid("UUID3");
    Assert.assertEquals(insertInfo3, getInfo3.get(0));
  }

  @Test
  public void testDeleteDataNodeStorageInfo() throws Exception {
    DataNodeStorageInfo insertInfo1 =
        new DataNodeStorageInfo("UUID1", 10, 10, "storageid1", false, 0, 0, 0, 0);
    DataNodeStorageInfo insertInfo2 =
        new DataNodeStorageInfo("UUID2", 10, 10, "storageid2", false, 0, 0, 0, 0);
    DataNodeStorageInfo insertInfo3 =
        new DataNodeStorageInfo("UUID3", 10, 10, "storageid3", false, 0, 0, 0, 0);
    metaStore.insertDataNodeStorageInfos(
        new DataNodeStorageInfo[]{insertInfo1, insertInfo2, insertInfo3});

    List<DataNodeStorageInfo> infos = metaStore.getAllDataNodeStorageInfo();
    Assert.assertEquals(3, infos.size());

    metaStore.deleteDataNodeStorageInfo(insertInfo1.getUuid());
    infos = metaStore.getAllDataNodeStorageInfo();
    Assert.assertEquals(2, infos.size());

    metaStore.deleteAllDataNodeStorageInfo();
    infos = metaStore.getAllDataNodeStorageInfo();
    Assert.assertTrue(infos.isEmpty());
  }

  @Test
  public void testInsertAndListAllBackUpInfo() throws MetaStoreException {
    BackUpInfo backUpInfo1 = new BackUpInfo(1, "test1", "test1", 1);
    BackUpInfo backUpInfo2 = new BackUpInfo(2, "test2", "test2", 2);
    BackUpInfo backUpInfo3 = new BackUpInfo(3, "test3", "test3", 3);

    metaStore.insertBackUpInfo(backUpInfo1);
    metaStore.insertBackUpInfo(backUpInfo2);
    metaStore.insertBackUpInfo(backUpInfo3);

    List<BackUpInfo> backUpInfos = metaStore.listAllBackUpInfo();
    List<BackUpInfo> expectedBackupInfos = Arrays.asList(backUpInfo1, backUpInfo2, backUpInfo3);

    Assert.assertEquals(expectedBackupInfos, backUpInfos);
  }

  @Test
  public void testGetBackUpInfoById() throws MetaStoreException {
    BackUpInfo backUpInfo1 = new BackUpInfo(1, "test1", "test1", 1);
    metaStore.insertBackUpInfo(backUpInfo1);
    Assert.assertEquals(backUpInfo1, metaStore.getBackUpInfo(1));
  }

  @Test
  public void testSrcInBackup() throws MetaStoreException {
    BackUpInfo backUpInfo1 = new BackUpInfo(1, "src/", "dest/", 1,
        ssmPatternToRegex("src/test_?/*.bin"));
    metaStore.insertBackUpInfo(backUpInfo1);

    Assert.assertFalse(metaStore.srcInBackup("src/file.bin"));
    Assert.assertFalse(metaStore.srcInBackup("/tmp/dest/logs"));
    Assert.assertFalse(metaStore.srcInBackup("src/another_file"));
    Assert.assertFalse(metaStore.srcInBackup("src/test_1/another_file"));
    Assert.assertFalse(metaStore.srcInBackup("src/test_2/another_dir/file.jpg"));
    Assert.assertFalse(metaStore.srcInBackup("src/test_/file.bin"));
    Assert.assertFalse(metaStore.srcInBackup("src/test_12/file.bin"));

    Assert.assertTrue(metaStore.srcInBackup("src/test_3/file.bin"));
    Assert.assertTrue(metaStore.srcInBackup("src/test_4/inner/another.bin"));
  }

  @Test
  public void testDeleteBackUpInfo() throws MetaStoreException {
    BackUpInfo backUpInfo1 = new BackUpInfo(1, "test1", "test1", 1);
    metaStore.insertBackUpInfo(backUpInfo1);
    Assert.assertTrue(metaStore.srcInBackup("test1/dfafdsaf"));
    Assert.assertFalse(metaStore.srcInBackup("test2"));
    metaStore.deleteBackUpInfo(1);

    Assert.assertTrue(metaStore.listAllBackUpInfo().isEmpty());

    metaStore.insertBackUpInfo(backUpInfo1);
    metaStore.deleteAllBackUpInfo();

    Assert.assertTrue(metaStore.listAllBackUpInfo().isEmpty());
  }

  @Test
  public void testInsertAndListAllClusterInfo() throws MetaStoreException {
    ClusterInfo clusterInfo1 = new ClusterInfo(1, "test1", "test1", "test1", "test1", "test1");
    ClusterInfo clusterInfo2 = new ClusterInfo(2, "test2", "test2", "test2", "test2", "test2");

    metaStore.insertClusterInfo(clusterInfo1);
    metaStore.insertClusterInfo(clusterInfo2);

    List<ClusterInfo> clusterInfos = metaStore.listAllClusterInfo();

    Assert.assertEquals(clusterInfo1, clusterInfos.get(0));
    Assert.assertEquals(clusterInfo2, clusterInfos.get(1));
  }

  @Test
  public void testGetClusterInfoById() throws MetaStoreException {
    ClusterInfo clusterInfo = new ClusterInfo(1, "test1", "test1", "test1", "test1", "test1");
    metaStore.insertClusterInfo(clusterInfo);

    Assert.assertEquals(clusterInfo, metaStore.getClusterInfoByCid(1));
  }

  @Test
  public void testDelectBackUpInfo() throws MetaStoreException {
    ClusterInfo clusterInfo = new ClusterInfo(1, "test1", "test1", "test1", "test1", "test1");
    metaStore.insertClusterInfo(clusterInfo);

    metaStore.deleteClusterInfo(1);

    Assert.assertTrue(metaStore.listAllClusterInfo().isEmpty());

    metaStore.insertClusterInfo(clusterInfo);
    metaStore.deleteAllClusterInfo();

    Assert.assertTrue(metaStore.listAllClusterInfo().isEmpty());
  }

  @Test
  public void testInsertSystemInfo() throws MetaStoreException {
    SystemInfo systemInfo = new SystemInfo("test", "test");
    metaStore.insertSystemInfo(systemInfo);
    Assert.assertEquals(systemInfo, metaStore.getSystemInfoByProperty("test"));
  }

  @Test
  public void testDeleteSystemInfo() throws MetaStoreException {
    SystemInfo systemInfo = new SystemInfo("test", "test");
    metaStore.insertSystemInfo(systemInfo);
    metaStore.deleteSystemInfo("test");

    Assert.assertTrue(metaStore.listAllSystemInfo().isEmpty());
  }

  @Test
  public void testUpdateSystemInfo() throws MetaStoreException {
    SystemInfo systemInfo = new SystemInfo("test", "test");
    metaStore.insertSystemInfo(systemInfo);
    SystemInfo newSystemInfo = new SystemInfo("test", "test1");
    metaStore.updateSystemInfo(newSystemInfo);
    Assert.assertEquals(newSystemInfo, metaStore.getSystemInfoByProperty("test"));
  }

  @Test
  public void testUpdateAndInsertSystemInfo() throws MetaStoreException {
    SystemInfo systemInfo = new SystemInfo("test", "test");
    metaStore.updateAndInsertIfNotExist(systemInfo);
    Assert.assertTrue(metaStore.containSystemInfo("test"));
    Assert.assertTrue(metaStore.getSystemInfoByProperty("test").equals(systemInfo));
  }

  @Test
  public void testInsertUpdateFileState() throws MetaStoreException {
    // Normal file
    FileState fileState = new NormalFileState("/test1");
    metaStore.insertUpdateFileState(fileState);
    Assert.assertEquals(fileState, metaStore.getFileState("/test1"));

    // Compression & Processing (without compression info)
    // fileState = new FileState("/test1", FileState.FileType.COMPRESSION,
    //     FileState.FileStage.PROCESSING);
    // metaStore.insertUpdateFileState(fileState);
    // Assert.assertEquals(fileState, metaStore.getFileState("/test1"));

    // Compression & Done (with compression info)
    int bufferSize = 1024;
    long originalLen = 100;
    long compressedLen = 50;
    Long[] originPos = {0L, 30L, 60L, 90L};
    Long[] compressedPos = {0L, 13L, 30L, 41L};
    fileState = new CompressionFileState("/test1", bufferSize, originalLen,
        compressedLen, originPos, compressedPos);
    metaStore.insertUpdateFileState(fileState);
    compareCompressionInfo(fileState, metaStore.getFileState("/test1"));
  }

  private void compareCompressionInfo(FileState fileState1, FileState fileState2) {
    Assert.assertEquals(fileState1, fileState2);
    Assert.assertTrue(fileState1 instanceof CompressionFileState);
    Assert.assertTrue(fileState2 instanceof CompressionFileState);
    CompressionFileState compressionFileState1 = (CompressionFileState) fileState1;
    CompressionFileState compressionFileState2 = (CompressionFileState) fileState2;
    Assert.assertEquals(compressionFileState1.getBufferSize(),
        compressionFileState2.getBufferSize());
    Assert.assertEquals(compressionFileState1.getOriginalLength(),
        compressionFileState2.getOriginalLength());
    Assert.assertEquals(compressionFileState1.getCompressedLength(),
        compressionFileState2.getCompressedLength());
    Assert.assertArrayEquals(compressionFileState1.getOriginalPos(),
        compressionFileState2.getOriginalPos());
    Assert.assertArrayEquals(compressionFileState1.getCompressedPos(),
        compressionFileState2.getCompressedPos());
  }

  @Test
  public void testDeleteFileState() throws MetaStoreException {
    // Normal file
    FileState fileState1 = new NormalFileState("/test1");
    metaStore.insertUpdateFileState(fileState1);
    Assert.assertEquals(fileState1, metaStore.getFileState("/test1"));

    // Compression & Processing (without compression info)
    // FileState fileState2 = new FileState("/test2", FileState.FileType.COMPRESSION,
    //     FileState.FileStage.PROCESSING);
    // metaStore.insertUpdateFileState(fileState2);
    // Assert.assertEquals(fileState2, metaStore.getFileState("/test2"));

    // Compression & Done (with compression info)
    int bufferSize = 1024;
    long originalLen = 100;
    long compressedLen = 50;
    Long[] originPos = {0L, 30L, 60L, 90L};
    Long[] compressedPos = {0L, 13L, 30L, 41L};
    FileState fileState3 = new CompressionFileState("/test3", bufferSize, originalLen,
        compressedLen, originPos, compressedPos);
    metaStore.insertUpdateFileState(fileState3);
    compareCompressionInfo(fileState3, metaStore.getFileState("/test3"));

    // Delete /test3
    metaStore.deleteFileState("/test3");
    Assert.assertNull(metaStore.getCompressionInfo("/test3"));
    Assert.assertEquals(new NormalFileState("/test3"), metaStore.getFileState("/test3"));

    // Delete all
    metaStore.deleteAllFileState();
    Assert.assertEquals(new NormalFileState("/test1"), metaStore.getFileState("/test1"));
    Assert.assertEquals(new NormalFileState("/test2"), metaStore.getFileState("/test2"));
    Assert.assertEquals(new NormalFileState("/test3"), metaStore.getFileState("/test3"));
  }
}
