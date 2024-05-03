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
package org.smartdata.server.controller;

import org.smartdata.metastore.model.SearchResult;
import org.smartdata.model.UserActivityEvent;
import org.smartdata.model.request.AuditSearchRequest;
import org.smartdata.server.engine.audit.AuditService;
import org.smartdata.server.generated.api.AuditApiDelegate;
import org.smartdata.server.generated.model.AuditEventResultDto;
import org.smartdata.server.generated.model.AuditEventsDto;
import org.smartdata.server.generated.model.AuditObjectTypeDto;
import org.smartdata.server.generated.model.AuditOperationDto;
import org.smartdata.server.generated.model.EventTimeIntervalDto;
import org.smartdata.server.generated.model.PageRequestDto;
import org.smartdata.server.mappers.AuditEventMapper;
import org.smartdata.server.mappers.PageRequestMapper;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

import java.util.List;

@Component
public class AuditControllerDelegate implements AuditApiDelegate {

  private final AuditService auditService;

  private final AuditEventMapper auditEventMapper;
  private final PageRequestMapper pageRequestMapper;

  public AuditControllerDelegate(
      AuditService auditService,
      AuditEventMapper auditEventMapper,
      PageRequestMapper pageRequestMapper) {
    this.auditService = auditService;
    this.auditEventMapper = auditEventMapper;
    this.pageRequestMapper = pageRequestMapper;
  }

  @Override
  public AuditEventsDto getAuditEvents(
      PageRequestDto pageRequestDto,
      String usernameLike,
      EventTimeIntervalDto eventTime,
      List<@Valid AuditObjectTypeDto> objectTypes,
      List<Long> objectIds,
      List<@Valid AuditOperationDto> operations,
      List<@Valid AuditEventResultDto> results) {

    AuditSearchRequest searchRequest = auditEventMapper.toSearchRequest(
        usernameLike, eventTime, objectTypes, objectIds, operations, results);

    SearchResult<UserActivityEvent> searchResult = auditService.search(
        searchRequest, pageRequestMapper.toPageRequest(pageRequestDto));

    return auditEventMapper.toAuditEventsDto(searchResult);
  }
}
