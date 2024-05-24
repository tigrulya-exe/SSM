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
package org.smartdata.server.cluster;

import org.smartdata.conf.SmartConf;
import org.smartdata.metastore.queries.sort.ClusterNodeSortField;
import org.smartdata.metastore.queries.sort.Sorting;
import org.smartdata.model.request.ClusterNodeSearchRequest;
import org.smartdata.server.SearchableInMemoryService;
import org.smartdata.server.engine.ActiveServerInfo;
import org.smartdata.server.engine.CmdletManager;
import org.smartdata.server.engine.StandbyServerInfo;
import org.smartdata.server.engine.cmdlet.HazelcastExecutorService;
import org.smartdata.server.engine.cmdlet.agent.AgentExecutorService;
import org.smartdata.server.engine.cmdlet.agent.AgentInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ClusterNodesManager extends SearchableInMemoryService<
    ClusterNodeSearchRequest, NodeCmdletMetrics, ClusterNodeSortField> {

  private final SmartConf conf;
  private final AgentExecutorService agentService;
  private final HazelcastExecutorService hazelcastService;
  private final ClusterNodeMetricsProvider nodeMetricsProvider;

  public ClusterNodesManager(
      SmartConf conf, CmdletManager cmdletManager) throws IOException {
    this(conf, new AgentExecutorService(conf, cmdletManager),
        new HazelcastExecutorService(cmdletManager), cmdletManager);
    cmdletManager.registerExecutorService(agentService);
    cmdletManager.registerExecutorService(hazelcastService);
  }

  public ClusterNodesManager(
      SmartConf conf,
      AgentExecutorService agentExecutorService,
      HazelcastExecutorService hazelcastService,
      ClusterNodeMetricsProvider nodeMetricsProvider) {
    this.conf = conf;
    this.nodeMetricsProvider = nodeMetricsProvider;
    this.agentService = agentExecutorService;
    this.hazelcastService = hazelcastService;
  }

  // todo remove after zeppelin removal
  public List<AgentInfo> getAgents() {
    return agentService.getAgentInfos();
  }

  // todo remove after zeppelin removal
  public List<StandbyServerInfo> getStandbyServers() {
    return hazelcastService.getStandbyServers();
  }

  // todo remove after zeppelin removal
  public Set<String> getAgentHosts() {
    return conf.getAgentHosts();
  }

  // todo remove after zeppelin removal
  public Set<String> getServerHosts() {
    return conf.getServerHosts();
  }

  // todo remove after zeppelin removal
  public List<NodeInfo> getSsmNodesInfo() {
    List<NodeInfo> nodes = new ArrayList<>();
    nodes.add(ActiveServerInfo.getInstance());
    nodes.addAll(getStandbyServers());
    nodes.addAll(getAgents());
    return nodes;
  }

  @Override
  protected Stream<NodeCmdletMetrics> allEntitiesStream() {
    return nodeMetricsProvider.getNodeMetrics().stream();
  }

  @Override
  protected List<Predicate<NodeCmdletMetrics>> entityFilters(
      ClusterNodeSearchRequest searchRequest) {

    Predicate<Long> timeIntervalPredicate =
        timeIntervalPredicate(searchRequest.getRegistrationTime());

    return Collections.singletonList(
        node -> timeIntervalPredicate.test(node.getRegistTime())
    );
  }

  @Override
  protected Comparator<NodeCmdletMetrics> sortingToComparator(
      Sorting<ClusterNodeSortField> sorting) {
    Comparator<NodeCmdletMetrics> comparator = null;
    switch (sorting.getColumn()) {
      case ID:
        comparator = Comparator.comparing(
            NodeCmdletMetrics::getNodeInfo,
            Comparator.comparing(NodeInfo::getId));
        break;
      case EXECUTORS:
        comparator = Comparator.comparingInt(
            NodeCmdletMetrics::getNumExecutors);
        break;
      case REGISTRATION_TIME:
        comparator = Comparator.comparingLong(
            NodeCmdletMetrics::getRegistTime);
    }

    return sorting.getOrder() == Sorting.Order.DESC
        ? comparator.reversed()
        : comparator;
  }

}
