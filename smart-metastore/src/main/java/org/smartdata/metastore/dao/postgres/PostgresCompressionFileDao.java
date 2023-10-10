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

import org.smartdata.metastore.dao.impl.DefaultCompressionFileDao;
import org.smartdata.model.CompressionFileState;

import javax.sql.DataSource;

public class PostgresCompressionFileDao extends DefaultCompressionFileDao {
  private static final String PRIMARY_KEY_FIELD = "path";

  private final PostgresUpsertSupport upsertSupport;

  public PostgresCompressionFileDao(DataSource dataSource) {
    super(dataSource);
    this.upsertSupport = new PostgresUpsertSupport(dataSource, tableName);
  }

  @Override
  public void insertUpdate(CompressionFileState compressionInfo) {
    upsertSupport.upsert(toMap(compressionInfo), PRIMARY_KEY_FIELD);
  }
}
