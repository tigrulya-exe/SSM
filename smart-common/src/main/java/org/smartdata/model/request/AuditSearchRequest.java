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
package org.smartdata.model.request;

import org.smartdata.model.TimeInterval;
import org.smartdata.model.UserActivityEvent.ObjectType;
import org.smartdata.model.UserActivityEvent.Operation;
import org.smartdata.model.UserActivityResult;

import java.time.Instant;
import java.util.List;

public class AuditSearchRequest {
  private final String userLike;
  private final TimeInterval timestampBetween;
  private final List<ObjectType> objectTypes;
  private final List<Long> objectIds;
  private final List<Operation> operations;
  private final List<UserActivityResult> results;

  public AuditSearchRequest(
      String userLike,
      TimeInterval timestampBetween,
      List<ObjectType> objectTypes,
      List<Long> objectIds,
      List<Operation> operations,
      List<UserActivityResult> results) {
    this.userLike = userLike;
    this.timestampBetween = timestampBetween;
    this.objectTypes = objectTypes;
    this.objectIds = objectIds;
    this.operations = operations;
    this.results = results;
  }

  public String getUserLike() {
    return userLike;
  }

  public TimeInterval getTimestampBetween() {
    return timestampBetween;
  }

  public List<ObjectType> getObjectTypes() {
    return objectTypes;
  }

  public List<Long> getObjectIds() {
    return objectIds;
  }

  public List<Operation> getOperations() {
    return operations;
  }

  public List<UserActivityResult> getResults() {
    return results;
  }

  public static AuditSearchRequestBuilder builder() {
    return new AuditSearchRequestBuilder();
  }

  public static AuditSearchRequest empty() {
    return new AuditSearchRequestBuilder().build();
  }

  public static class AuditSearchRequestBuilder {
    private String userLike;
    private TimeInterval timestampBetween;
    private List<ObjectType> objectTypes;
    private List<Long> objectIds;
    private List<Operation> operations;
    private List<UserActivityResult> results;

    public AuditSearchRequestBuilder userLike(String userLike) {
      this.userLike = userLike;
      return this;
    }

    public AuditSearchRequestBuilder timestampBetween(Instant from, Instant to) {
      this.timestampBetween = new TimeInterval(from, to);
      return this;
    }

    public AuditSearchRequestBuilder timestampBetween(TimeInterval timestampBetween) {
      this.timestampBetween = timestampBetween;
      return this;
    }

    public AuditSearchRequestBuilder objectTypes(List<ObjectType> objectTypes) {
      this.objectTypes = objectTypes;
      return this;
    }

    public AuditSearchRequestBuilder objectIds(List<Long> objectIds) {
      this.objectIds = objectIds;
      return this;
    }

    public AuditSearchRequestBuilder operations(List<Operation> operations) {
      this.operations = operations;
      return this;
    }

    public AuditSearchRequestBuilder results(List<UserActivityResult> results) {
      this.results = results;
      return this;
    }

    public AuditSearchRequest build() {
      return new AuditSearchRequest(
          userLike,
          timestampBetween,
          objectTypes,
          objectIds,
          operations,
          results);
    }
  }

}
