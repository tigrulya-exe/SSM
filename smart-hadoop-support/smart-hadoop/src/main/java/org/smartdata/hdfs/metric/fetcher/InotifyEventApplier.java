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

import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.action.SyncAction;
import org.smartdata.conf.SmartConf;
import org.smartdata.hdfs.CompatibilityHelperLoader;
import org.smartdata.hdfs.HadoopUtil;
import org.smartdata.hdfs.action.CopyFileAction;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.FileDiff;
import org.smartdata.model.FileDiffType;
import org.smartdata.model.FileInfo;
import org.smartdata.model.FileInfoDiff;
import org.smartdata.model.PathChecker;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.smartdata.action.SyncAction.BASE_OPERATION;
import static org.smartdata.utils.PathUtil.addPathSeparator;

/**
 * This is a very preliminary and buggy applier, can further enhance by referring to
 * {@link org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader}
 */
public class InotifyEventApplier {
  private static final Logger LOG =
      LoggerFactory.getLogger(InotifyEventApplier.class);

  private static final String ROOT_DIRECTORY = "/";

  private final MetaStore metaStore;
  private final PathChecker pathChecker;
  private final DFSClient client;

  private NamespaceFetcher namespaceFetcher;

  public InotifyEventApplier(MetaStore metaStore, DFSClient client) {
    this(new SmartConf(), metaStore, client);
  }

  public InotifyEventApplier(SmartConf conf, MetaStore metaStore, DFSClient client, NamespaceFetcher namespaceFetcher) {
    this(conf, metaStore, client);
    this.namespaceFetcher = namespaceFetcher;
  }

  public InotifyEventApplier(SmartConf conf, MetaStore metaStore, DFSClient client) {
    this.metaStore = metaStore;
    this.client = client;
    this.pathChecker = new PathChecker(conf);
  }

  public void apply(List<Event> events) throws IOException, InterruptedException {
    for (Event event : events) {
      apply(event);
    }
  }

  public void apply(Event[] events) throws IOException, InterruptedException {
    this.apply(Arrays.asList(events));
  }

  private void apply(Event event) throws IOException, InterruptedException {
    String path;
    String srcPath, dstPath;
    LOG.debug("Handle event {}", event);

    // we already filtered events in the fetch tasks, so we can skip
    // event's path check here
    switch (event.getEventType()) {
      case CREATE:
        path = ((Event.CreateEvent) event).getPath();
        LOG.trace("event type: {}, path: {}", event.getEventType().name(), path);
        applyCreate((Event.CreateEvent) event);
        break;
      case CLOSE:
        path = ((Event.CloseEvent) event).getPath();
        LOG.trace("event type: {}, path: {}", event.getEventType().name(), path);
        applyClose((Event.CloseEvent) event);
        break;
      case RENAME:
        srcPath = ((Event.RenameEvent) event).getSrcPath();
        dstPath = ((Event.RenameEvent) event).getDstPath();
        LOG.trace("event type: {}, src path: {}, dest path: {}",
            event.getEventType().name(), srcPath, dstPath);
        applyRename((Event.RenameEvent) event);
        break;
      case METADATA:
        // The property dfs.namenode.accesstime.precision in HDFS's configuration controls
        // the precision of access time. Its default value is 1h. To avoid missing a
        // MetadataUpdateEvent for updating access time, a smaller value should be set.
        path = ((Event.MetadataUpdateEvent) event).getPath();
        LOG.trace("event type: {}, path: {}", event.getEventType().name(), path);
        applyMetadataUpdate((Event.MetadataUpdateEvent) event);
        break;
      case APPEND:
        path = ((Event.AppendEvent) event).getPath();
        LOG.trace("event type: {}, path: {}", event.getEventType().name(), path);
        // do nothing
        break;
      case UNLINK:
        path = ((Event.UnlinkEvent) event).getPath();
        LOG.trace("event type: {}, path: {}", event.getEventType().name(), path);
        applyUnlink((Event.UnlinkEvent) event);
    }
  }

  //Todo: times and ec policy id, etc.
  // TODO we need to create FileInfo from create event, not from HDFS client,
  // because it can be either deleted or renamed at the moment
  // of fetching info from HDFS
  private void applyCreate(Event.CreateEvent createEvent) throws IOException, MetaStoreException {
    FileInfo fileInfo = getFileInfo(createEvent.getPath());
    if (fileInfo == null) {
      LOG.warn("Skipping create event for file {}", createEvent.getPath());
      return;
    }

    applyCreateFileDiff(fileInfo);
    metaStore.deleteFileByPath(fileInfo.getPath(), false);
    metaStore.deleteFileState(fileInfo.getPath());
    metaStore.insertFile(fileInfo);
  }

  private void applyRenameIgnoredFile(Event.RenameEvent renameEvent) throws IOException, MetaStoreException {
    FileInfo fileInfo = getFileInfo(renameEvent.getDstPath());
    if (fileInfo == null) {
      return;
    }

    applyCreateFileDiff(fileInfo);
    metaStore.deleteFileByPath(fileInfo.getPath(), false);
    metaStore.insertFile(fileInfo);
    metaStore.renameFile(renameEvent.getSrcPath(), renameEvent.getDstPath(), fileInfo.isDir());
  }

  private FileInfo getFileInfo(String path) throws IOException {
    HdfsFileStatus fileStatus = client.getFileInfo(path);
    if (fileStatus == null) {
      LOG.debug("Can not get HdfsFileStatus for file " + path);
      return null;
    }

    return HadoopUtil.convertFileStatus(fileStatus, path);
  }

  private void applyCreateFileDiff(FileInfo fileInfo) throws MetaStoreException {
    if (inBackup(fileInfo.getPath())) {
      if (fileInfo.isDir()) {
        FileDiff fileDiff = new FileDiff(FileDiffType.MKDIR);
        fileDiff.setSrc(fileInfo.getPath());
        metaStore.insertFileDiff(fileDiff);
        return;
      }
      FileDiff fileDiff = new FileDiff(FileDiffType.APPEND);
      fileDiff.setSrc(fileInfo.getPath());
      fileDiff.getParameters().put("-offset", String.valueOf(0));
      // Note that "-length 0" means create an empty file
      fileDiff.getParameters()
          .put("-length", String.valueOf(fileInfo.getLength()));
      // TODO add support in CopyFileAction or split into two file diffs
      //add modification_time and access_time to filediff
      fileDiff.getParameters().put("-mtime", "" + fileInfo.getModificationTime());
      // fileDiff.getParameters().put("-atime", "" + fileInfo.getAccessTime());
      //add owner to filediff
      fileDiff.getParameters().put("-owner", "" + fileInfo.getOwner());
      fileDiff.getParameters().put("-group", "" + fileInfo.getGroup());
      //add Permission to filediff
      fileDiff.getParameters().put("-permission", "" + fileInfo.getPermission());
      //add replication count to file diff
      fileDiff.getParameters().put("-replication", "" + fileInfo.getBlockReplication());
      metaStore.insertFileDiff(fileDiff);
    }
  }

  private boolean inBackup(String src) throws MetaStoreException {
    return metaStore.srcInBackup(src);
  }

  //Todo: should update mtime? atime?
  private void applyClose(Event.CloseEvent closeEvent) throws MetaStoreException {
    FileDiff fileDiff = new FileDiff(FileDiffType.APPEND);
    fileDiff.setSrc(closeEvent.getPath());
    long newLen = closeEvent.getFileSize();
    long currLen;
    // TODO make sure offset is correct
    if (inBackup(closeEvent.getPath())) {
      FileInfo fileInfo = metaStore.getFile(closeEvent.getPath());
      if (fileInfo == null) {
        // TODO add metadata
        currLen = 0;
      } else {
        currLen = fileInfo.getLength();
      }
      if (currLen != newLen) {
        fileDiff.getParameters().put("-offset", String.valueOf(currLen));
        fileDiff.getParameters()
            .put("-length", String.valueOf(newLen - currLen));
        metaStore.insertFileDiff(fileDiff);
      }
    }
    FileInfoDiff fileInfoDiff = new FileInfoDiff()
        .setLength(closeEvent.getFileSize())
        .setModificationTime(closeEvent.getTimestamp());
    metaStore.updateFileByPath(closeEvent.getPath(), fileInfoDiff);
  }

  private void applyRename(Event.RenameEvent renameEvent)
      throws IOException, InterruptedException {
    String src = renameEvent.getSrcPath();
    String dest = renameEvent.getDstPath();

    if (pathChecker.isIgnored(src)) {
      applyRenameIgnoredFile(renameEvent);
      return;
    }

    HdfsFileStatus destHdfsStatus = client.getFileInfo(dest);
    FileInfo info = metaStore.getFile(src);

    // For backup data to use.
    generateFileDiff(renameEvent);

    // The dest path which the src is renamed to should be checked in file table
    // to avoid duplicated record for one same path.
    FileInfo destInfo = metaStore.getFile(dest);
    if (destInfo != null) {
      metaStore.deleteFileByPath(dest, false);
    }
    // src is not in file table because it is not fetched or other reason
    if (info == null) {
      // TODO get rid of repeating namespace fetching
      // by achieving full consistency of metastore fs namespace
      // by saving all files including ignored ones
      if (destHdfsStatus != null) {
        namespaceFetcher.startFetch(dest);
        while (!namespaceFetcher.fetchFinished()) {
          LOG.info("Fetching the files under " + dest);
          Thread.sleep(100);
        }
        namespaceFetcher.stop();
      }
      return;
    }

    // if the dest is ignored, delete src info from file table
    // TODO: tackle with file_state and small_state
    if (pathChecker.isIgnored(dest)) {
      // fuzzy matching is used to delete content under the dir
      metaStore.deleteFileByPath(src, true);
      return;
    }

    metaStore.renameFile(src, dest, info.isDir());
  }

  private void generateFileDiff(Event.RenameEvent renameEvent) throws MetaStoreException {
    boolean srcInBackup = inBackup(renameEvent.getSrcPath());
    boolean destInBackup = inBackup(renameEvent.getDstPath());

    if (!srcInBackup && !destInBackup) {
      return;
    }

    FileInfo srcFileInfo = metaStore.getFile(renameEvent.getSrcPath());
    if (srcFileInfo == null) {
      LOG.warn(
          "Inconsistency in metastore and HDFS namespace, file not found: {}",
          renameEvent.getSrcPath());
      return;
    }

    List<FileDiff> fileDiffs;
    if (srcInBackup) {
      if (destInBackup) {
        // if both src and dest are in backup directory,
        // then generate rename diffs for all content under src
        fileDiffs = visitFileRecursively(srcFileInfo, renameEvent, this::buildRenameFileDiff);
      } else {
        // if src is in backup directory and dest isn't,
        // then simply delete all files under src on remote cluster
        fileDiffs = Collections.singletonList(getDeleteFileDiff(srcFileInfo.getPath()));
      }
    } else {
      // if dest is in backup directory and src isn't,
      // then simply copy files under dest to remote cluster
      fileDiffs = visitFileRecursively(srcFileInfo, renameEvent, this::buildCreateFileDiff);
    }

    if (fileDiffs.isEmpty()) {
      LOG.warn(
          "Inconsistency in metastore and HDFS namespace, file not found: {}",
          renameEvent.getSrcPath());
      return;
    }
    // set first diff as base rename operation
    fileDiffs.get(0).setParameter(BASE_OPERATION, "");
    metaStore.insertFileDiffs(fileDiffs);
  }

  private <C, T> List<T> visitFileRecursively(
      FileInfo srcFileInfo, C context,
      BiFunction<FileInfo, C, T> diffProducer)
      throws MetaStoreException {
    List<T> results = new ArrayList<>();
    results.add(diffProducer.apply(srcFileInfo, context));

    if (srcFileInfo.isDir()) {
      metaStore.getFilesByPrefixInOrder(addPathSeparator(srcFileInfo.getPath()))
          .stream()
          .map(fileInfo -> diffProducer.apply(fileInfo, context))
          .forEach(results::add);
    }

    return results;
  }

  private FileDiff buildRenameFileDiff(FileInfo fileInfo, Event.RenameEvent renameEvent) {
    FileDiff fileDiff = new FileDiff(FileDiffType.RENAME);
    fileDiff.setSrc(fileInfo.getPath());
    fileDiff.getParameters().put(
        SyncAction.DEST,
        fileInfo.getPath().replaceFirst(
            renameEvent.getSrcPath(),
            renameEvent.getDstPath()));
    return fileDiff;
  }

  private FileDiff buildCreateFileDiff(FileInfo fileInfo, Event.RenameEvent renameEvent) {
    if (fileInfo.isDir()) {
      FileDiff fileDiff = new FileDiff(FileDiffType.MKDIR);
      fileDiff.setSrc(fileInfo.getPath());
      return fileDiff;
    }

    FileDiff fileDiff = new FileDiff(FileDiffType.APPEND);
    fileDiff.setSrc(fileInfo.getPath()
        .replaceFirst(renameEvent.getSrcPath(), renameEvent.getDstPath()));
    fileDiff.getParameters().put(
        CopyFileAction.OFFSET_INDEX, String.valueOf(0));
    fileDiff.getParameters()
        .put(CopyFileAction.LENGTH, String.valueOf(fileInfo.getLength()));
    return fileDiff;
  }

  private void applyMetadataUpdate(Event.MetadataUpdateEvent metadataUpdateEvent) throws MetaStoreException {

    FileDiff fileDiff = null;
    if (inBackup(metadataUpdateEvent.getPath())) {
      fileDiff = new FileDiff(FileDiffType.METADATA);
      fileDiff.setSrc(metadataUpdateEvent.getPath());
    }
    FileInfoDiff fileInfoUpdate = new FileInfoDiff();
    switch (metadataUpdateEvent.getMetadataType()) {
      case TIMES:
        if (metadataUpdateEvent.getMtime() > 0) {
          if (fileDiff != null) {
            fileDiff.getParameters().put("-mtime", String.valueOf(metadataUpdateEvent.getMtime()));
            // fileDiff.getParameters().put("-access_time", "" + metadataUpdateEvent.getAtime());
            metaStore.insertFileDiff(fileDiff);
          }
          fileInfoUpdate.setModificationTime(metadataUpdateEvent.getMtime());
        }
        if (metadataUpdateEvent.getAtime() > 0) {
          // if (fileDiff != null) {
          //   fileDiff.getParameters().put("-access_time", "" + metadataUpdateEvent.getAtime());
          //   metaStore.insertFileDiff(fileDiff);
          // }
          fileInfoUpdate.setAccessTime(metadataUpdateEvent.getAtime());
        }
        break;
      case OWNER:
        if (fileDiff != null) {
          fileDiff.getParameters().put("-owner", metadataUpdateEvent.getOwnerName());
          metaStore.insertFileDiff(fileDiff);
        }
        fileInfoUpdate.setOwner(metadataUpdateEvent.getOwnerName())
            .setGroup(metadataUpdateEvent.getGroupName());
        break;
      case PERMS:
        if (fileDiff != null) {
          fileDiff.getParameters().put("-permission", "" + metadataUpdateEvent.getPerms().toShort());
          metaStore.insertFileDiff(fileDiff);
        }
        fileInfoUpdate.setPermission(metadataUpdateEvent.getPerms().toShort());
        break;
      case REPLICATION:
        if (fileDiff != null) {
          fileDiff.getParameters().put("-replication", "" + metadataUpdateEvent.getReplication());
          metaStore.insertFileDiff(fileDiff);
        }
        fileInfoUpdate.setBlockReplication((short) metadataUpdateEvent.getReplication());
        break;
      case XATTRS:
        final String EC_POLICY = "hdfs.erasurecoding.policy";
        //Todo
        if (LOG.isDebugEnabled()) {
          String message = metadataUpdateEvent.getxAttrs()
              .stream()
              .map(XAttr::toString)
              .collect(Collectors.joining("\n"));
          LOG.debug(message);
        }
        // The following code should be executed merely on HDFS3.x.
        for (XAttr xAttr : metadataUpdateEvent.getxAttrs()) {
          if (xAttr.getName().equals(EC_POLICY)) {
            try {
              String ecPolicyName = WritableUtils.readString(
                  new DataInputStream(new ByteArrayInputStream(xAttr.getValue())));
              byte ecPolicyId = CompatibilityHelperLoader.getHelper().
                  getErasureCodingPolicyByName(client, ecPolicyName);
              if (ecPolicyId == (byte) -1) {
                LOG.error("Unrecognized EC policy for updating!");
              }
              fileInfoUpdate.setErasureCodingPolicy(ecPolicyId);
              break;
            } catch (IOException ex) {
              LOG.error("Error occurred for updating ecPolicy!", ex);
            }
          }
        }
        break;
      case ACLS:
        return;
    }
    metaStore.updateFileByPath(metadataUpdateEvent.getPath(), fileInfoUpdate);
  }

  private void applyUnlink(Event.UnlinkEvent unlinkEvent) throws MetaStoreException {
    // delete root, i.e., /
    if (ROOT_DIRECTORY.equals(unlinkEvent.getPath())) {
      LOG.warn("Deleting root directory!!!");
      insertDeleteDiff(ROOT_DIRECTORY);
      metaStore.unlinkRootDirectory();
      return;
    }

    String path = unlinkEvent.getPath();
    // file has no "/" appended in the metaStore
    FileInfo fileInfo = metaStore.getFile(path.endsWith("/") ?
        path.substring(0, path.length() - 1) : path);

    if (fileInfo != null) {
      insertDeleteDiff(unlinkEvent.getPath());
      metaStore.unlinkFile(unlinkEvent.getPath(), fileInfo.isDir());
    }
  }

  private void insertDeleteDiff(String path) throws MetaStoreException {
    if (inBackup(path)) {
      FileDiff deleteFileDiff = getDeleteFileDiff(path);
      metaStore.insertFileDiff(deleteFileDiff);
    }
  }

  private FileDiff getDeleteFileDiff(String path) {
    FileDiff fileDiff = new FileDiff(FileDiffType.DELETE);
    fileDiff.setSrc(path);
    return fileDiff;
  }
}
