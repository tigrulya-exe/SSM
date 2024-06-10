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
package org.smartdata.metastore.dao;

import org.smartdata.metastore.DBPool;
import org.smartdata.metastore.DBType;
import org.smartdata.metastore.dao.impl.DefaultDaoProvider;
import org.smartdata.metastore.dao.postgres.PostgresDaoProvider;
import org.smartdata.metastore.dao.sqlite.SqliteDaoProvider;
import org.springframework.transaction.PlatformTransactionManager;

public class DaoProviderFactory {
  public DaoProvider createDaoProvider(
      DBPool dbPool, PlatformTransactionManager transactionManager, DBType dbType) {
    switch (dbType) {
      case POSTGRES:
        return new PostgresDaoProvider(dbPool, transactionManager);
      case SQLITE:
        return new SqliteDaoProvider(dbPool, transactionManager);
      default:
        return new DefaultDaoProvider(dbPool, transactionManager);
    }
  }
}
