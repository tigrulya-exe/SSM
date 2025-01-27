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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.hadoop.security.UserGroupInformation;
import org.smartdata.conf.SmartConf;
import org.smartdata.protocol.message.LaunchCmdlet;
import org.smartdata.utils.StringUtil;

import java.time.Duration;

import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USERS_CACHE_SIZE_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USERS_CACHE_SIZE_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USERS_CACHE_TTL_DEFAULT;
import static org.smartdata.conf.SmartConfKeys.SMART_PROXY_USERS_CACHE_TTL_KEY;

public class CmdletOwnerUserImpersonationStrategy extends BaseUserImpersonationStrategy {

  private final Cache<String, UserGroupInformation> proxyUsersCache;

  public CmdletOwnerUserImpersonationStrategy(Duration entryTtl, int cacheMaxSize) {
    this.proxyUsersCache = Caffeine.newBuilder()
        .expireAfterAccess(entryTtl)
        .maximumSize(cacheMaxSize)
        .build();
  }

  @Override
  public String getUserFor(LaunchCmdlet launchCmdlet) {
    return launchCmdlet.getOwner();
  }

  @Override
  protected UserGroupInformation getProxyUserFor(String user) {
    return proxyUsersCache.get(user, this::createProxyUser);
  }

  public static CmdletOwnerUserImpersonationStrategy from(SmartConf conf) {
    int cacheMaxSize = conf.getInt(
        SMART_PROXY_USERS_CACHE_SIZE_KEY,
        SMART_PROXY_USERS_CACHE_SIZE_DEFAULT);

    String cacheEntryTtlRaw = conf.get(
        SMART_PROXY_USERS_CACHE_TTL_KEY,
        SMART_PROXY_USERS_CACHE_TTL_DEFAULT);
    Duration cacheEntryTtl = Duration.ofMillis(
        StringUtil.parseTimeString(cacheEntryTtlRaw));
    return new CmdletOwnerUserImpersonationStrategy(cacheEntryTtl, cacheMaxSize);
  }
}
