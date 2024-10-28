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
package org.smartdata.server.config.ldap.search;

import org.junit.Test;
import org.smartdata.conf.SmartConf;
import org.smartdata.server.config.ldap.search.user.UserSearchByCustomQueryFactory;

import static org.smartdata.server.config.ConfigKeys.SMART_REST_SERVER_LDAP_CUSTOM_SEARCH;

public class TestUserSearchByCustomQueryFactory extends TestLdapSearchTemplateFactory {

  @Override
  protected LdapSearchTemplateFactory create(SmartConf conf) {
    return new UserSearchByCustomQueryFactory(conf);
  }

  @Test
  public void checkGeneratedQueryWithCustomConf() {
    String expectedTemplate = "(&(class=test)(uid={0}))";
    SmartConf conf = new SmartConf();
    conf.set(SMART_REST_SERVER_LDAP_CUSTOM_SEARCH, expectedTemplate);

    checkGeneratedSearchTemplate(conf, expectedTemplate);
  }
}
