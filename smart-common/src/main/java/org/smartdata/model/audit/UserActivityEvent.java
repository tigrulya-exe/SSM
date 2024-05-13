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
package org.smartdata.model.audit;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@EqualsAndHashCode
@ToString
@Getter
@Builder
public class UserActivityEvent {
  private final Long id;
  private final String username;
  private final Instant timestamp;
  private final Long objectId;
  private final UserActivityObject objectType;
  private final UserActivityOperation operation;
  private final UserActivityResult result;
  private final String additionalInfo;

  public UserActivityEvent(
      Long id,
      String username,
      Instant timestamp,
      Long objectId,
      UserActivityObject objectType,
      UserActivityOperation operation,
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
}
