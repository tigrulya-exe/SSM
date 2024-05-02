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
package org.smartdata.metastore.dao.impl;

import org.smartdata.metastore.SearchableAbstractDao;
import org.smartdata.metastore.dao.UserActivityDao;
import org.smartdata.metastore.queries.MetastoreQuery;
import org.smartdata.model.TimeInterval;
import org.smartdata.model.UserActivityEvent;
import org.smartdata.model.UserActivityEvent.ObjectType;
import org.smartdata.model.UserActivityEvent.Operation;
import org.smartdata.model.UserActivityResult;
import org.smartdata.model.request.AuditSearchRequest;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.smartdata.metastore.queries.MetastoreQuery.selectAll;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.greaterThanEqual;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.in;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.inStrings;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.lessThanEqual;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.like;

public class DefaultUserActivityDao
    extends SearchableAbstractDao<AuditSearchRequest, UserActivityEvent>
    implements UserActivityDao {
  private static final String TABLE_NAME = "user_activity_event";

  public DefaultUserActivityDao(
      DataSource dataSource,
      PlatformTransactionManager transactionManager) {
    super(dataSource, transactionManager, TABLE_NAME);
  }

  @Override
  public void insert(UserActivityEvent event) {
    insert(event, this::toMap);
  }

  @Override
  protected SimpleJdbcInsert simpleJdbcInsert() {
    return super.simpleJdbcInsert()
        .usingGeneratedKeyColumns("id");
  }

  @Override
  protected MetastoreQuery searchQuery(AuditSearchRequest searchRequest) {
    Long timestampFrom = Optional.ofNullable(searchRequest.getTimestampBetween())
        .map(TimeInterval::getFrom)
        .map(Instant::toEpochMilli)
        .orElse(null);

    Long timestampTo = Optional.ofNullable(searchRequest.getTimestampBetween())
        .map(TimeInterval::getTo)
        .map(Instant::toEpochMilli)
        .orElse(null);

    return selectAll()
        .from(TABLE_NAME)
        .where(
            like("username", searchRequest.getUserLike()),
            greaterThanEqual("timestamp", timestampFrom),
            lessThanEqual("timestamp", timestampTo),
            inStrings("object_type", searchRequest.getObjectTypes()),
            in("object_id", searchRequest.getObjectIds()),
            inStrings("operation", searchRequest.getOperations()),
            inStrings("result", searchRequest.getResults())
        );
  }

  @Override
  protected UserActivityEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return UserActivityEvent.newBuilder()
        .id(resultSet.getLong(1))
        .userName(resultSet.getString(2))
        .timestamp(Instant.ofEpochMilli(resultSet.getLong(3)))
        .objectType(ObjectType.valueOf(resultSet.getString(4)))
        .objectId(resultSet.getLong(5))
        .operation(Operation.valueOf(resultSet.getString(6)))
        .result(UserActivityResult.valueOf(resultSet.getString(7)))
        .additionalInfo(resultSet.getString(8))
        .build();
  }

  private Map<String, Object> toMap(UserActivityEvent event) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("username", event.getUserName());
    properties.put("timestamp", event.getTimestamp().toEpochMilli());
    properties.put("object_type", event.getObjectType());
    properties.put("object_id", event.getObjectId());
    properties.put("operation", event.getOperation());
    properties.put("result", event.getResult());
    properties.put("additional_info", event.getAdditionalInfo());
    return properties;
  }
}
