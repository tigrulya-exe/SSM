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
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.CmdletInfo;
import org.smartdata.model.DetailedRuleInfo;
import org.smartdata.model.RuleInfo;
import org.smartdata.model.RuleState;
import org.smartdata.server.engine.CmdletManager;
import org.smartdata.server.engine.RuleManager;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

import java.io.IOException;
import java.util.List;

@Tag(name = "Rules")
@RestController
@RequestMapping(
    value = "/api/v1/rules",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class RuleController {
  private final RuleManager ruleManager;
  private final CmdletManager cmdletManager;

  public RuleController(RuleManager ruleManager, CmdletManager cmdletManager) {
    this.ruleManager = ruleManager;
    this.cmdletManager = cmdletManager;
  }

  @Operation(summary = "Submit rule")
  @PostMapping("/add")
  public long addRule(@RequestParam String ruleText) throws IOException {
    return ruleManager.submitRule(ruleText, RuleState.NEW);
  }

  @Operation(summary = "Delete rule")
  @DeleteMapping("/{ruleId}/delete")
  public void deleteRule(@PathVariable long ruleId) throws IOException {
    ruleManager.deleteRule(ruleId, false);
  }

  @Operation(summary = "Start/continue rule")
  @PostMapping("/{ruleId}/start")
  public void start(@PathVariable long ruleId) throws IOException {
    ruleManager.activateRule(ruleId);
  }

  @Operation(summary = "Stop rule")
  @PostMapping("/{ruleId}/stop")
  public void stop(@PathVariable long ruleId) throws IOException {
    ruleManager.disableRule(ruleId, true);
  }

  @Operation(summary = "Get detailed info about rule")
  @GetMapping("/{ruleId}/info")
  public RuleInfo getInfo(@PathVariable long ruleId) throws IOException {
    return ruleManager.getRuleInfo(ruleId);
  }

  @Operation(summary = "List all cmdlets of specified rule")
  @GetMapping("/{ruleId}/cmdlets")
  public List<CmdletInfo> getCmdlets(@PathVariable long ruleId) throws IOException {
    return cmdletManager.listCmdletsInfo(ruleId, null);
  }

  @Operation(summary = "List all rules")
  @GetMapping("/list")
  public List<RuleInfo> getRuleInfos() {
    return ruleManager.listRulesInfo();
  }

  @Operation(summary = "List all move rules")
  @GetMapping("/list/move")
  public List<DetailedRuleInfo> getMoveRuleInfos() throws IOException {
    return ruleManager.listRulesMoveInfo();
  }

  @Operation(summary = "List all sync rules")
  @GetMapping("/list/sync")
  public List<DetailedRuleInfo> getSyncRuleInfos() throws IOException {
    return ruleManager.listRulesSyncInfo();
  }

  @Operation(summary = "List all cmdlets of specified rule")
  @GetMapping("/{ruleId}/cmdlets/{pageIndex}/{numPerPage}/{orderBy}/{isDesc}")
  public CmdletManager.CmdletGroup getCmdlets(
      @PathVariable long ruleId,
      @PathVariable @Min(0) int pageIndex,
      @PathVariable @Min(0) int numPerPage,
      @PathVariable List<String> orderBy,
      @PathVariable List<Boolean> isDesc) throws MetaStoreException, IOException {
    return cmdletManager.listCmdletsInfo(
        ruleId, pageIndex, numPerPage, orderBy, isDesc);
  }
}
