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

import lombok.RequiredArgsConstructor;
import org.smartdata.conf.SmartConf;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import static org.smartdata.conf.SmartConfKeys.SMART_REST_SERVER_PORT_KEY;
import static org.smartdata.conf.SmartConfKeys.SMART_REST_SERVER_PORT_KEY_DEFAULT;

@Component
@RequiredArgsConstructor
public class EmbeddedServerCustomizer
    implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

  private final SmartConf conf;

  @Override
  public void customize(ConfigurableWebServerFactory factory) {
    int serverPort = conf.getInt(SMART_REST_SERVER_PORT_KEY,
        SMART_REST_SERVER_PORT_KEY_DEFAULT);
    factory.setPort(serverPort);
  }
}
