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

package org.smartdata.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.utils.Constants;
import org.smartdata.model.CachedFileStatus;
import org.smartdata.model.FileAccessInfo;
import org.smartdata.model.FileInfo;
import org.smartdata.model.Utilization;
import org.smartdata.server.SmartEngine;
import org.smartdata.server.cluster.NodeCmdletMetrics;
import org.smartdata.server.cluster.NodeInfo;
import org.smartdata.server.engine.StatesManager;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Tag(name = "Cluster")
@RestController
@RequestMapping(
    value = "/api/v1/cluster/primary",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class ClusterController {
  private final SmartEngine smartEngine;
  private final StatesManager statesManager;

  public ClusterController(SmartEngine smartEngine, StatesManager statesManager) {
    this.smartEngine = smartEngine;
    this.statesManager = statesManager;
  }

  @Operation(summary = "Get address of the HDFS NameNode")
  @GetMapping
  public String getNameNodeUrl() {
    return smartEngine.getConf().
        get(SmartConfKeys.SMART_DFS_NAMENODE_RPCSERVER_KEY);
  }

  @Operation(summary = "Get list of cached files")
  @GetMapping("/cachedfiles")
  public List<CachedFileStatus> getCachedFiles() throws IOException {
    return statesManager.getCachedFileStatus();
  }

  @Operation(summary = "Get list of hot files for the last hour")
  @GetMapping("/hotfiles")
  public List<FileAccessInfo> getHotFiles() throws IOException, MetaStoreException {
    //TODO: Make fileLimit settable on web UI.
    //Currently, fileLimit=0 means that SSM will use the value configured in smart-default.xml
    return statesManager.getHotFilesForLast(Constants.ONE_HOUR_IN_MILLIS, 0);
  }

  @Operation(summary = "Get utilization of specified resource")
  @GetMapping("/utilization/{resourceName}")
  public Utilization getResourceUtilization(@PathVariable String resourceName) throws IOException {
    return smartEngine.getUtilization(resourceName);
  }

  /**
   * @param timeGranularity Time interval of successive data points in milliseconds
   * @param intervalStart   Begin timestamp in milliseconds.
   *                        If <=0 denotes the value related to 'endTs'
   * @param intervalEnd     Like 'beginTs'. If <= 0 denotes the time related to current server time.
   */
  @Operation(summary = "Get utilization of resource for specified time interval")
  @GetMapping("/hist_utilization/{resourceName}/{timeGranularity}/{intervalStart}/{intervalEnd}")
  public List<Utilization> getResourceUtilizationForInterval(
      @PathVariable String resourceName,
      @PathVariable @Min(0) long timeGranularity,
      @PathVariable long intervalStart,
      @PathVariable long intervalEnd) throws IOException {
    if (intervalEnd <= 0) {
      intervalEnd += System.currentTimeMillis();
    }

    if (intervalStart <= 0) {
      intervalStart += intervalEnd;
    }

    if (intervalStart > intervalEnd) {
      throw new IOException("Invalid time range");
    }

    return smartEngine.getHistUtilization(
        resourceName, timeGranularity, intervalStart, intervalEnd);
  }

  @Operation(summary = "Get information about file")
  @GetMapping("/fileinfo")
  public FileInfo getFileInfo(@RequestBody String filePath) throws IOException {
    return statesManager.getFileInfo(filePath);
  }

  @Operation(summary = "Get information about SSM nodes")
  @GetMapping("/ssmnodesinfo")
  public List<NodeInfo> getSsmNodesInfo() {
    return smartEngine.getSsmNodesInfo();
  }

  @Operation(summary = "Get cmdlet metrics from SSM nodes")
  @GetMapping("/ssmnodescmdletmetrics")
  public Collection<NodeCmdletMetrics> getSsmNodesCmdletMetrics() {
    return smartEngine.getCmdletManager().getAllNodeCmdletMetrics();
  }
}
