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
package org.smartdata.metastore.db.metadata;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

public abstract class AbstractDbMetadataProvider
    implements DbMetadataProvider {

  protected final NamedParameterJdbcTemplate jdbcTemplate;
  protected final DataSource dataSource;

  public AbstractDbMetadataProvider(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public boolean tableExists(String tableName) {
    return tablesCount(Collections.singletonList(tableName)) > 0;
  }

  protected String getDbName() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.getCatalog();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
