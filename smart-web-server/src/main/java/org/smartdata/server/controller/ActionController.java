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
import org.smartdata.action.ActionRegistry;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.model.ActionDescriptor;
import org.smartdata.model.ActionInfo;
import org.smartdata.model.DetailedFileAction;
import org.smartdata.server.SmartEngine;
import org.smartdata.server.engine.CmdletManager;
import org.smartdata.server.engine.action.ActionInfoHandler;
import org.smartdata.server.engine.model.ActionGroup;
import org.smartdata.server.engine.model.DetailedFileActionGroup;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

import java.io.IOException;
import java.util.List;

@Tag(name = "Actions")
@RestController
@RequestMapping(
    value = "/api/v1/actions",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class ActionController {
  private final ActionInfoHandler actionInfoHandler;

  public ActionController(CmdletManager cmdletManager) {
    this.actionInfoHandler = cmdletManager.getActionInfoHandler();
  }

  @Operation(summary = "List supported actions")
  @GetMapping("/registry/list")
  public List<ActionDescriptor> getSupportedActions() throws IOException {
    return ActionRegistry.supportedActions();
  }

  @Operation(summary = "List actions")
  @GetMapping("/list/{pageIndex}/{numPerPage}/{orderBy}/{isDesc}")
  public ActionGroup getActions(
      @PathVariable @Min(0) int pageIndex,
      @PathVariable @Min(0) int numPerPage,
      @PathVariable List<String> orderBy,
      @PathVariable List<Boolean> isDesc) throws MetaStoreException, IOException {
    return actionInfoHandler.listActions(pageIndex, numPerPage, orderBy, isDesc);
  }

  @Operation(summary = "Find actions by query")
  @GetMapping("/search/{search}/{pageIndex}/{numPerPage}/{orderBy}/{isDesc}")
  public ActionGroup searchActions(
      @PathVariable String search,
      @PathVariable @Min(0) int pageIndex,
      @PathVariable @Min(0) int numPerPage,
      @PathVariable List<String> orderBy,
      @PathVariable List<Boolean> isDesc) throws IOException {
    return actionInfoHandler.searchAction(search, pageIndex, numPerPage, orderBy, isDesc);
  }

  @Operation(summary = "List actions of rule")
  @GetMapping("/list/{actionLimit}/{ruleId}")
  public List<ActionInfo> getActions(
      @PathVariable @Min(0) int actionLimit,
      @PathVariable long ruleId) throws IOException {
    return actionInfoHandler.getActions(ruleId, actionLimit);
  }

  @Operation(summary = "List file actions of rule")
  @GetMapping("/filelist/{ruleId}/{pageIndex}/{numPerPage}")
  public DetailedFileActionGroup getFileActions(
      @PathVariable long ruleId,
      @PathVariable @Min(0) int pageIndex,
      @PathVariable @Min(0) int numPerPage) throws IOException, MetaStoreException {
    return actionInfoHandler.getFileActions(ruleId, pageIndex, numPerPage);
  }

  @Operation(summary = "List file actions of rule")
  @GetMapping("/filelist/{actionLimit}/{ruleId}")
  public List<DetailedFileAction> getFileActions(
      @PathVariable long ruleId,
      @PathVariable @Min(0) int actionLimit) throws IOException {
    return actionInfoHandler.getFileActions(ruleId, actionLimit);
  }

  @Operation(summary = "Get the last created instances of action")
  @GetMapping("/type/{actionLimit}/{actionName}")
  public List<ActionInfo> getLastActions(
      @PathVariable @Min(0) int actionLimit,
      @PathVariable String actionName) throws IOException {
    return actionInfoHandler.listNewCreatedActions(actionName, actionLimit);
  }

  @Operation(summary = "Get detailed info about action")
  @GetMapping("/{actionId}/info")
  public ActionInfo getActionInfo(@PathVariable long actionId) throws IOException {
    return actionInfoHandler.getActionInfo(actionId);
  }
}
