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

package org.smartdata.server.engine.cmdlet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.metastore.MetaStore;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.CmdletDescriptor;
import org.smartdata.model.CmdletInfo;
import org.smartdata.model.CmdletState;
import org.smartdata.model.LaunchAction;
import org.smartdata.protocol.message.CmdletStatus;
import org.smartdata.protocol.message.LaunchCmdlet;
import org.smartdata.server.engine.ActiveServerInfo;
import org.smartdata.server.engine.action.ActionInfoHandler;
import org.smartdata.server.engine.model.CmdletGroup;

public class CmdletInfoHandler {
  private static final Logger LOG = LoggerFactory.getLogger(CmdletInfoHandler.class);

  private final MetaStore metaStore;
  private final ActionInfoHandler actionInfoHandler;
  private final InMemoryRegistry inMemoryRegistry;
  private AtomicLong maxCmdletId;


  public CmdletInfoHandler(CmdletManagerContext context, ActionInfoHandler actionInfoHandler) {
    this.metaStore = context.getMetaStore();
    this.inMemoryRegistry = context.getInMemoryRegistry();
    this.actionInfoHandler = actionInfoHandler;
  }

  public void init() throws IOException {
    LOG.info("Initializing ...");
    try {
      maxCmdletId = new AtomicLong(metaStore.getMaxCmdletId());
      LOG.info("Initialized.");
    } catch (MetaStoreException e) {
      LOG.error("DB Connection error! Failed to get Max CmdletId!", e);
      throw new IOException(e);
    }
  }

  public CmdletInfo createCmdletInfo(CmdletDescriptor cmdletDescriptor) {
    long submitTime = System.currentTimeMillis();
    return CmdletInfo.newBuilder()
        .setId(maxCmdletId.getAndIncrement())
        .setRuleId(cmdletDescriptor.getRuleId())
        .setState(CmdletState.PENDING)
        .setParameters(cmdletDescriptor.getCmdletString())
        .setGenerateTime(submitTime)
        .setStateChangedTime(submitTime)
        .setDeferredToTime(submitTime + cmdletDescriptor.getDeferIntervalMs())
        .build();
  }

  public LaunchCmdlet createLaunchCmdlet(CmdletInfo cmdletInfo) {
    if (cmdletInfo == null) {
      return null;
    }

    List<LaunchAction> launchActions = cmdletInfo.getAids()
        .stream()
        .map(inMemoryRegistry::getUnfinishedAction)
        .filter(Objects::nonNull)
        .map(actionInfoHandler::createLaunchAction)
        .collect(Collectors.toList());

    return new LaunchCmdlet(cmdletInfo.getCid(), launchActions);
  }

  public CmdletInfo getCmdletInfo(long cid) throws IOException {
    CmdletInfo cmdletInfo = inMemoryRegistry.getUnfinishedCmdlet(cid);
    try {
      return cmdletInfo == null
          ? metaStore.getCmdletById(cid)
          : cmdletInfo;
    } catch (MetaStoreException e) {
      LOG.error("CmdletId -> [ {} ], get CmdletInfo from DB error", cid, e);
      throw new IOException(e);
    }
  }

  public CmdletGroup listCmdletsInfo(
      long rid, long pageIndex, long numPerPage,
      List<String> orderBy, List<Boolean> isDesc) throws MetaStoreException {
    List<CmdletInfo> cmdlets = metaStore.listPageCmdlets(rid,
        (pageIndex - 1) * numPerPage, numPerPage, orderBy, isDesc);
    return new CmdletGroup(cmdlets, metaStore.getNumCmdletsByRid(rid));
  }

  public List<CmdletInfo> listCmdletsInfo(long rid, CmdletState cmdletState) throws IOException {
    List<CmdletInfo> result = new ArrayList<>();
    try {
      if (rid == -1) {
        result.addAll(metaStore.getCmdlets(null, null, cmdletState));
      } else {
        result.addAll(metaStore.getCmdlets(null, String.format("= %d", rid), cmdletState));
      }
    } catch (MetaStoreException e) {
      LOG.error("RuleId -> [ {} ], List CmdletInfo from DB error", rid, e);
      throw new IOException(e);
    }
    for (CmdletInfo info : inMemoryRegistry.getUnfinishedCmdlets().values()) {
      if (info.getRid() == rid && info.getState().equals(cmdletState)) {
        result.add(info);
      }
    }
    return result;
  }

  public List<CmdletInfo> listCmdletsInfo(long rid) throws IOException {
    Map<Long, CmdletInfo> result = new HashMap<>();
    try {
      String ridCondition = rid == -1 ? null : String.format("= %d", rid);
      for (CmdletInfo info : metaStore.getCmdlets(null, ridCondition, null)) {
        result.put(info.getCid(), info);
      }
    } catch (MetaStoreException e) {
      LOG.error("RuleId -> [ {} ], List CmdletInfo from DB error", rid, e);
      throw new IOException(e);
    }
    for (CmdletInfo info : inMemoryRegistry.getUnfinishedCmdlets().values()) {
      if (info.getRid() == rid) {
        result.put(info.getCid(), info);
      }
    }
    return Lists.newArrayList(result.values());
  }

  public void onCmdletFinished(CmdletInfo cmdletInfo, boolean success) {
    for (Long aid : cmdletInfo.getAids()) {

      inMemoryRegistry.updateAction(aid, actionInfo -> {
        actionInfo.setProgress(1.0F);
        actionInfo.setFinished(true);
        actionInfo.setCreateTime(cmdletInfo.getStateChangedTime());
        actionInfo.setFinishTime(cmdletInfo.getStateChangedTime());
        actionInfo.setExecHost(ActiveServerInfo.getInstance().getId());
        actionInfo.setSuccessful(success);
      });
    }
  }

  public void deleteCmdlet(long cmdletId) throws IOException {
    try {
      metaStore.deleteCmdlet(cmdletId);
      metaStore.deleteCmdletActions(cmdletId);
    } catch (MetaStoreException e) {
      LOG.error("CmdletId -> [ {} ], delete from DB error", cmdletId, e);
      throw new IOException(e);
    }
  }

  public void updateCmdletExecHost(long cmdletId, String host) throws IOException {
    List<Long> actionIds = Optional.ofNullable(getCmdletInfo(cmdletId))
        .map(CmdletInfo::getAids)
        .orElseGet(Collections::emptyList);

    for (long id : actionIds) {
      ActionInfo action = actionInfoHandler.getActionInfo(id);
      if (action != null) {
        action.setExecHost(host);
      }
    }
  }

  /**
   * Delete all cmdlets related with ruleId.
   */
  public List<Long> deleteCmdletsByRule(long ruleId) throws IOException {
    List<Long> cmdletIds = listCmdletsInfo(ruleId, null)
        .stream()
        .map(CmdletInfo::getCid)
        .collect(Collectors.toList());

    inMemoryRegistry.deleteCmdletsAsync(cmdletIds);
    return cmdletIds;
  }

  public List<Long> deleteUnfinishedCmdletsByRule(long ruleId) {
    List<Long> cmdletIds = inMemoryRegistry.getUnfinishedCmdlets()
        .values()
        .stream()
        .filter(cmdlet -> cmdlet.getRid() == ruleId && !CmdletState.isTerminalState(cmdlet.getState()))
        .map(CmdletInfo::getCid)
        .collect(Collectors.toList());

    inMemoryRegistry.deleteCmdletsAsync(cmdletIds);
    return cmdletIds;
  }

  public CmdletInfo updateCmdletStatus(long cmdletId, CmdletStatus status) {
    return inMemoryRegistry.updateCmdlet(cmdletId, cmdlet -> updateCmdletStatus(cmdlet, status));
  }

  private void updateCmdletStatus(CmdletInfo cmdletInfo, CmdletStatus status) {
    if (CmdletState.isTerminalState(cmdletInfo.getState())) {
      return;
    }
    CmdletState state = status.getCurrentState();
    cmdletInfo.setState(state);
    cmdletInfo.setStateChangedTime(status.getStateUpdateTime());
  }
}
