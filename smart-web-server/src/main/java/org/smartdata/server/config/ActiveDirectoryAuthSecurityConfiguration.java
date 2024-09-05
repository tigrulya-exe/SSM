/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.config;

import org.smartdata.conf.SmartConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import java.util.Optional;

import static org.smartdata.server.config.ConfigKeys.AD_AUTH_ENABLED;
import static org.smartdata.server.config.ConfigKeys.WEB_SECURITY_ENABLED;

@Configuration
@ConditionalOnProperty(
    name = {WEB_SECURITY_ENABLED, AD_AUTH_ENABLED},
    havingValue = "true")
// todo
public class ActiveDirectoryAuthSecurityConfiguration {
  @Bean
  public SsmAuthHttpConfigurer activeDirectoryBasicAuthHttpConfigurer() {
    return new SecurityConfiguration.BasicAuthHttpConfigurer();
  }

  @Bean
  public AuthenticationProvider adAuthenticationProvider(SmartConf conf) {
    String adUrl = conf.get("smart.rest.server.security.active-directory.url");
    String adDomain = conf.get("smart.rest.server.security.active-directory.domain");
    String rootDn = conf.get("smart.rest.server.security.active-directory.root");
    String searchFilter = conf.get("smart.rest.server.security.active-directory.search.users.filter");

    ActiveDirectoryLdapAuthenticationProvider authenticationProvider =
        new ActiveDirectoryLdapAuthenticationProvider(adDomain, adUrl, rootDn);

    Optional.ofNullable(searchFilter)
        .ifPresent(authenticationProvider::setSearchFilter);

    return authenticationProvider;
  }

}
