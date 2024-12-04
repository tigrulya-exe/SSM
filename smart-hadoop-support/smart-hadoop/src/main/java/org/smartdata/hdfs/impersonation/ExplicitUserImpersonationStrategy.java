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
import org.smartdata.conf.SmartConf;
import org.smartdata.protocol.message.LaunchCmdlet;

import java.util.Optional;

import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USER_KEY;

public class ExplicitUserImpersonationStrategy extends BaseUserImpersonationStrategy {

  private final UserGroupInformation proxyUser;

  public ExplicitUserImpersonationStrategy(String user) {
    this.proxyUser = createProxyUser(user);
  }

  @Override
  public String getUserFor(LaunchCmdlet launchCmdlet) {
    return proxyUser.getShortUserName();
  }

  @Override
  protected UserGroupInformation getProxyUserFor(String user) {
    return proxyUser;
  }

  public static ExplicitUserImpersonationStrategy from(SmartConf conf) {
    return Optional.ofNullable(conf.get(SMART_PROXY_USER_KEY))
        .map(ExplicitUserImpersonationStrategy::new)
        .orElseThrow(() -> new IllegalArgumentException(SMART_PROXY_USER_KEY + " option not set"));
  }
}
