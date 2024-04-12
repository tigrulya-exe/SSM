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
import org.smartdata.model.CmdletInfo;
import org.smartdata.server.engine.CmdletManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Cmdlets")
@RestController
@RequestMapping(
    value = "/api/v1/cmdlets",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class CmdletController {
  private final CmdletManager cmdletManager;

  public CmdletController(CmdletManager cmdletManager) {
    this.cmdletManager = cmdletManager;
  }

  @Operation(summary = "Get detailed info about cmdlet")
  @GetMapping("/{cmdletId}/info")
  public CmdletInfo getCmdletInfo(@PathVariable long cmdletId) throws IOException {
    return cmdletManager.getCmdletInfo(cmdletId);
  }

  @Operation(summary = "List cmdlets")
  @GetMapping("/list")
  public List<CmdletInfo> getCmdletInfos() throws IOException {
    return cmdletManager.listCmdletsInfo(-1, null);
  }

  @Operation(summary = "Submit cmdlet")
  @PostMapping("/submit")
  public long submitCmdlet(@RequestBody String cmdlet) throws IOException {
    return cmdletManager.submitCmdlet(cmdlet);
  }

  @Operation(summary = "Stop cmdlet")
  @PostMapping("/{cmdletId}/stop")
  public void stopCmdlet(@PathVariable Long cmdletId) throws IOException {
    cmdletManager.disableCmdlet(cmdletId);
  }
}
