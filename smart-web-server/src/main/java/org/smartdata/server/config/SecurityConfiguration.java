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
package org.smartdata.server.config;

import org.smartdata.conf.SmartConfKeys;
import org.smartdata.server.security.SmartPrincipalInitializerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {
  @Bean
  @ConditionalOnProperty(
      name = SmartConfKeys.SMART_SECURITY_ENABLE,
      havingValue = "false",
      matchIfMissing = true)
  public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .anyRequest()
        .permitAll();
    return http.build();
  }

  @Bean
  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public SecurityFilterChain kerberosSecurityFilterChain(HttpSecurity http) throws Exception {
    http.addFilterBefore(
        new SmartPrincipalInitializerFilter(), AnonymousAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        // todo ADH-4364: replace with SPNEGO filter registration
        .permitAll();
    return http.build();
  }
}

