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

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteDbMetadataProvider
    extends AbstractDbMetadataProvider {
  public SqliteDbMetadataProvider(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public int tablesCount(List<String> tableNames) {
    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put("tableNames", tableNames);

    return jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND NAME IN (:tableNames)",
        queryParams,
        Integer.class
    );
  }
}
