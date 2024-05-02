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
package org.smartdata.model;

import java.time.Instant;
import java.util.Objects;

public class UserActivityEvent {
  public enum ObjectType {
    RULE,
    CMDLET
  }

  public enum Operation {
    CREATE,
    DELETE,
    START,
    STOP
  }

  private final Long id;
  private final String username;
  private final Instant timestamp;
  private final Long objectId;
  private final ObjectType objectType;
  private final Operation operation;
  private final UserActivityResult result;
  private final String additionalInfo;

  public UserActivityEvent(
      Long id,
      String username,
      Instant timestamp,
      Long objectId,
      ObjectType objectType,
      Operation operation,
      UserActivityResult result,
      String additionalInfo) {
    this.id = id;
    this.username = username;
    this.timestamp = timestamp;
    this.objectId = objectId;
    this.objectType = objectType;
    this.operation = operation;
    this.result = result;
    this.additionalInfo = additionalInfo;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Long getObjectId() {
    return objectId;
  }

  public ObjectType getObjectType() {
    return objectType;
  }

  public Operation getOperation() {
    return operation;
  }

  public UserActivityResult getResult() {
    return result;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserActivityEvent event = (UserActivityEvent) o;
    return Objects.equals(id, event.id)
        && Objects.equals(username, event.username)
        && Objects.equals(timestamp, event.timestamp)
        && Objects.equals(objectId, event.objectId)
        && objectType == event.objectType
        && operation == event.operation
        && result == event.result
        && Objects.equals(additionalInfo, event.additionalInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, timestamp, objectId,
        objectType, operation, result, additionalInfo);
  }

  @Override
  public String toString() {
    return "UserActivityEvent{"
        + "id=" + id
        + ", username='" + username + '\''
        + ", timestamp=" + timestamp
        + ", objectId=" + objectId
        + ", objectType=" + objectType
        + ", operation=" + operation
        + ", result=" + result
        + ", additionalInfo='" + additionalInfo + '\''
        + '}';
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private String userName;
    private Instant timestamp;
    private Long objectId;
    private ObjectType objectType;
    private Operation operation;
    private UserActivityResult result;
    private String additionalInfo;

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder userName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder objectId(Long objectId) {
      this.objectId = objectId;
      return this;
    }

    public Builder objectType(ObjectType objectType) {
      this.objectType = objectType;
      return this;
    }

    public Builder operation(Operation operation) {
      this.operation = operation;
      return this;
    }

    public Builder result(UserActivityResult result) {
      this.result = result;
      return this;
    }

    public Builder additionalInfo(String additionalInfo) {
      this.additionalInfo = additionalInfo;
      return this;
    }

    public UserActivityEvent build() {
      return new UserActivityEvent(
          id,
          userName,
          timestamp,
          objectId,
          objectType,
          operation,
          result,
          additionalInfo
      );
    }
  }
}
