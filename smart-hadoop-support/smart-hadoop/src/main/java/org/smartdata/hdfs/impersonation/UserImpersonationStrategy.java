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

import org.smartdata.conf.SmartConf;
import org.smartdata.protocol.message.LaunchCmdlet;

import java.security.PrivilegedExceptionAction;

import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USER_STRATEGY_KEY;

public interface UserImpersonationStrategy {
  enum Scope {
    DISABLED,
    NODE_SCOPE,
    CMDLET_SCOPE
  }

  String getUserFor(LaunchCmdlet launchCmdlet);

  void runWithImpersonation(String currentUser, Runnable action);

  <T> T runWithImpersonation(String currentUser, PrivilegedExceptionAction<T> action) throws Exception;

  static UserImpersonationStrategy from(SmartConf conf) {
    Scope impersonationScope = conf.getEnum(SMART_PROXY_USER_STRATEGY_KEY, Scope.DISABLED);
    switch (impersonationScope) {
      case NODE_SCOPE:
        return ExplicitUserImpersonationStrategy.from(conf);
      case CMDLET_SCOPE:
        return CmdletOwnerUserImpersonationStrategy.from(conf);
      default:
        return new DisabledUserImpersonationStrategy();
    }
  }
}
