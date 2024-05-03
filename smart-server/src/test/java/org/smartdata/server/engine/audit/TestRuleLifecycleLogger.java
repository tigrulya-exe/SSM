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

package org.smartdata.server.engine.audit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartdata.conf.SmartConf;
import org.smartdata.metastore.SqliteTestDaoBase;
import org.smartdata.model.RuleInfo;
import org.smartdata.model.RuleState;
import org.smartdata.model.UserActivityEvent;
import org.smartdata.model.UserActivityResult;
import org.smartdata.model.request.AuditSearchRequest;
import org.smartdata.server.engine.RuleManager;
import org.smartdata.server.engine.ServerContext;
import org.smartdata.server.engine.ServiceMode;

import java.util.Collections;
import java.util.List;

public class TestRuleLifecycleLogger extends SqliteTestDaoBase {
  private RuleManager ruleManager;
  private AuditService auditService;

  @Before
  public void init() throws Exception {
    SmartConf smartConf = new SmartConf();
    ServerContext serverContext = new ServerContext(smartConf, metaStore);
    serverContext.setServiceMode(ServiceMode.HDFS);

    auditService = new AuditService(metaStore.userActivityDao());
    RuleLifecycleLogger lifecycleLogger = new RuleLifecycleLogger(auditService);

    ruleManager = new RuleManager(serverContext, null, null, lifecycleLogger);
    ruleManager.init();
    ruleManager.start();
  }

  @After
  public void close() throws Exception {
    ruleManager.stop();
  }

  @Test
  public void testSuccessfullySubmitRule() throws Exception {
    String rule = "file: every 1s \n | accessCount(5s) > 3 | cache";
    long id = ruleManager.submitRule(rule, RuleState.DISABLED);
    RuleInfo ruleInfo = ruleManager.getRuleInfo(id);
    Assert.assertEquals(ruleInfo.getRuleText(), rule);

    List<UserActivityEvent> ruleEvents = findRuleEvents(id);
    Assert.assertEquals(1, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.CREATE, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  @Test
  public void testSubmitRuleWithError() {
    String rule = "wrong syntax rule";
    try {
      ruleManager.submitRule(rule, RuleState.DISABLED);
    } catch (Exception ignore) {
    }

    List<UserActivityEvent> ruleEvents = auditService.search(AuditSearchRequest.empty());
    Assert.assertEquals(1, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.CREATE, event.getOperation());
    Assert.assertEquals(UserActivityResult.FAILURE, event.getResult());
  }

  @Test
  public void testStartRuleWithError() {
    try {
      // unknown id
      ruleManager.activateRule(777L);
    } catch (Exception ignore) {
    }

    List<UserActivityEvent> ruleEvents = findRuleEvents(777L);
    Assert.assertEquals(1, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.START, event.getOperation());
    Assert.assertEquals(UserActivityResult.FAILURE, event.getResult());
  }

  @Test
  public void testSuccessfullyStopRule() throws Exception {
    String rule = "file: every 1s \n | accessCount(5s) > 3 | cache";
    long id = ruleManager.submitRule(rule, RuleState.DISABLED);

    ruleManager.disableRule(id, false);

    List<UserActivityEvent> ruleEvents = findRuleEvents(id);
    Assert.assertEquals(2, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(1);
    Assert.assertEquals(UserActivityEvent.Operation.STOP, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  @Test
  public void testStopRuleWithError() {
    try {
      // unknown id
      ruleManager.disableRule(777L, false);
    } catch (Exception ignore) {
    }

    List<UserActivityEvent> ruleEvents = findRuleEvents(777L);
    Assert.assertEquals(1, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.STOP, event.getOperation());
    Assert.assertEquals(UserActivityResult.FAILURE, event.getResult());
  }

  @Test
  public void testSuccessfullyDeleteRule() throws Exception {
    String rule = "file: every 1s \n | accessCount(5s) > 3 | cache";
    long id = ruleManager.submitRule(rule, RuleState.DISABLED);

    ruleManager.deleteRule(id, false);

    List<UserActivityEvent> ruleEvents = findRuleEvents(id);
    Assert.assertEquals(2, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(1);
    Assert.assertEquals(UserActivityEvent.Operation.DELETE, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  @Test
  public void testDeleteRuleWithError() {
    try {
      // unknown id
      ruleManager.deleteRule(777L, false);
    } catch (Exception ignore) {
    }

    List<UserActivityEvent> ruleEvents = findRuleEvents(777L);
    Assert.assertEquals(1, ruleEvents.size());

    UserActivityEvent event = ruleEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.DELETE, event.getOperation());
    Assert.assertEquals(UserActivityResult.FAILURE, event.getResult());
  }

  private List<UserActivityEvent> findRuleEvents(long ruleId) {
    AuditSearchRequest searchRequest = AuditSearchRequest.builder()
        .objectIds(Collections.singletonList(ruleId))
        .objectTypes(Collections.singletonList(UserActivityEvent.ObjectType.RULE))
        .build();

    return auditService.search(searchRequest);
  }
}
