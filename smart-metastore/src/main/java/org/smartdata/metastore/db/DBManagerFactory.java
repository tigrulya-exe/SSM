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

import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_LABELS_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_METASTORE_MIGRATION_LABELS_KEY;

public class DBManagerFactory {
  public DBManager createDbManager(DBPool dbPool, Configuration conf) {
    String changelogPath = conf.get(
        SMART_METASTORE_MIGRATION_CHANGELOG_PATH_KEY,
        SMART_METASTORE_MIGRATION_CHANGELOG_PATH_DEFAULT);

    String labels = conf.get(
        SMART_METASTORE_MIGRATION_LABELS_KEY,
        SMART_METASTORE_MIGRATION_LABELS_DEFAULT);

    return new LiquibaseDBManager(dbPool, changelogPath, labels);
  }
}
