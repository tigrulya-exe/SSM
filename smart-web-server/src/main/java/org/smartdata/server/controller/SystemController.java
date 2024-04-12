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
package org.smartdata.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.smartdata.server.SmartEngine;
import org.smartdata.server.engine.StandbyServerInfo;
import org.smartdata.server.engine.cmdlet.agent.AgentInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static org.smartdata.conf.SmartConfKeys.SMART_SERVER_VERSION_CURRENT;
import static org.smartdata.conf.SmartConfKeys.SMART_SERVER_VERSION_KEY;

@Tag(name = "System")
@RestController
@RequestMapping(
    value = "/api/v1/system",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class SystemController {

  private final SmartEngine smartEngine;

  public SystemController(SmartEngine smartEngine) {
    this.smartEngine = smartEngine;
  }

  @Operation(summary = "Get version of SSM")
  @GetMapping("/version")
  public String getSsmVersion() {
    return smartEngine.getConf().get(
        SMART_SERVER_VERSION_KEY, SMART_SERVER_VERSION_CURRENT);
  }

  @Operation(summary = "List all SSM servers")
  @GetMapping("/servers")
  public List<StandbyServerInfo> getServers() {
    return smartEngine.getStandbyServers();
  }

  @Operation(summary = "List all SSM server hosts")
  @GetMapping("/allServerHosts")
  public Set<String> getServerHosts() {
    return smartEngine.getServerHosts();
  }

  @Operation(summary = "List all SSM agents")
  @GetMapping("/agents")
  public List<AgentInfo> getAgents() {
    return smartEngine.getAgents();
  }

  @Operation(summary = "List all SSM agent hosts")
  @GetMapping("/allAgentHosts")
  public Set<String> getAgentHosts() {
    return smartEngine.getAgentHosts();
  }
}
