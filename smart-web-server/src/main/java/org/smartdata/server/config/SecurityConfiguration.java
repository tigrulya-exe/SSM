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
package org.smartdata.server.config;

import org.apache.commons.lang.StringUtils;
import org.smartdata.conf.SmartConf;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.server.security.SmartPrincipalInitializerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosClient;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosTicketValidator;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {
//  @Bean
//  @ConditionalOnProperty(
//      name = SmartConfKeys.SMART_SECURITY_ENABLE,
//      havingValue = "false",
//      matchIfMissing = true)
//  public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
//    withDisabledCsrf(http)
//        .authorizeRequests()
//        .anyRequest()
//        .permitAll();
//    return http.build();
//  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public SecurityFilterChain kerberosSecurityFilterChain(
      HttpSecurity http,
      SpnegoAuthenticationProcessingFilter spnegoFilter,
      UsernamePasswordAuthenticationFilter usernameFilter) throws Exception {
    withDisabledCsrf(http)
        .anonymous().disable()
        .addFilterBefore(spnegoFilter, BasicAuthenticationFilter.class)
        .addFilterAfter(
            usernameFilter, SpnegoAuthenticationProcessingFilter.class)
        .addFilterAfter(
            new SmartPrincipalInitializerFilter(), BasicAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated()
//        .and()
//        .formLogin()
        .and()
        .rememberMe()
        .and()
        .logout(logout -> logout.deleteCookies("JSESSIONID")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()));
    return http.build();
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
      AuthenticationManager authenticationManager) {
    SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
    filter.setAuthenticationManager(authenticationManager);
    return filter;
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public AuthenticationManager authManager(
      HttpSecurity http,
      KerberosAuthenticationProvider authenticationProvider,
      KerberosServiceAuthenticationProvider serviceAuthenticationProvider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(authenticationProvider)
        .authenticationProvider(serviceAuthenticationProvider)
        .build();
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public KerberosAuthenticationProvider kerberosAuthenticationProvider(
      UserDetailsService userDetailsService) {
    KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
    KerberosClient client = new SunJaasKerberosClient();
    provider.setKerberosClient(client);
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider(
      KerberosTicketValidator kerberosTicketValidator,
      UserDetailsService userDetailsService) {
    KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
    provider.setTicketValidator(kerberosTicketValidator);
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public KerberosTicketValidator kerberosTicketValidator(SmartConf smartConf) {
    SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
    // todo validate non null
//    String principal = smartConf.get(SmartConfKeys.SMART_SERVER_KERBEROS_PRINCIPAL_KEY);
    String principal = "HTTP/myhost.com@ARENADATA.IO";
    ticketValidator.setServicePrincipal(principal);
//    String keytabPath = smartConf.get(SmartConfKeys.SMART_SERVER_KEYTAB_FILE_KEY);
    String keytabPath = "test";
    ticketValidator.setKeyTabLocation(new FileSystemResource(keytabPath));
    return ticketValidator;
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public SpnegoEntryPoint spnegoEntryPoint() {
    return new SpnegoEntryPoint("/api/v2/login");
  }

  @Bean
//  @ConditionalOnProperty(name = SmartConfKeys.SMART_SECURITY_ENABLE, havingValue = "true")
  public UserDetailsService userDetailsService(SmartConf smartConf) {
    List<UserDetails> predefinedUsers = parsePredefinedUsers(smartConf);
    return new InMemoryUserDetailsManager(predefinedUsers);
  }

  @Bean
  public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(
      AuthenticationManager authManager) {
    UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
    filter.setAuthenticationManager(authManager);
    filter.setFilterProcessesUrl("/api/v2/login");
//    filter.setAuthenticationSuccessHandler();
//    filter.setAuthenticationSuccessHandler();
    return filter;
  }

  private List<UserDetails> parsePredefinedUsers(SmartConf smartConf) {
    return Collections.singletonList(User.withUsername("tigran")
        .password("manasyan")
        .roles()
        .build());
//    return smartConf.getStringCollection("static.users")
//        .stream()
//        .map(this::parsePredefinedUser)
//        .collect(Collectors.toList());
  }

  private UserDetails parsePredefinedUser(String rawUser) {
    String[] userParts = rawUser.split(":");
    if (userParts.length < 2 || userParts.length > 3) {
      throw new IllegalArgumentException(
          "Wrong form of user, should be username:password[:roles]");
    }

    List<GrantedAuthority> authorities = Collections.emptyList();
    String username = userParts[0];
    String password = userParts[1];
    if (userParts.length == 3) {
      authorities = getRoles(userParts[2]);
    }

    return User.withUsername(username)
        .password(password)
        .authorities("static")
        .build();
  }

  @Bean
  PasswordEncoder getPasswordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  private List<GrantedAuthority> getRoles(String rawRoles) {
    return StringUtils.isBlank(rawRoles)
        ? Collections.emptyList()
        : Arrays.stream(rawRoles.split(";"))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  private HttpSecurity withDisabledCsrf(HttpSecurity http) throws Exception {
    return http.cors()
        .disable()
        .csrf()
        .disable();
  }
}
