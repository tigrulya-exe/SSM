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
package org.smartdata.server.engine.model;

import org.smartdata.model.ActionInfo;

import java.util.List;

public class ActionGroup {
    private final List<ActionInfo> actions;
    private final long totalNumOfActions;

    public ActionGroup() {
      this(null, 0);
    }

    public ActionGroup(List<ActionInfo> actions, long totalNumOfActions) {
      this.actions = actions;
      this.totalNumOfActions = totalNumOfActions;
    }

    public List<ActionInfo> getActions() {
      return actions;
    }

    public long getTotalNumOfActions() {
      return totalNumOfActions;
    }
  }
