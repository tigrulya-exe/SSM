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

package org.smartdata.server.engine.rule.copy;

import java.util.List;

public class DiffCreationTimeCopyScheduleStrategy implements FileCopyScheduleStrategy {
  public DiffCreationTimeCopyScheduleStrategy(Order order) {
    this.order = order;
  }

  public enum Order {
    ASC,
    DESC
  }

  private final Order order;

  @Override
  public String wrapGetFilesToCopyQuery(String query, List<String> pathTemplates) {
    return "SELECT file_diff.src "
        + "FROM file_diff "
        + "LEFT JOIN (" + query + ") as q "
        + "ON file_diff.src = q.path "
        + "WHERE q.path IS NOT NULL OR "
        + "(state = 0 AND diff_type IN (1,2) AND ("
        + FileCopyScheduleStrategy.pathTemplatesToSqlCondition(pathTemplates)
        + ")) "
        + "GROUP BY file_diff.src "
        + "ORDER BY " + orderClause() + ";";
  }

  private String orderClause() {
    return order == Order.ASC
        ? "MIN(create_time)"
        : "MAX(create_time) DESC";
  }
}
