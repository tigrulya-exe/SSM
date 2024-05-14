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

import org.junit.Test;
import org.smartdata.metastore.TestDaoBase;
import org.smartdata.metastore.utils.MetaStoreUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDbMetadataProvider extends TestDaoBase {

  @Test
  public void testTableExists() throws Exception {
    boolean fileTableExists = dbMetadataProvider.tableExists("file");
    assertTrue(fileTableExists);

    dbSchemaManager.clearDatabase();

    fileTableExists = dbMetadataProvider.tableExists("file");
    assertFalse(fileTableExists);
  }

  @Test
  public void testTableCount() throws Exception {
    List<String> tables = MetaStoreUtils.SSM_TABLES;

    int tablesCount = dbMetadataProvider.tablesCount(tables);
    assertEquals(tables.size(), tablesCount);

    dbSchemaManager.clearDatabase();

    tablesCount = dbMetadataProvider.tablesCount(tables);
    assertEquals(0, tablesCount);
  }
}
