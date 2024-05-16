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
package org.smartdata.client;

import org.smartdata.metrics.FileAccessEvent;
import org.smartdata.model.FileState;
import org.smartdata.protocol.SmartClientProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class MockSmartServer implements SmartClientProtocol {
  private final boolean failReportAccessEvent;
  private final boolean failGetFileState;
  private final long delayMs;
  private final Map<String, FileState> expectedFileStates;
  private final Map<String, Integer> reportedAccessCounts;

  private MockSmartServer(
      boolean failReportAccessEvent,
      boolean failGetFileState,
      long delayMs,
      Map<String, FileState> expectedFileStates) {
    this.failReportAccessEvent = failReportAccessEvent;
    this.failGetFileState = failGetFileState;
    this.expectedFileStates = expectedFileStates;
    this.delayMs = delayMs;
    this.reportedAccessCounts = new HashMap<>();
  }

  @Override
  public void reportFileAccessEvent(FileAccessEvent event) throws IOException {
    if (failReportAccessEvent) {
      throw new IOException();
    }

    maybeSleep();
    reportedAccessCounts.merge(event.getPath(), 1, Integer::sum);
  }

  @Override
  public FileState getFileState(String filePath) throws IOException {
    if (failGetFileState) {
      throw new IOException();
    }

    maybeSleep();
    return expectedFileStates.get(filePath);
  }

  private void maybeSleep() {
    if (delayMs <= 0) {
      return;
    }

    try {
      Thread.sleep(delayMs);
    } catch (InterruptedException ignore) {
      // no-op
    }
  }

  public Map<String, Integer> getReportedAccessCounts() {
    return reportedAccessCounts;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static MockSmartServer standbyServer() {
    return builder()
        .failReportAccessEvent()
        .failGetFileState()
        .build();
  }

  public static MockSmartServer activeServer() {
    return builder().build();
  }

  public static SmartServerHandle standbyServerHandle(InetSocketAddress address) {
    return new SmartServerHandle(standbyServer(), address);
  }

  public static SmartServerHandle activeServerHandle(InetSocketAddress address) {
    return new SmartServerHandle(activeServer(), address);
  }

  public static class Builder {
    private boolean failReportAccessEvent;
    private boolean failGetFileState;
    private long delayMs;
    private Map<String, FileState> expectedFileStates;

    private Builder() {
      this.failReportAccessEvent = false;
      this.failGetFileState = false;
      this.delayMs = 0;
      this.expectedFileStates = new HashMap<>();
    }

    public Builder failReportAccessEvent() {
      this.failReportAccessEvent = true;
      return this;
    }

    public Builder failGetFileState() {
      this.failGetFileState = true;
      return this;
    }

    public Builder returnFileState(String path, FileState state) {
      expectedFileStates.put(path, state);
      return this;
    }

    public Builder returnFileStates(Map<String, FileState> fileStates) {
      this.expectedFileStates = fileStates;
      return this;
    }

    public Builder withDelay(long delayMs) {
      this.delayMs = delayMs;
      return this;
    }

    public MockSmartServer build() {
      return new MockSmartServer(
          failReportAccessEvent,
          failGetFileState,
          delayMs,
          expectedFileStates
      );
    }
  }
}
