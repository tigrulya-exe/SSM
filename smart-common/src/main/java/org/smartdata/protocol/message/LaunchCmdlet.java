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
package org.smartdata.protocol.message;

import lombok.Data;
import org.smartdata.AgentService;
import org.smartdata.SmartConstants;
import org.smartdata.model.CmdletDispatchPolicy;
import org.smartdata.model.LaunchAction;

import java.util.List;

/**
 * Command send out by Active SSM server to SSM Agents, Standby servers or itself for execution.
 *
 */
@Data
public class LaunchCmdlet implements AgentService.Message {
  private final long cmdletId;
  private final List<LaunchAction> launchActions;
  private final CmdletDispatchPolicy dispPolicy;
  private final String owner;
  private String nodeId;

  public LaunchCmdlet(long cmdletId, List<LaunchAction> launchActions, String owner) {
    this.cmdletId = cmdletId;
    this.launchActions = launchActions;
    this.owner = owner;
    this.dispPolicy = CmdletDispatchPolicy.ANY;
  }

  @Override
  public String getServiceName() {
    return SmartConstants.AGENT_CMDLET_SERVICE_NAME;
  }

  @Override
  public String toString() {
    return String.format("{cmdletId = %d, dispPolicy = '%s'}", cmdletId, dispPolicy);
  }
}
