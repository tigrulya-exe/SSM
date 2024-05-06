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

package org.smartdata.client.fileaccess;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.smartdata.client.MockSmartServer;
import org.smartdata.client.SmartServerHandle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.smartdata.client.MockSmartServer.standbyServerHandle;
import static org.smartdata.conf.SmartConfKeys.SMART_CLIENT_CONCURRENT_REPORT_ENABLED;
import static org.smartdata.conf.SmartConfKeys.SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_KEY;

public class ParallelFileAccessReportStrategyTest extends FileAccessReportStrategyTest {

  public static final long REPORT_TASKS_TIMEOUT_MS = 1000;

  @Override
  protected Configuration getConfig() {
    Configuration config = new Configuration();
    config.setBoolean(SMART_CLIENT_CONCURRENT_REPORT_ENABLED, true);
    config.setLong(SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_KEY, REPORT_TASKS_TIMEOUT_MS);
    return config;
  }

  @Test
  public void testReportFileAccessWithSlowActiveServer() throws IOException {
    List<SmartServerHandle> serverHandles = Arrays.asList(
        standbyServerHandle(new InetSocketAddress(2)),
        delayedActiveServerHandle(REPORT_TASKS_TIMEOUT_MS / 2),
        standbyServerHandle(new InetSocketAddress(3))
    );

    testReportFileAccessInternal(serverHandles, 1);
  }

  @Test
  public void testFailIfTimeout() {
    List<SmartServerHandle> serverHandles = Arrays.asList(
        delayedActiveServerHandle(REPORT_TASKS_TIMEOUT_MS * 2),
        standbyServerHandle(new InetSocketAddress(3)),
        standbyServerHandle(new InetSocketAddress(1))
    );

    IOException exception = assertThrows(
        IOException.class,
        () -> testReportFileAccessInternal(serverHandles, 0));

    assertEquals(
        "Failed to report access event to SSM servers",
        exception.getMessage());
  }

  private SmartServerHandle delayedActiveServerHandle(long delayMs) {
    MockSmartServer activeSmartServer = MockSmartServer.builder()
        .withDelay(delayMs)
        .build();

    return new SmartServerHandle(
        activeSmartServer, new InetSocketAddress(11));
  }
}
