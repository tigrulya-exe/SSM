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
import org.smartdata.model.UserActivityEvent;
import org.smartdata.model.UserActivityResult;
import org.smartdata.model.request.AuditSearchRequest;
import org.smartdata.server.engine.CmdletManager;
import org.smartdata.server.engine.ServerContext;
import org.smartdata.server.engine.ServiceMode;
import org.smartdata.server.engine.cmdlet.CmdletDispatcherHelper;

import java.util.Collections;
import java.util.List;

public class TestCmdletLifecycleLogger extends SqliteTestDaoBase {
  private CmdletManager cmdletManager;
  private AuditService auditService;

  @Before
  public void init() throws Exception {
    SmartConf smartConf = new SmartConf();
    ServerContext serverContext = new ServerContext(smartConf, metaStore);
    serverContext.setServiceMode(ServiceMode.HDFS);

    auditService = new AuditService(metaStore.userActivityDao());
    CmdletLifecycleLogger lifecycleLogger = new CmdletLifecycleLogger(auditService);

    CmdletDispatcherHelper.init();
    cmdletManager = new CmdletManager(serverContext, lifecycleLogger);
    cmdletManager.init();
    cmdletManager.start();
  }

  @After
  public void close() throws Exception {
    cmdletManager.stop();
  }

  @Test
  public void testSuccessfullySubmitCmdlet() throws Exception {
    String cmdlet = "cache -file /testCacheFile";
    long id = cmdletManager.submitCmdlet(cmdlet);

    List<UserActivityEvent> cmdletEvents = findCmdletEvents(id);
    Assert.assertEquals(1, cmdletEvents.size());

    UserActivityEvent event = cmdletEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.START, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  @Test
  public void testSubmitCmdletWithError() {
    String cmdlet = "wrong syntax cmdlet";
    try {
      cmdletManager.submitCmdlet(cmdlet);
    } catch (Exception ignore) {
    }

    List<UserActivityEvent> cmdletEvents = auditService.search(AuditSearchRequest.empty());
    Assert.assertEquals(1, cmdletEvents.size());

    UserActivityEvent event = cmdletEvents.get(0);
    Assert.assertEquals(UserActivityEvent.Operation.START, event.getOperation());
    Assert.assertEquals(UserActivityResult.FAILURE, event.getResult());
  }

  @Test
  public void testSuccessfullyStopCmdlet() throws Exception {
    String cmdlet = "cache -file /testCacheFile";
    long id = cmdletManager.submitCmdlet(cmdlet);

    cmdletManager.disableCmdlet(id);

    List<UserActivityEvent> cmdletEvents = findCmdletEvents(id);
    Assert.assertEquals(2, cmdletEvents.size());

    UserActivityEvent event = cmdletEvents.get(1);
    Assert.assertEquals(UserActivityEvent.Operation.STOP, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  @Test
  public void testSuccessfullyDeleteCmdlet() throws Exception {
    String cmdlet = "cache -file /testCacheFile";
    long id = cmdletManager.submitCmdlet(cmdlet);

    cmdletManager.deleteCmdlet(id);

    List<UserActivityEvent> cmdletEvents = findCmdletEvents(id);
    Assert.assertEquals(2, cmdletEvents.size());

    UserActivityEvent event = cmdletEvents.get(1);
    Assert.assertEquals(UserActivityEvent.Operation.DELETE, event.getOperation());
    Assert.assertEquals(UserActivityResult.SUCCESS, event.getResult());
  }

  private List<UserActivityEvent> findCmdletEvents(long cmdletId) {
    AuditSearchRequest searchRequest = AuditSearchRequest.builder()
        .objectIds(Collections.singletonList(cmdletId))
        .objectTypes(Collections.singletonList(UserActivityEvent.ObjectType.CMDLET))
        .build();

    return auditService.search(searchRequest);
  }
}
