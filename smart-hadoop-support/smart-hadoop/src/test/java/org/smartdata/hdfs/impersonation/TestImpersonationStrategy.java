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
package org.smartdata.hdfs.impersonation;

import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;
import org.smartdata.conf.SmartConf;

import static org.junit.Assert.assertEquals;

public class TestImpersonationStrategy {
  @Test
  public void testDisabledStrategy() throws Exception {
    testStrategy(
        new DisabledUserImpersonationStrategy(),
        "someUser",
        UserGroupInformation.getCurrentUser().getShortUserName()
    );
  }

  @Test
  public void testExplicitStrategy() throws Exception {
    String nodeProxyUser = "someNodeProxyUser";

    testStrategy(
        new ExplicitUserImpersonationStrategy(nodeProxyUser),
        "someUser",
        nodeProxyUser
    );
  }

  @Test
  public void testCmdletOwnerStrategy() throws Exception {
    String cmdletOwner = "cmdletOwner";

    testStrategy(
        CmdletOwnerUserImpersonationStrategy.from(new SmartConf()),
        cmdletOwner,
        cmdletOwner
    );
  }

  private void testStrategy(
      UserImpersonationStrategy strategy, String user, String expectedProxyUser) throws Exception {
    strategy.runWithImpersonation(user, () -> {
      assertEquals(UserGroupInformation.getCurrentUser().getShortUserName(), expectedProxyUser);
      return null;
    });
  }
}
