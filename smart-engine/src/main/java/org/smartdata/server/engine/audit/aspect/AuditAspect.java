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
package org.smartdata.server.engine.audit.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.smartdata.model.audit.UserActivityEvent;
import org.smartdata.model.audit.UserActivityResult;
import org.smartdata.server.engine.audit.AuditService;
import org.smartdata.server.engine.audit.Auditable;

import java.time.Instant;

@Aspect
public class AuditAspect {

  @Pointcut("execution(* org.smartdata.server.engine.audit.Auditable+.* "
      + "(@org.smartdata.server.engine.audit.aspect.AuditId (*), ..)) "
      + "&& args(objectId, ..) "
      + "&& @annotation(audit)")
  public void auditableMethodWithIdArg(Audit audit, long objectId) {
  }

  @Pointcut("execution(@org.smartdata.server.engine.audit.aspect.ReturnsAuditId "
      + "long org.smartdata.server.engine.audit.Auditable+.* ((*), ..)) "
      + "&& @annotation(audit)")
  public void auditableMethodReturningId(Audit audit) {
  }

  @AfterReturning(
      value = "auditableMethodWithIdArg(audit, objectId)",
      argNames = "joinPoint,audit,objectId")
  public void wrapAuditableMethodWithIdArg(
      JoinPoint joinPoint, Audit audit, long objectId) {
    logSuccess(joinPoint, audit, objectId);
  }

  @AfterThrowing(
      value = "auditableMethodWithIdArg(audit, objectId)",
      throwing = "exception",
      argNames = "joinPoint,audit,objectId,exception")
  public void onAuditableMethodWithIdArgError(
      JoinPoint joinPoint, Audit audit, long objectId, Exception exception) {
    logFailure(joinPoint, audit, objectId, exception);
  }

  @AfterReturning(
      value = "auditableMethodReturningId(audit)",
      returning = "objectId",
      argNames = "joinPoint,audit,objectId")
  public void wrapAuditableMethodReturningId(
      JoinPoint joinPoint, Audit audit, long objectId) {
    logSuccess(joinPoint, audit, objectId);
  }

  @AfterThrowing(
      value = "auditableMethodReturningId(audit)",
      throwing = "exception",
      argNames = "joinPoint,audit,exception")
  public void onAuditableMethodReturningIdError(
      JoinPoint joinPoint, Audit audit, Exception exception) {
    logFailure(joinPoint, audit, null, exception);
  }

  private void logSuccess(JoinPoint joinPoint, Audit audit, long objectId) {
    UserActivityEvent event = eventBuilder(joinPoint, audit, objectId)
        .result(UserActivityResult.SUCCESS)
        .build();

    logEvent(joinPoint, event);
  }

  private void logFailure(
      JoinPoint joinPoint, Audit audit, Long objectId, Exception exception) {
    UserActivityEvent event = eventBuilder(joinPoint, audit, objectId)
        .result(UserActivityResult.FAILURE)
        .additionalInfo(exception.getMessage())
        .build();

    logEvent(joinPoint, event);
  }

  private void logEvent(JoinPoint joinPoint, UserActivityEvent event) {
    Auditable auditable = (Auditable) joinPoint.getThis();
    AuditService auditService = auditable.getAuditService();

    if (auditService == null) {
      throw new IllegalStateException(
          "getAuditService() method should return non-null value");
    }
    auditService.logEvent(event);
  }

  private UserActivityEvent.Builder eventBuilder(
      JoinPoint joinPoint, Audit audit, Long objectId) {
    Auditable auditable = (Auditable) joinPoint.getThis();

    String currentUserName = auditable.getPrincipalService()
        .getCurrentPrincipal()
        .getName();

    return UserActivityEvent.builder()
        .objectId(objectId)
        .operation(audit.operation())
        .objectType(audit.objectType())
        .username(currentUserName)
        .timestamp(Instant.now());
  }
}
