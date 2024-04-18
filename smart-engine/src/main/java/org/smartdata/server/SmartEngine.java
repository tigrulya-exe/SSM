/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.smartdata.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.AbstractService;
import org.smartdata.conf.SmartConf;
import org.smartdata.model.StorageCapacity;
import org.smartdata.model.Utilization;
import org.smartdata.server.cluster.NodeInfo;
import org.smartdata.server.engine.ActiveServerInfo;
import org.smartdata.server.engine.CmdletManager;
import org.smartdata.server.engine.RuleManager;
import org.smartdata.server.engine.ServerContext;
import org.smartdata.server.engine.StandbyServerInfo;
import org.smartdata.server.engine.StatesManager;
import org.smartdata.server.engine.audit.AuditService;
import org.smartdata.server.engine.audit.CmdletLifecycleLogger;
import org.smartdata.server.engine.audit.RuleLifecycleLogger;
import org.smartdata.server.engine.cmdlet.HazelcastExecutorService;
import org.smartdata.server.engine.cmdlet.agent.AgentExecutorService;
import org.smartdata.server.engine.cmdlet.agent.AgentInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SmartEngine extends AbstractService {
  public static final Logger LOG = LoggerFactory.getLogger(SmartEngine.class);

  private final SmartConf conf;
  private final ServerContext serverContext;
  private StatesManager statesMgr;
  private RuleManager ruleMgr;
  private CmdletManager cmdletManager;
  private AgentExecutorService agentService;
  private HazelcastExecutorService hazelcastService;
  private AuditService auditService;
  private final List<AbstractService> services;

  public SmartEngine(ServerContext context) {
    super(context);
    this.serverContext = context;
    this.conf = serverContext.getConf();
    this.services = new ArrayList<>();
  }

  @Override
  public void init() throws IOException {
    statesMgr = new StatesManager(serverContext);
    services.add(statesMgr);
    auditService = new AuditService(serverContext.getMetaStore().userActivityDao());
    cmdletManager = new CmdletManager(
        serverContext, new CmdletLifecycleLogger(auditService));
    services.add(cmdletManager);
    agentService = new AgentExecutorService(conf, cmdletManager);
    hazelcastService = new HazelcastExecutorService(cmdletManager);
    cmdletManager.registerExecutorService(agentService);
    cmdletManager.registerExecutorService(hazelcastService);
    ruleMgr = new RuleManager(serverContext, statesMgr,
        cmdletManager, new RuleLifecycleLogger(auditService));
    services.add(ruleMgr);

    for (AbstractService s : services) {
      s.init();
    }
  }

  @Override
  public boolean inSafeMode() {
    if (services.isEmpty()) { //Not initiated
      return true;
    }
    for (AbstractService service : services) {
      if (service.inSafeMode()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void start() throws IOException {
    for (AbstractService s : services) {
      s.start();
    }
  }

  @Override
  public void stop() throws IOException {
    for (int i = services.size() - 1; i >= 0; i--) {
      stopEngineService(services.get(i));
    }
  }

  private void stopEngineService(AbstractService service) {
    try {
      if (service != null) {
        service.stop();
      }
    } catch (IOException e) {
      LOG.error("Error while stopping "
          + service.getClass().getCanonicalName(), e);
    }
  }

  public List<StandbyServerInfo> getStandbyServers() {
    return hazelcastService.getStandbyServers();
  }

  public Set<String> getAgentHosts() {
    return conf.getAgentHosts();
  }

  public Set<String> getServerHosts() {
    return conf.getServerHosts();
  }

  public List<AgentInfo> getAgents() {
    return agentService.getAgentInfos();
  }

  public SmartConf getConf() {
    return serverContext.getConf();
  }

  public StatesManager getStatesManager() {
    return statesMgr;
  }

  public RuleManager getRuleManager() {
    return ruleMgr;
  }

  public AuditService getAuditService() {
    return auditService;
  }

  public CmdletManager getCmdletManager() {
    return cmdletManager;
  }

  public Utilization getUtilization(String resourceName) throws IOException {
    return getStatesManager().getStorageUtilization(resourceName);
  }

  public List<Utilization> getHistUtilization(String resourceName, long granularity,
      long begin, long end) throws IOException {
    long now = System.currentTimeMillis();
    if (begin == end && Math.abs(begin - now) <= 5) {
      return Collections.singletonList(getUtilization(resourceName));
    }

    List<StorageCapacity> cs = serverContext.getMetaStore().getStorageHistoryData(
        resourceName, granularity, begin, end);
    List<Utilization> us = new ArrayList<>(cs.size());
    for (StorageCapacity c : cs) {
      us.add(new Utilization(c.getTimeStamp(), c.getCapacity(), c.getUsed()));
    }
    return us;
  }

  public List<NodeInfo> getSsmNodesInfo() {
    List<NodeInfo> ret = new LinkedList<>();
    ret.add(ActiveServerInfo.getInstance());
    ret.addAll(getStandbyServers());
    ret.addAll(getAgents());
    return ret;
  }
}
