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
package org.smartdata.model.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.smartdata.model.TimeInterval;
import org.smartdata.model.audit.UserActivityObject;
import org.smartdata.model.audit.UserActivityOperation;
import org.smartdata.model.audit.UserActivityResult;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@Builder
@RequiredArgsConstructor
public class AuditSearchRequest {
  private final String userLike;
  private final TimeInterval timestampBetween;
  private final List<UserActivityObject> objectTypes;
  private final List<Long> objectIds;
  private final List<UserActivityOperation> operations;
  private final List<UserActivityResult> results;

  public static AuditSearchRequest empty() {
    return builder().build();
  }
}
