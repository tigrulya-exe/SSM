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
package org.smartdata.server;

import org.smartdata.conf.SmartConf;
import org.smartdata.server.config.SsmContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

public class SmartRestServer {
  private final SpringApplication springApplication;

  private volatile ConfigurableApplicationContext applicationContext;

  public SmartRestServer(SmartConf ssmConfig, SmartEngine smartEngine) {
    this.springApplication = new SpringApplication(RestServerApplication.class);

    SsmContextInitializer contextInitializer =
        new SsmContextInitializer(smartEngine, ssmConfig);
    springApplication.addInitializers(contextInitializer);
  }

  public void start() {
    applicationContext = springApplication.run();
  }

  public void stop() {
    if (isRunning()) {
      SpringApplication.exit(applicationContext);
    }
  }

  private boolean isRunning() {
    return applicationContext != null;
  }

  // todo remove embedded server autoconfig exclusion after zeppelin removal
  @SpringBootApplication(exclude = EmbeddedWebServerFactoryCustomizerAutoConfiguration.class)
  public static class RestServerApplication {
    // empty class just to enable auto configs
  }
}
