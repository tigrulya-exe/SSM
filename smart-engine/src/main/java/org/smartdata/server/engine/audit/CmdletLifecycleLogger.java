package org.smartdata.server.engine.audit;

import java.time.Instant;
import org.smartdata.server.engine.cmdlet.CmdletLifecycleListener;

public class CmdletLifecycleLogger implements CmdletLifecycleListener {

  private final AuditService auditService;

  public CmdletLifecycleLogger(AuditService auditService) {
    this.auditService = auditService;
  }

  @Override
  public void cmdletAdded(long cmdletId) {
    logEvent(cmdletId, AuditEvent.Operation.START);
  }

  @Override
  public void cmdletStopped(long cmdletId) {
    logEvent(cmdletId, AuditEvent.Operation.STOP);
  }

  @Override
  public void cmdletDeleted(long cmdletId) {
    logEvent(cmdletId, AuditEvent.Operation.DELETE);
  }

  private void logEvent(long cmdletId, AuditEvent.Operation operation) {
    AuditEvent event = AuditEvent.newBuilder()
        .setObject(AuditEvent.Object.CMDLET)
        .setTimestamp(Instant.now())
        // todo
        .setUserName("todo")
        .setOperation(operation)
        .setObjectId(cmdletId)
        .build();
    auditService.logEvent(event);
  }
}