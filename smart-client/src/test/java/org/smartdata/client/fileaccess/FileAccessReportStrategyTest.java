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
package org.smartdata.client.fileaccess;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.smartdata.client.MockSmartServer;
import org.smartdata.client.SmartServerHandle;
import org.smartdata.client.SmartServerHandles;
import org.smartdata.metrics.FileAccessEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.smartdata.client.MockSmartServer.activeServerHandle;
import static org.smartdata.client.MockSmartServer.standbyServerHandle;

public abstract class FileAccessReportStrategyTest {

  protected abstract Configuration getConfig();

  @Test
  public void testReportFileAccess() throws IOException {
    List<SmartServerHandle> serverHandles = Arrays.asList(
        activeServerHandle(new InetSocketAddress(1)),
        standbyServerHandle(new InetSocketAddress(2)),
        standbyServerHandle(new InetSocketAddress(3))
    );

    testReportFileAccessInternal(serverHandles, 0);
  }

  @Test
  public void testReportFileAccessWithNewActiveServer() throws IOException {
    List<SmartServerHandle> serverHandles = Arrays.asList(
        standbyServerHandle(new InetSocketAddress(2)),
        standbyServerHandle(new InetSocketAddress(3)),
        activeServerHandle(new InetSocketAddress(1))
    );

    testReportFileAccessInternal(serverHandles, 2);
  }

  @Test
  public void testFailIfNoActiveServer() {
    List<SmartServerHandle> serverHandles = Arrays.asList(
        standbyServerHandle(new InetSocketAddress(2)),
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

  protected void testReportFileAccessInternal(
      List<SmartServerHandle> serverHandlesList,
      int activeServerIdx) throws IOException {

    SmartServerHandle activeServerHandle = serverHandlesList.get(activeServerIdx);

    Map<String, Integer> expectedFileAccessCounts = buildExpectedFileAccessCounts();

    try (FileAccessReportStrategy strategy = FileAccessReportStrategy.from(
        getConfig(), new SmartServerHandles(serverHandlesList))) {

      for (String file : expectedFileAccessCounts.keySet()) {
        FileAccessEvent fileAccessEvent = new FileAccessEvent(file);
        SmartServerHandle reportedServer =
            strategy.reportFileAccessEvent(fileAccessEvent);

        assertEquals(reportedServer, activeServerHandle);
      }
    }

    MockSmartServer activeServer = getTestSmartServer(activeServerHandle);
    assertEquals(expectedFileAccessCounts,
        activeServer.getReportedAccessCounts());
  }

  private Map<String, Integer> buildExpectedFileAccessCounts() {
    return IntStream.range(0, 10)
        .mapToObj(val -> "/test" + val)
        .collect(Collectors.toMap(
            Function.identity(),
            ignore -> 1
        ));
  }

  private MockSmartServer getTestSmartServer(SmartServerHandle serverHandle) {
    return (MockSmartServer) serverHandle.getProtocol();
  }
}
