/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.engine.action;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.action.ActionException;
import org.smartdata.action.ActionRegistry;
import org.smartdata.action.SmartAction;
import org.smartdata.hdfs.action.move.AbstractMoveFileAction;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.CmdletDescriptor;
import org.smartdata.model.CmdletInfo;
import org.smartdata.model.DetailedFileAction;
import org.smartdata.model.LaunchAction;
import org.smartdata.protocol.message.ActionStatus;
import org.smartdata.server.engine.cmdlet.CmdletManagerContext;
import org.smartdata.server.engine.cmdlet.InMemoryRegistry;
import org.smartdata.server.engine.model.ActionGroup;
import org.smartdata.server.engine.model.DetailedFileActionGroup;

public class ActionInfoHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ActionInfoHandler.class);

  private final MetaStore metaStore;
  private AtomicLong maxActionId;
  // todo remove it after zeppelin web removal
  private ActionGroup tmpActions = new ActionGroup();

  private final InMemoryRegistry inMemoryRegistry;

  public ActionInfoHandler(CmdletManagerContext context) {
    this.metaStore = context.getMetaStore();
    this.inMemoryRegistry = context.getInMemoryRegistry();
  }

  public void init() throws IOException {
    LOG.info("Initializing ...");
    try {
      maxActionId = new AtomicLong(metaStore.getMaxActionId());

      LOG.info("Initialized.");
    } catch (MetaStoreException e) {
      LOG.error("DB Connection error! Failed to get Max CmdletId/ActionId!", e);
      throw new IOException(e);
    } catch (Exception t) {
      throw new IOException(t);
    }
  }

  public LaunchAction createLaunchAction(ActionInfo actionInfo) {
    Map<String, String> args = new HashMap<>(actionInfo.getArgs());
    return new LaunchAction(
        actionInfo.getActionId(),
        actionInfo.getActionName(),
        args);
  }

  public ActionInfo getActionInfo(long actionID) throws IOException {
    ActionInfo actionInfo = inMemoryRegistry.getUnfinishedAction(actionID);
    try {
      return actionInfo == null
          ? metaStore.getActionById(actionID)
          : actionInfo;
    } catch (MetaStoreException e) {
      LOG.error("ActionId -> [ {} ], get ActionInfo from DB error", actionID, e);
      throw new IOException(e);
    }
  }

  public List<ActionInfo> listNewCreatedActions(String actionName,
                                                int actionNum) throws IOException {
    try {
      return metaStore.getNewCreatedActions(actionName, actionNum);
    } catch (MetaStoreException e) {
      LOG.error("ActionName -> [ {} ], list ActionInfo from DB error", actionName, e);
      throw new IOException(e);
    }
  }

  public List<ActionInfo> listNewCreatedActions(int actionNum) throws IOException {
    try {
      Map<Long, ActionInfo> actionInfos = new HashMap<>();
      for (ActionInfo info : metaStore.getNewCreatedActions(actionNum)) {
        actionInfos.put(info.getActionId(), info);
      }
      actionInfos.putAll(inMemoryRegistry.getUnfinishedActions());
      return Lists.newArrayList(actionInfos.values());
    } catch (MetaStoreException e) {
      LOG.error("Get Finished Actions from DB error", e);
      throw new IOException(e);
    }
  }

  public ActionGroup listActions(long pageIndex, long numPerPage,
                                 List<String> orderBy, List<Boolean> isDesc) throws MetaStoreException {
    if (pageIndex == Long.parseLong("0")) {
      if (tmpActions.getTotalNumOfActions() != 0) {
        return tmpActions;
      } else {
        pageIndex = 1;
      }
    }
    List<ActionInfo> infos = metaStore.listPageAction((pageIndex - 1) * numPerPage,
        numPerPage, orderBy, isDesc);
    for (ActionInfo info : infos) {
      ActionInfo memInfo = inMemoryRegistry.getUnfinishedAction(info.getActionId());
      if (memInfo != null) {
        info.setCreateTime(memInfo.getCreateTime());
        info.setProgress(memInfo.getProgress());
      }
    }
    tmpActions = new ActionGroup(infos, metaStore.getCountOfAllAction());
    return tmpActions;
  }

  public ActionGroup searchAction(String path, long pageIndex, long numPerPage,
                                  List<String> orderBy, List<Boolean> isDesc) throws IOException {
    try {
      if (pageIndex == 0) {
        if (tmpActions.getTotalNumOfActions() != 0) {
          return tmpActions;
        }
        pageIndex = 1;
      }

      long[] total = new long[1];
      String escapedPath = path.replaceAll("[%_\"/']", "/$0");
      List<ActionInfo> infos = metaStore.searchAction(escapedPath, (pageIndex - 1) * numPerPage,
          numPerPage, orderBy, isDesc, total);
      for (ActionInfo info : infos) {
        LOG.debug("[metaStore search] " + info.getActionName());
        ActionInfo memInfo = inMemoryRegistry.getUnfinishedAction(info.getActionId());
        if (memInfo != null) {
          info.setCreateTime(memInfo.getCreateTime());
          info.setProgress(memInfo.getProgress());
        }
      }
      tmpActions = new ActionGroup(infos, total[0]);
      return tmpActions;
    } catch (MetaStoreException e) {
      LOG.error("Search [ {} ], Get Finished Actions by search from DB error", path, e);
      throw new IOException(e);
    }
  }

  public List<ActionInfo> getActions(List<Long> aids) throws IOException {
    try {
      return metaStore.getActions(aids);
    } catch (MetaStoreException e) {
      LOG.error("Get Actions by aid list [{}] from DB error", aids.toString());
      throw new IOException(e);
    }
  }

  public List<ActionInfo> getActions(long rid, int size) throws IOException {
    try {
      return metaStore.getActions(rid, size);
    } catch (MetaStoreException e) {
      LOG.error("RuleId -> [ {} ], Get Finished Actions by rid and size from DB error", rid, e);
      throw new IOException(e);
    }
  }

  public DetailedFileActionGroup getFileActions(long rid,
                                                long pageIndex,
                                                long numPerPage)
      throws MetaStoreException {
    List<DetailedFileAction> detailedFileActions = metaStore.listFileActions(rid,
        (pageIndex - 1) * numPerPage, numPerPage);
    return new DetailedFileActionGroup(detailedFileActions, metaStore.getNumFileAction(rid));
  }

  public List<DetailedFileAction> getFileActions(long rid, int size) throws IOException {
    try {
      return metaStore.listFileActions(rid, size);
    } catch (MetaStoreException e) {
      LOG.error("RuleId -> [ {} ], Get Finished Actions by rid and size from DB error", rid, e);
      throw new IOException(e);
    }
  }

  public ActionInfo updateActionStatus(long actionId, ActionStatus status) {
    return inMemoryRegistry.updateAction(actionId,
        actionInfo -> updateActionStatusInternal(actionInfo, status));
  }

  public void updateActionStatusInternal(ActionInfo actionInfo, ActionStatus status) {
    if (actionInfo.isFinished()) {
      return;
    }

    actionInfo.setLog(status.getLog());
    actionInfo.setResult(status.getResult());
    if (!status.isFinished()) {
      actionInfo.setProgress(status.getPercentage());
      actionInfo.setFinishTime(System.currentTimeMillis());
    } else {
      actionInfo.setProgress(1.0F);
      actionInfo.setFinished(true);
      actionInfo.setCreateTime(status.getStartTime());
      actionInfo.setFinishTime(status.getFinishTime());
      if (status.getThrowable() != null) {
        actionInfo.setSuccessful(false);
      } else {
        actionInfo.setSuccessful(true);
        updateStorageIfNeeded(actionInfo);
      }
    }
  }

  //Todo: remove this implementation
  private void updateStorageIfNeeded(ActionInfo info) {
    SmartAction action;
    try {
      action = ActionRegistry.createAction(info.getActionName());
    } catch (ActionException e) {
      LOG.error("Failed to create action from {}", info, e);
      return;
    }
    if (action instanceof AbstractMoveFileAction) {
      String policy = ((AbstractMoveFileAction) action).getStoragePolicy();
      Map<String, String> args = info.getArgs();
      if (policy == null) {
        policy = args.get(AbstractMoveFileAction.STORAGE_POLICY);
      }
      String path = args.get(AbstractMoveFileAction.FILE_PATH);
      try {
        String result = info.getResult();
        result = result == null ? "" : result;
        if (!result.contains("UpdateStoragePolicy=false")) {
          metaStore.updateFileStoragePolicy(path, policy);
        }
      } catch (MetaStoreException e) {
        LOG.error("Failed to update storage policy {} for file {}", policy, path, e);
      }
    }
  }

  public List<ActionInfo> createActionInfos(
      CmdletDescriptor cmdletDescriptor, CmdletInfo cmdletInfo) throws IOException {

    validateActionNames(cmdletDescriptor);

    return IntStream.range(0, cmdletDescriptor.getActionSize())
        .mapToObj(actionIdx ->
            createInitialActionInfo(cmdletDescriptor, cmdletInfo, actionIdx))
        .collect(Collectors.toList());
  }

  public ActionInfo createInitialActionInfo(
      CmdletDescriptor cmdletDescriptor, CmdletInfo cmdletInfo, int actionIndex) {
    return ActionInfo.newBuilder()
        .setActionId(maxActionId.getAndIncrement())
        .setCmdletId(cmdletInfo.getCid())
        .setCreateTime(cmdletInfo.getGenerateTime())
        .setActionName(cmdletDescriptor.getActionName(actionIndex))
        .setArgs(cmdletDescriptor.getActionArgs(actionIndex))
        .build();
  }

  /**
   * Check if action names in cmdletDescriptor are correct.
   */
  private void validateActionNames(CmdletDescriptor cmdletDescriptor) throws IOException {
    List<String> unknownActions = cmdletDescriptor.getActionNames()
        .stream()
        .filter(name -> !ActionRegistry.registeredAction(name))
        .collect(Collectors.toList());

    if (!unknownActions.isEmpty()) {
      throw new IOException("Unknown actions used in cmdlet: " + unknownActions);
    }
  }
}
