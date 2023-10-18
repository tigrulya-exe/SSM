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
package org.smartdata.metastore.db;

import org.apache.hadoop.conf.Configuration;
import org.smartdata.metastore.DBPool;
import org.smartdata.metastore.DBType;
import org.smartdata.metastore.MetaStoreException;
import org.smartdata.metastore.db.metadata.DbMetadataProvider;
import org.smartdata.metastore.db.metadata.MySqlDbMetadataProvider;
import org.smartdata.metastore.db.metadata.PostgresDbMetadataProvider;
import org.smartdata.metastore.db.metadata.SqliteDbMetadataProvider;

import javax.sql.DataSource;

import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_LEGACY_MYSQL_SUPPORT_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_LEGACY_MYSQL_SUPPORT_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY;

public class DBHandlersFactory {
  private static final String OLD_MYSQL_LABEL = "old_mysql";

  public DbSchemaManager createDbManager(DBPool dbPool, Configuration conf) {
    String changelogPath = conf.get(
        SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY,
        SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT);

    boolean legacyMysqlSupportEnabled = conf.getBoolean(
        SMART_METASTORE_LEGACY_MYSQL_SUPPORT_KEY,
        SMART_METASTORE_LEGACY_MYSQL_SUPPORT_DEFAULT);

    String labelsFilter = legacyMysqlSupportEnabled
        ? OLD_MYSQL_LABEL
        : "!" + OLD_MYSQL_LABEL;

    return new LiquibaseDbSchemaManager(dbPool, changelogPath, labelsFilter);
  }

  public DbMetadataProvider createDbMetadataProvider(DBPool dbPool, DBType dbType)
      throws MetaStoreException {
    DataSource dataSource = dbPool.getDataSource();
    switch (dbType) {
      case MYSQL:
        return new MySqlDbMetadataProvider(dataSource);
      case POSTGRES:
        return new PostgresDbMetadataProvider(dataSource);
      default:
        return new SqliteDbMetadataProvider(dataSource);
    }
  }
}
