/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.cmdlet.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParsedCmdlet {
  private final String cmdletString;
  private final List<ParsedAction> actions;

  public ParsedCmdlet(List<ParsedAction> actions, String cmdletString) {
    this.actions = actions;
    this.cmdletString = cmdletString;
  }

  public List<ParsedAction> getActions() {
    return actions;
  }

  public String getCmdletString() {
    return cmdletString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParsedCmdlet that = (ParsedCmdlet) o;
    return Objects.equals(cmdletString, that.cmdletString)
        && Objects.equals(actions, that.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cmdletString, actions);
  }

  @Override
  public String toString() {
    return "ParsedCmdlet{"
        + "cmdletString='" + cmdletString + "'"
        + ", actions=" + actions
        + '}';
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private final List<ParsedAction> actions;
    private String cmdletString;

    private Builder() {
      this.actions = new ArrayList<>();
    }

    public Builder addAction(String name, Map<String, String> args) {
      this.actions.add(new ParsedAction(name, args));
      return this;
    }

    public Builder setCmdletString(String cmdletString) {
      this.cmdletString = cmdletString;
      return this;
    }

    public ParsedCmdlet build() {
      return new ParsedCmdlet(actions, cmdletString);
    }
  }
}
