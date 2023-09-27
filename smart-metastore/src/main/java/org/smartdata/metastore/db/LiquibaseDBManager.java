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
package org.smartdata.metastore.db;

import liquibase.command.CommandScope;
import liquibase.command.core.DropAllCommandStep;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import org.smartdata.metastore.DBPool;

import java.sql.Connection;

public class LiquibaseDBManager implements DBManager {
    private final DBPool pool;
    private final String changelogPath;
    private final String labelFilterExpr;

    public LiquibaseDBManager(DBPool pool, String changelogPath, String labelFilterExpr) {
        this.pool = pool;
        this.changelogPath = changelogPath;
        this.labelFilterExpr = labelFilterExpr;
    }

    @Override
    public void initializeDatabase() throws Exception {
        try (Connection connection = pool.getConnection();
             JdbcConnection jdbcConnection = new JdbcConnection(connection)) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(jdbcConnection);

            new CommandScope(UpdateCommandStep.COMMAND_NAME)
                    .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, db)
                    .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changelogPath)
                    .addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, labelFilterExpr)
                    .execute();
        }
    }

    @Override
    public void clearDatabase() throws Exception {
        try (Connection connection = pool.getConnection();
             JdbcConnection jdbcConnection = new JdbcConnection(connection)) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(jdbcConnection);

            new CommandScope(DropAllCommandStep.COMMAND_NAME)
                    .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, db)
                    .execute();
        }
    }
}
