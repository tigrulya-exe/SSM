/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.metastore.dao.postgres;

import org.smartdata.metastore.dao.impl.DefaultStorageDao;
import org.smartdata.model.StorageCapacity;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

public class PostgresStorageDao extends DefaultStorageDao {
  private static final String PRIMARY_KEY_FIELD = "type";

  private final PostgresUpsertSupport upsertSupport;

  public PostgresStorageDao(DataSource dataSource) {
    super(dataSource);
    this.upsertSupport = new PostgresUpsertSupport(dataSource, tableName);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void insertUpdateStoragesTable(StorageCapacity[] storages) {
    if (storages.length == 0) {
      return;
    }
    upsertSupport.batchUpsert(storages,
        this::toNamedParameters, PRIMARY_KEY_FIELD);
  }

  private Map<String, Object> toNamedParameters(StorageCapacity storageCapacity) {
    Map<String, Object> namedParameters = new HashMap<>();
    namedParameters.put("type", storageCapacity.getType());

    Long timestamp = storageCapacity.getTimeStamp() == null
        ? System.currentTimeMillis()
        : storageCapacity.getTimeStamp();
    namedParameters.put("time_stamp", timestamp);

    namedParameters.put("capacity", storageCapacity.getCapacity());
    namedParameters.put("free", storageCapacity.getFree());
    return namedParameters;
  }
}
