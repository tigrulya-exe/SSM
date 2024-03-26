package org.smartdata.server.engine.audit;

import java.time.Instant;

public class AuditEvent {
  public enum Object {
    RULE,
    CMDLET
  }

  public enum Operation {
    CREATE,
    DELETE,
    START,
    STOP
  }
  private final String userName;
  private final Instant timestamp;
  private final long objectId;
  private final AuditEvent.Object object;
  private final AuditEvent.Operation operation;

  public AuditEvent(String userName, Instant timestamp, long objectId, Object object, Operation operation) {
    this.userName = userName;
    this.timestamp = timestamp;
    this.objectId = objectId;
    this.object = object;
    this.operation = operation;
  }

  public String getUserName() {
    return userName;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public long getObjectId() {
    return objectId;
  }

  public Object getObject() {
    return object;
  }

  public Operation getOperation() {
    return operation;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private String userName;
    private Instant timestamp;
    private long objectId;
    private AuditEvent.Object object;
    private AuditEvent.Operation operation;

    public Builder setUserName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder setTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder setObjectId(long objectId) {
      this.objectId = objectId;
      return this;
    }

    public Builder setObject(AuditEvent.Object object) {
      this.object = object;
      return this;
    }

    public Builder setOperation(AuditEvent.Operation operation) {
      this.operation = operation;
      return this;
    }

    public AuditEvent build() {
      return new AuditEvent(userName, timestamp, objectId, object, operation);
    }
  }
}
