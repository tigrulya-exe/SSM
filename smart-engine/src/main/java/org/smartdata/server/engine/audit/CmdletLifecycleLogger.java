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

import static org.smartdata.model.UserActivityEvent.ObjectType.CMDLET;
import static org.smartdata.model.UserActivityEvent.Operation.DELETE;
import static org.smartdata.model.UserActivityEvent.Operation.START;
import static org.smartdata.model.UserActivityEvent.Operation.STOP;
import static org.smartdata.model.UserActivityResult.FAILURE;
import static org.smartdata.model.UserActivityResult.SUCCESS;

public class CmdletLifecycleLogger implements UserCmdletLifecycleListener {

  private final AuditService auditService;

  public CmdletLifecycleLogger(AuditService auditService) {
    this.auditService = auditService;
  }

  @Override
  public void onCmdletAdded(long cmdletId) {
    logEvent(cmdletId, START, SUCCESS);
  }

  @Override
  public void onCmdletAddFailure(String cmdletText) {
    UserActivityEvent event = eventBuilder(null, START, FAILURE)
        .additionalInfo(cmdletText)
        .build();
    auditService.logEvent(event);
  }

  @Override
  public void onCmdletStop(long cmdletId, UserActivityResult result) {
    logEvent(cmdletId, STOP, result);
  }

  @Override
  public void onCmdletDelete(long cmdletId, UserActivityResult result) {
    logEvent(cmdletId, DELETE, result);
  }

  private void logEvent(
      Long cmdletId,
      UserActivityEvent.Operation operation,
      UserActivityResult result) {
    UserActivityEvent event = eventBuilder(cmdletId, operation, result).build();
    auditService.logEvent(event);
  }

  private UserActivityEvent.Builder eventBuilder(
      Long cmdletId,
      UserActivityEvent.Operation operation,
      UserActivityResult result) {
    String currentUserName = SmartPrincipalHolder
        .getCurrentPrincipalName()
        .orElse(null);

    return UserActivityEvent.newBuilder()
        .objectType(CMDLET)
        .timestamp(Instant.now())
        .userName(currentUserName)
        .operation(operation)
        .objectId(cmdletId)
        .result(result);
  }
}
