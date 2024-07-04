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
package org.smartdata.metastore.dao.accesscount;

import org.junit.Before;
import org.junit.Test;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.db.metadata.DbMetadataProvider;
import org.smartdata.metastore.model.AccessCountTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDbAccessCountTableManager extends TestDaoBase {

  private DbAccessCountTableManager dbAccessCountTableManager;
  private DbMetadataProvider dbMetadataProvider;
  private AccessCountTableDao accessCountTableDao;

  @Before
  public void setUp() {
    dbAccessCountTableManager = new DbAccessCountTableManager(metaStore);
    accessCountTableDao = metaStore.accessCountTableDao();
    dbMetadataProvider = metaStore.dbMetadataProvider();
  }

  private List<AccessCountTable> createTables(String... tables) throws Exception {
    List<AccessCountTable> createdTables = new ArrayList<>();

    for (String tableName : tables) {
      AccessCountTable table = dummyTable(tableName);
      dbAccessCountTableManager.createTable(table);
      createdTables.add(table);
    }

    return createdTables;
  }

  private AccessCountTable dummyTable(String tableName) {
    return new AccessCountTable(tableName, 0L, 0L, false);
  }

  @Test
  public void testCreateTable() throws Exception {
    createTables("table1", "table2", "table3")
        .stream()
        .map(AccessCountTable::getTableName)
        .forEach(this::assertTableExists);
  }

  @Test
  public void testAggregateTables() throws Exception {
    List<AccessCountTable> tablesToAggregate =
        createTables("table1", "table2", "table3");

    AccessCountTable destTable = dummyTable("dest");
    dbAccessCountTableManager.aggregate(destTable, tablesToAggregate);

    assertTableExists(destTable.getTableName());
  }

  @Test
  public void testDropTable() throws Exception {
    List<AccessCountTable> tablesToAggregate =
        createTables("table1", "table2");

    dbAccessCountTableManager.dropTable(tablesToAggregate.get(0));
    assertFalse(accessCountTableDao.tableExists("table1"));
    assertFalse(dbMetadataProvider.tableExists("table1"));
    assertTableExists("table2");

    dbAccessCountTableManager.dropTable(dummyTable("another"));
    assertTableExists("table2");
  }

  @Test
  public void testRecoverOnlyValidTables() throws Exception {
    // table from previous SSM version without last access time column
    AccessCountTable oldTable = new AccessCountTable(0, 5000);
    metaStore.execute(
        "CREATE TABLE " + oldTable.getTableName()
            + "(fid BIGINT NOT NULL, "
            + "count INTEGER NOT NULL)");

    accessCountTableDao.insert(oldTable);

    // non-existing table
    AccessCountTable deletedTable = new AccessCountTable(15000, 20000);
    accessCountTableDao.insert(deletedTable);

    List<AccessCountTable> validTables = Arrays.asList(
        new AccessCountTable(5000, 10000),
        new AccessCountTable(10000, 15000),
        new AccessCountTable(20000, 25000));
    for (AccessCountTable table: validTables) {
      dbAccessCountTableManager.createTable(table);
    }

    List<AccessCountTable> validatedTables = dbAccessCountTableManager.getTables();
    assertEquals(validTables, validatedTables);
  }

  private void assertTableExists(String tableName) {
    assertTrue(accessCountTableDao.tableExists(tableName));
    assertTrue(dbMetadataProvider.tableExists(tableName));
  }
}
