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
package org.smartdata.server.engine.cmdlet;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.SmartContext;
import org.smartdata.action.ActionException;
import org.smartdata.action.ActionRegistry;
import org.smartdata.action.SmartAction;
import org.smartdata.hdfs.action.HdfsAction;
import org.smartdata.hdfs.client.CachingLocalFileSystemProvider;
import org.smartdata.hdfs.client.LocalFileSystemProvider;
import org.smartdata.hdfs.impersonation.UserImpersonationStrategy;
import org.smartdata.model.LaunchAction;
import org.smartdata.protocol.message.LaunchCmdlet;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CmdletFactory implements Closeable {
  static final Logger LOG = LoggerFactory.getLogger(CmdletFactory.class);

  private final SmartContext smartContext;
  private final LocalFileSystemProvider localFileSystemProvider;
  private final UserImpersonationStrategy userImpersonationStrategy;

  public CmdletFactory(SmartContext smartContext,
      UserImpersonationStrategy userImpersonationStrategy) {
    this(smartContext,
        new CachingLocalFileSystemProvider(smartContext.getConf(), userImpersonationStrategy),
        userImpersonationStrategy);
  }

  public CmdletFactory(SmartContext smartContext,
      LocalFileSystemProvider localFileSystemProvider,
      UserImpersonationStrategy userImpersonationStrategy) {
    this.smartContext = smartContext;
    this.localFileSystemProvider = localFileSystemProvider;
    this.userImpersonationStrategy = userImpersonationStrategy;
  }

  public Cmdlet createCmdlet(LaunchCmdlet launchCmdlet) throws ActionException {
    List<SmartAction> actions = new ArrayList<>();
    int idx = 0;
    for (LaunchAction launchAction : launchCmdlet.getLaunchActions()) {
      idx++;
      SmartAction action = createAction(
          launchCmdlet.getCmdletId(),
          idx == launchCmdlet.getLaunchActions().size(),
          launchAction,
          userImpersonationStrategy.getUserFor(launchCmdlet));
      actions.add(action);
    }
    return Cmdlet.builder()
        .id(launchCmdlet.getCmdletId())
        .actions(actions)
        .owner(launchCmdlet.getOwner())
        .build();
  }

  public SmartAction createAction(
      long cmdletId,
      boolean isLastAction,
      LaunchAction launchAction,
      String actionUser) throws ActionException {
    SmartAction smartAction = ActionRegistry.createAction(launchAction.getActionType());
    smartAction.setContext(smartContext);
    smartAction.setCmdletId(cmdletId);
    smartAction.setLastAction(isLastAction);
    smartAction.init(launchAction.getArgs());
    smartAction.setActionId(launchAction.getActionId());
    if (smartAction instanceof HdfsAction) {
      setLocalFileSystem((HdfsAction) smartAction, actionUser);
    }
    return smartAction;
  }

  private void setLocalFileSystem(HdfsAction action, String actionUser) throws ActionException {
    try {
      DistributedFileSystem localFileSystem = localFileSystemProvider.provide(
          smartContext.getConf(), actionUser, action.localFsType());
      action.setLocalFileSystem(localFileSystem);
    } catch (IOException exception) {
      LOG.error("smartAction aid={} setDfsClient error", action.getActionId(), exception);
      throw new ActionException(exception);
    }
  }

  @Override
  public void close() {
    try {
      localFileSystemProvider.close();
    } catch (IOException exception) {
      String errorMessage = "Error closing DFS client provider";
      log.error(errorMessage, exception);
      throw new RuntimeException(errorMessage, exception);
    }
  }
}
