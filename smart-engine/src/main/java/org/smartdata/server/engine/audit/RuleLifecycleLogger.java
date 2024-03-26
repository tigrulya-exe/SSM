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
package org.smartdata.server.engine.audit;

import org.smartdata.model.UserActivityEvent;
import org.smartdata.model.UserActivityResult;
import org.smartdata.security.SmartPrincipalHolder;

import java.time.Instant;

import static org.smartdata.model.UserActivityEvent.ObjectType.RULE;
import static org.smartdata.model.UserActivityEvent.Operation.CREATE;
import static org.smartdata.model.UserActivityEvent.Operation.DELETE;
import static org.smartdata.model.UserActivityEvent.Operation.START;
import static org.smartdata.model.UserActivityEvent.Operation.STOP;
import static org.smartdata.model.UserActivityResult.FAILURE;
import static org.smartdata.model.UserActivityResult.SUCCESS;

public class RuleLifecycleLogger implements UserRuleLifecycleListener {

  private final AuditService auditService;

  public RuleLifecycleLogger(AuditService auditService) {
    this.auditService = auditService;
  }

  @Override
  public void onRuleAdded(long ruleId) {
    logEvent(ruleId, CREATE, SUCCESS);
  }

  @Override
  public void onRuleAddFailure(String ruleText) {
    UserActivityEvent event = eventBuilder(null, CREATE, FAILURE)
        .additionalInfo(ruleText)
        .build();
    auditService.logEvent(event);
  }

  @Override
  public void onRuleStart(long ruleId, UserActivityResult result) {
    logEvent(ruleId, START, result);
  }

  @Override
  public void onRuleStop(long ruleId, UserActivityResult result) {
    logEvent(ruleId, STOP, result);
  }

  @Override
  public void onRuleDelete(long ruleId, UserActivityResult result) {
    logEvent(ruleId, DELETE, result);
  }

  private void logEvent(
      Long ruleId,
      UserActivityEvent.Operation operation,
      UserActivityResult result) {
    UserActivityEvent event = eventBuilder(ruleId, operation, result).build();
    auditService.logEvent(event);
  }

  private UserActivityEvent.Builder eventBuilder(
      Long ruleId,
      UserActivityEvent.Operation operation,
      UserActivityResult result) {
    String currentUserName = SmartPrincipalHolder
        .getCurrentPrincipalName()
        .orElse(null);

    return UserActivityEvent.newBuilder()
        .objectType(RULE)
        .timestamp(Instant.now())
        .userName(currentUserName)
        .operation(operation)
        .objectId(ruleId)
        .result(result);
  }
}
