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
package org.smartdata.server.engine.cmdlet;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.action.SmartAction;
import org.smartdata.protocol.message.ActionStatus;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Action is the minimum unit of execution. A cmdlet can contain more than one
 * actions. Different cmdlets can be executed at the same time, but actions
 * belonging to a cmdlet can only be executed in sequence.
 *
 * <p>The cmdlet get executed when rule conditions fulfills.
 */
@Getter
public class Cmdlet implements Runnable {
  static final Logger LOG = LoggerFactory.getLogger(Cmdlet.class);

  private final long id;
  private final List<SmartAction> actions;
  private final Set<Long> idsToReport;
  private final Long ruleId;
  private final String owner;

  public String toString() {
    return "Rule-" + ruleId + "-Cmd-" + id;
  }

  @lombok.Builder
  public Cmdlet(long id, List<SmartAction> actions, Long ruleId, String owner) {
    this.id = id;
    this.ruleId = ruleId;
    this.owner = owner;
    this.actions = actions;
    this.idsToReport = actions.stream()
        .map(SmartAction::getActionId)
        .collect(Collectors.toSet());
  }

  @Override
  public void run() {
    Iterator<SmartAction> iter = actions.iterator();
    while (iter.hasNext()) {
      SmartAction act = iter.next();
      if (act == null) {
        continue;
      }
      // Init Action
      // TODO: this statement maybe can be removed.
      act.init(act.getArguments());
      act.run();
      if (!act.isSuccessful()) {
        while (iter.hasNext()) {
          SmartAction nextAct = iter.next();
          synchronized (this) {
            idsToReport.remove(nextAct.getActionId());
          }
        }
        LOG.error("Executing Cmdlet [id={}] meets failed.", getId());
        return;
      }
    }
  }

  public synchronized List<ActionStatus> getActionStatuses() {
    if (idsToReport.isEmpty()) {
      return Collections.emptyList();
    }

    return actions.stream()
        .filter(action -> idsToReport.contains(action.getActionId()))
        .map(SmartAction::getActionStatus)
        .peek(status -> {
              if (status.isFinished()) {
                idsToReport.remove(status.getActionId());
              }
            }
        ).collect(Collectors.toList());
  }
}
