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
package org.smartdata.metastore.queries.expression;

import java.util.Collections;
import java.util.Map;

public class MetastoreQueryPlaceholder<T> implements MetastoreQueryExpression {

  private String column;
  private final T value;

  public MetastoreQueryPlaceholder(String column, T value) {
    this.column = column;
    this.value = value;
  }

  @Override
  public String build() {
    return "(:" + column + ")";
  }

  @Override
  public Map<String, Object> getParameters() {
    return Collections.singletonMap(column, value);
  }

  @Override
  public void renameParameter(String oldName, String newName) {
    if (column.equals(oldName)) {
      column = newName;
    }
  }
}
