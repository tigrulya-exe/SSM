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

import org.smartdata.metastore.dao.AbstractDao;
import org.smartdata.metastore.dao.UserActivityDao;
import org.smartdata.model.UserActivityEvent;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

public class DefaultUserActivityDao extends AbstractDao implements UserActivityDao {
  public DefaultUserActivityDao(DataSource dataSource) {
    super(dataSource, "user_activity_event");
  }

  @Override
  public void save(UserActivityEvent event) {
    insert(event, this::toMap);
  }

  private Map<String, Object> toMap(UserActivityEvent event) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("user", event.getUserName());
    properties.put("timestamp", event.getTimestamp());
    properties.put("object_type", event.getObjectType());
    properties.put("object_id", event.getObjectId());
    properties.put("operation", event.getOperation());
    properties.put("result", event.getResult());
    properties.put("additional_info", event.getAdditionalInfo());
    return properties;
  }
}
