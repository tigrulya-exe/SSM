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
package org.smartdata.hdfs.metric.fetcher;

import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.smartdata.conf.SmartConf;
import org.smartdata.hdfs.HadoopUtil;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.model.BackUpInfo;
import org.smartdata.model.FileDiff;
import org.smartdata.model.FileInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestInotifyEventApplier extends TestDaoBase {
  @Test
  public void testApplier() throws Exception {
    DFSClient client = Mockito.mock(DFSClient.class);

    FileInfo root = HadoopUtil.convertFileStatus(getDummyDirStatus("/", 1000), "/");
    metaStore.insertFile(root, false);
    BackUpInfo backUpInfo = new BackUpInfo(1L, "/file", "remote/dest/", 10);
    metaStore.insertBackUpInfo(backUpInfo);
    InotifyEventApplier applier = new InotifyEventApplier(new SmartConf(), metaStore, client);

    Event.CreateEvent createEvent =
        new Event.CreateEvent.Builder()
            .iNodeType(Event.CreateEvent.INodeType.FILE)
            .ctime(1)
            .defaultBlockSize(1024)
            .groupName("cg1")
            .overwrite(true)
            .ownerName("user1")
            .path("/file")
            .perms(new FsPermission("777"))
            .replication(3)
            .build();
    HdfsFileStatus status1 = new HdfsFileStatus.Builder()
        .length(0)
        .isdir(false)
        .replication(1)
        .blocksize(1024)
        .mtime(0)
        .atime(0)
        .perm(new FsPermission("777"))
        .owner("owner")
        .group("group")
        .symlink(new byte[0])
        .path(new byte[0])
        .fileId(1010)
        .children(0)
        .feInfo(null)
        .storagePolicy((byte) 0)
        .build();
    Mockito.when(client.getFileInfo(ArgumentMatchers.startsWith("/file"))).thenReturn(status1);
    Mockito.when(client.getFileInfo(ArgumentMatchers.startsWith("/dir")))
        .thenReturn(getDummyDirStatus("", 1010));
    applier.apply(new Event[]{createEvent});

    FileInfo result1 = metaStore.getFile().get(1);
    Assert.assertEquals("/file", result1.getPath());
    Assert.assertEquals(1L, result1.getFileId());
    Assert.assertEquals(511, result1.getPermission());

    Event close = new Event.CloseEvent("/file", 1024, 0);
    applier.apply(new Event[]{close});
    FileInfo result2 = metaStore.getFile().get(1);
    Assert.assertEquals(1024, result2.getLength());
    Assert.assertEquals(0L, result2.getModificationTime());

    Event meta =
        new Event.MetadataUpdateEvent.Builder()
            .path("/file")
            .metadataType(Event.MetadataUpdateEvent.MetadataType.TIMES)
            .mtime(2)
            .atime(3)
            .replication(4)
            .ownerName("user2")
            .groupName("cg2")
            .build();
    applier.apply(new Event[]{meta});
    FileInfo result4 = metaStore.getFile().get(1);
    Assert.assertEquals(result4.getAccessTime(), 3);
    Assert.assertEquals(result4.getModificationTime(), 2);

    Event meta1 =
        new Event.MetadataUpdateEvent.Builder()
            .path("/file")
            .metadataType(Event.MetadataUpdateEvent.MetadataType.OWNER)
            .ownerName("user1")
            .groupName("cg1")
            .build();
    applier.apply(new Event[]{meta1});
    result4 = metaStore.getFile().get(1);
    Assert.assertEquals(result4.getOwner(), "user1");
    Assert.assertEquals(result4.getGroup(), "cg1");
    // check metadata event didn't flush other FileInfo fields
    Assert.assertEquals(1L, result4.getFileId());
    Assert.assertEquals(new FsPermission("777").toShort(), result4.getPermission());

    Event.CreateEvent createEvent2 =
        new Event.CreateEvent.Builder()
            .iNodeType(Event.CreateEvent.INodeType.DIRECTORY)
            .ctime(1)
            .groupName("cg1")
            .overwrite(true)
            .ownerName("user1")
            .path("/dir")
            .perms(new FsPermission("777"))
            .replication(3)
            .build();
    Event.CreateEvent createEvent3 =
        new Event.CreateEvent.Builder()
            .iNodeType(Event.CreateEvent.INodeType.FILE)
            .ctime(1)
            .groupName("cg1")
            .overwrite(true)
            .ownerName("user1")
            .path("/dir/file")
            .perms(new FsPermission("777"))
            .replication(3)
            .build();
    Event rename =
        new Event.RenameEvent.Builder().dstPath("/dir2").srcPath("/dir").timestamp(5).build();

    applier.apply(new Event[]{createEvent2, createEvent3, rename});
    List<FileInfo> result5 = metaStore.getFile();
    List<String> expectedPaths = Arrays.asList("/dir2", "/dir2/file", "/file");
    List<String> actualPaths = new ArrayList<>();
    for (FileInfo s : result5) {
      actualPaths.add(s.getPath());
    }
    Collections.sort(actualPaths);
    Assert.assertEquals(4, actualPaths.size());
    Assert.assertTrue(actualPaths.containsAll(expectedPaths));

    Event unlink = new Event.UnlinkEvent.Builder().path("/").timestamp(6).build();
    applier.apply(new Event[]{unlink});
    Thread.sleep(1200);
    Assert.assertEquals(metaStore.getFile().size(), 0);
    System.out.println("Files in table " + metaStore.getFile().size());
    List<FileDiff> fileDiffList = metaStore.getPendingDiff();
    Assert.assertEquals(4, fileDiffList.size());
  }

  @Test
  public void testApplierCreateEvent() throws Exception {
    DFSClient client = Mockito.mock(DFSClient.class);
    InotifyEventApplier applier = new InotifyEventApplier(new SmartConf(), metaStore, client);

    BackUpInfo backUpInfo = new BackUpInfo(1L, "/file1", "remote/dest/", 10);
    metaStore.insertBackUpInfo(backUpInfo);

    HdfsFileStatus status1 = new HdfsFileStatus.Builder()
        .length(0)
        .isdir(false)
        .replication(2)
        .blocksize(123)
        .mtime(0)
        .atime(0)
        .perm(new FsPermission("777"))
        .owner("test")
        .group("group")
        .symlink(new byte[0])
        .path(new byte[0])
        .fileId(1010)
        .children(0)
        .feInfo(null)
        .storagePolicy((byte) 0)
        .build();
    Mockito.when(client.getFileInfo("/file1")).thenReturn(status1);

    List<Event> events = new ArrayList<>();
    Event.CreateEvent createEvent =
        new Event.CreateEvent.Builder().path("/file1").defaultBlockSize(123).ownerName("test")
            .replication(2)
            .perms(new FsPermission(FsAction.NONE, FsAction.NONE, FsAction.NONE)).build();
    events.add(createEvent);
    Mockito.when(client.getFileInfo("/file1")).thenReturn(status1);
    applier.apply(events);

    Assert.assertEquals("test", metaStore.getFile("/file1").getOwner());
    //judge file diff
    List<FileDiff> fileDiffs = metaStore.getFileDiffsByFileName("/file1");

    Assert.assertFalse(fileDiffs.isEmpty());
  }

  @Test
  public void testApplierRenameEvent() throws Exception {
    DFSClient client = Mockito.mock(DFSClient.class);
    SmartConf conf = new SmartConf();
    InotifyEventApplier applier = new InotifyEventApplier(conf, metaStore, client);

    FileInfo[] fileInfos = new FileInfo[]{
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dirfile", 7000), "/dirfile"),
        HadoopUtil.convertFileStatus(getDummyDirStatus("/dir", 8000), "/dir"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dir/file1", 8001), "/dir/file1"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dir/file2", 8002), "/dir/file2"),
        HadoopUtil.convertFileStatus(getDummyDirStatus("/dir2", 8100), "/dir2"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dir2/file1", 8101), "/dir2/file1"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dir2/file2", 8102), "/dir2/file2"),
        HadoopUtil.convertFileStatus(getDummyDirStatus("/dir/dir", 8200), "/dir/dir"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/dir/dir/f1", 8201), "/dir/dir/f1"),
        HadoopUtil.convertFileStatus(getDummyFileStatus("/file", 2000), "/file"),
    };
    metaStore.insertFiles(fileInfos, false);
    Mockito.when(client.getFileInfo("/dir1")).thenReturn(getDummyDirStatus("/dir1", 8000));
    Event.RenameEvent dirRenameEvent = new Event.RenameEvent.Builder()
        .srcPath("/dir")
        .dstPath("/dir1")
        .build();
    applier.apply(new Event[]{dirRenameEvent});
    Assert.assertNull(metaStore.getFile("/dir"));
    Assert.assertNull(metaStore.getFile("/dir/file1"));
    Assert.assertNotNull(metaStore.getFile("/dirfile"));
    Assert.assertNotNull(metaStore.getFile("/dir1"));
    Assert.assertNotNull(metaStore.getFile("/dir1/file1"));
    Assert.assertNotNull(metaStore.getFile("/dir1/dir/f1"));
    Assert.assertNotNull(metaStore.getFile("/dir2"));
    Assert.assertNotNull(metaStore.getFile("/dir2/file1"));
    Assert.assertNotNull(metaStore.getFile("/file"));

    List<Event> events = new ArrayList<>();
    Event.RenameEvent renameEvent = new Event.RenameEvent.Builder()
        .srcPath("/file1")
        .dstPath("/file2")
        .build();
    events.add(renameEvent);
    applier.apply(events);
    Assert.assertNull(metaStore.getFile("/file2"));

    events.clear();
    renameEvent = new Event.RenameEvent.Builder()
        .srcPath("/file")
        .dstPath("/file1")
        .build();
    events.add(renameEvent);
    applier.apply(events);
    FileInfo info2 = metaStore.getFile("/file");
    Assert.assertNull(info2);
    FileInfo info3 = metaStore.getFile("/file1");
    Assert.assertNotNull(info3);

    renameEvent = new Event.RenameEvent.Builder()
        .srcPath("/file1")
        .dstPath("/file2")
        .build();
    events.clear();
    events.add(renameEvent);
    applier.apply(events);
    FileInfo info4 = metaStore.getFile("/file1");
    FileInfo info5 = metaStore.getFile("/file2");
    Assert.assertTrue(info4 == null && info5 != null);
  }

  private HdfsFileStatus getDummyFileStatus(String file, long fid) {
    return doGetDummyStatus(file, fid, false);
  }

  private HdfsFileStatus getDummyDirStatus(String file, long fid) {
    return doGetDummyStatus(file, fid, true);
  }

  private HdfsFileStatus doGetDummyStatus(String file, long fid, boolean isdir) {
    return new HdfsFileStatus.Builder()
        .length(0)
        .isdir(isdir)
        .replication(1)
        .blocksize(1024)
        .mtime(0)
        .atime(0)
        .perm(new FsPermission("777"))
        .owner("owner")
        .group("group")
        .symlink(new byte[0])
        .path(file.getBytes())
        .fileId(fid)
        .children(0)
        .feInfo(null)
        .storagePolicy((byte) 0)
        .build();
  }
}
