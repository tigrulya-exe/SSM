package org.smartdata.server.engine.audit;

import java.time.Instant;
import org.smartdata.server.engine.rule.RuleLifecycleListener;

public class RuleLifecycleLogger implements RuleLifecycleListener {

  private final AuditService auditService;

  public RuleLifecycleLogger(AuditService auditService) {
    this.auditService = auditService;
  }

  @Override
  public void ruleAdded(long ruleId) {
    logEvent(ruleId, AuditEvent.Operation.CREATE);
  }

  @Override
  public void ruleStarted(long ruleId) {
    logEvent(ruleId, AuditEvent.Operation.START);
  }

  @Override
  public void ruleStopped(long ruleId) {
    logEvent(ruleId, AuditEvent.Operation.STOP);
  }

  @Override
  public void ruleDeleted(long ruleId) {
    logEvent(ruleId, AuditEvent.Operation.DELETE);
  }

  private void logEvent(long ruleId, AuditEvent.Operation operation) {
    AuditEvent event = AuditEvent.newBuilder()
        .setObject(AuditEvent.Object.RULE)
        .setTimestamp(Instant.now())
        // todo
        .setUserName("todo")
        .setOperation(operation)
        .setObjectId(ruleId)
        .build();
    auditService.logEvent(event);
  }
}