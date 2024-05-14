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
package org.smartdata.cmdlet.parser;

import java.util.Map;
import java.util.Objects;

public class ParsedAction {
  private final String name;

  private final Map<String, String> args;

  public ParsedAction(String name, Map<String, String> args) {
    this.name = name;
    this.args = args;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getArgs() {
    return args;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParsedAction that = (ParsedAction) o;
    return Objects.equals(name, that.name)
        && Objects.equals(args, that.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, args);
  }

  @Override
  public String toString() {
    return "ParsedAction{"
        + "name='" + name + "'"
        + ", args=" + args
        + '}';
  }
}
