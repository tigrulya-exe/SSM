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
package org.smartdata.metastore.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

/** Contains common methods for DAOs. */
public class AbstractDao {
  protected final DataSource dataSource;
  protected final JdbcTemplate jdbcTemplate;
  protected final String tableName;

  public AbstractDao(DataSource dataSource, String tableName) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.tableName = tableName;
  }

  protected SimpleJdbcInsert simpleJdbcInsert() {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
    simpleJdbcInsert.setTableName(tableName);
    return simpleJdbcInsert;
  }

  protected <T> void insert(T entity, EntityToMapConverter<T> converter) {
    SimpleJdbcInsert simpleJdbcInsert = simpleJdbcInsert();
    simpleJdbcInsert.execute(converter.toMap(entity));
  }

  @SuppressWarnings("unchecked")
  protected <T> void insert(List<T> entities, EntityToMapConverter<T> converter) {
    insert((T[]) entities.toArray(), converter);
  }

  @SuppressWarnings("unchecked")
  protected <T> void insert(T[] entities, EntityToMapConverter<T> converter) {
    SimpleJdbcInsert simpleJdbcInsert = simpleJdbcInsert();
    Map<String, Object>[] maps = new Map[entities.length];
    for (int i = 0; i < entities.length; i++) {
      maps[i] = converter.toMap(entities[i]);
    }
    simpleJdbcInsert.executeBatch(maps);
  }

  protected interface EntityToMapConverter<T> {
    Map<String, Object> toMap(T entity);
  }
}
