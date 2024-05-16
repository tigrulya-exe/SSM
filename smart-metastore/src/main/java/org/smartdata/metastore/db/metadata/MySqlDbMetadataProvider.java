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

import com.google.common.collect.Sets;
import org.smartdata.metastore.MetaStoreException;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySqlDbMetadataProvider
    extends AbstractDbMetadataProvider {

  private static final Set<String> DB_NAME_NOT_ALLOWED = Sets.newHashSet(
      "mysql",
      "sys",
      "information_schema",
      "INFORMATION_SCHEMA",
      "performance_schema",
      "PERFORMANCE_SCHEMA"
  );

  private final String dbName;

  public MySqlDbMetadataProvider(DataSource dataSource) throws MetaStoreException {
    super(dataSource);

    String dbName = getDbName();
    if (DB_NAME_NOT_ALLOWED.contains(dbName)) {
      throw new MetaStoreException(
          String.format(
              "The database %s in mysql is for DB system use, "
                  + "please appoint other database in druid.xml.",
              dbName));
    }
    this.dbName = dbName;
  }

  @Override
  public int tablesCount(List<String> tableNames) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put("dbName", dbName);
    queryParams.put("tableNames", tableNames);

    return jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
            + "WHERE TABLE_SCHEMA = :dbName AND TABLE_NAME IN (:tableNames)",
        queryParams,
        Integer.class
    );
  }
}
