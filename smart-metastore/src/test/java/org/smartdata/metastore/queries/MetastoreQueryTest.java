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
package org.smartdata.metastore.queries;

import org.apache.curator.shaded.com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.smartdata.metastore.queries.MetastoreQuery.select;
import static org.smartdata.metastore.queries.MetastoreQuery.selectAll;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.and;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.equal;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.greaterThan;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.greaterThanEqual;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.in;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.lessThan;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.lessThanEqual;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.like;
import static org.smartdata.metastore.queries.expression.MetastoreQueryDsl.or;

public class MetastoreQueryTest {

  @Test
  public void testAllOperators() throws IOException {
    PageRequest pageRequest = PageRequest.builder()
        .offset(0L)
        .limit(10L)
        .sortByAsc("id")
        .sortByAsc("ascColumn")
        .sortByDesc("descColumn")
        .build();

    MetastoreQuery query =
        select("id", "anotherColumn")
            .from("testTable")
            .where(
                or(
                    and(
                        greaterThan("gtColumn", 1),
                        lessThan("ltColumn", 2L),
                        greaterThanEqual("gteColumn", 3.0),
                        lessThanEqual("lteColumn", 4.0F)
                    ),
                    equal("anotherColumn", "str_val"),
                    in("listMember", Arrays.asList(1, 2)),
                    like("strColumn", "pattern")
                )
            ).withPagination(pageRequest);

    Map<String, Object> expectedParams = ImmutableMap.<String, Object>builder()
        .put("gtColumn", 1)
        .put("ltColumn", 2L)
        .put("gteColumn", 3.0)
        .put("lteColumn", 4.0F)
        .put("anotherColumn", "str_val")
        .put("listMember", Arrays.asList(1, 2))
        .put("strColumn", "%pattern%")
        .build();

    assertQuery("allOperators", query, expectedParams);
  }

  @Test
  public void testRemoveNullFilters() throws IOException {
    MetastoreQuery query =
        selectAll()
            .from("tableName")
            .where(
                and(
                    equal("nullColumn1", null),
                    like("nonNullColumn1", "test"),
                    or(
                        greaterThan("nonNullColumn2", 777),
                        in("nullColumn2", null)
                    )
                )
            )
            .limit(12L);

    Map<String, Object> expectedParams = ImmutableMap.of(
        "nonNullColumn1", "%test%",
        "nonNullColumn2", 777
    );
    assertQuery("ignoredNullFilters", query, expectedParams);
  }

  @Test
  public void testCollapseWhereIfAllFiltersAreNull() throws IOException {
    MetastoreQuery query =
        selectAll()
            .from("tableName")
            .where(
                or(
                    and(
                        greaterThan("field3", null),
                        in("listField", null)
                    ),
                    equal("column", null),
                    like("test", null)
                )
            );

    assertQuery("collapsedQuery", query, Collections.emptyMap());
  }

  private void assertQuery(
      String queryName, MetastoreQuery query, Map<String, Object> expectedParams)
      throws IOException {
    String expectedSqlQuery = getTestQuery(queryName);

    assertEquals(expectedSqlQuery, query.toSqlQuery());
    assertEquals(expectedParams, query.getParameters());
  }

  private String getTestQuery(String name) throws IOException {
    Path queryPath = Optional.ofNullable(getClass().getClassLoader().getResource("queries"))
        .map(confDir -> Paths.get(confDir.getPath(), name + ".sql"))
        .orElseThrow(() -> new RuntimeException("Resource not found"));

    return new String(Files.readAllBytes(queryPath), StandardCharsets.UTF_8);
  }
}
