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

import org.smartdata.client.SmartServerHandle;
import org.smartdata.client.SmartServerHandles;
import org.smartdata.metrics.FileAccessEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Report file access event concurrently. Only one server is active, so
 * reporting to this server should be successful.
 */
public class ParallelFileAccessReportStrategy implements FileAccessReportStrategy {
  private final SmartServerHandles smartServerHandles;
  private final ExecutorService accessReportExecutor;
  private final long reportTasksTimeoutMs;

  public ParallelFileAccessReportStrategy(
      SmartServerHandles smartServerHandles,
      long reportTasksTimeoutMs) {
    this.smartServerHandles = smartServerHandles;
    this.reportTasksTimeoutMs = reportTasksTimeoutMs;
    this.accessReportExecutor = Executors.newFixedThreadPool(
        smartServerHandles.handles().size());
  }

  private Callable<SmartServerHandle> reportFileAccessTask(
      SmartServerHandle serverHandle, FileAccessEvent event) {
    return () -> {
      serverHandle.getProtocol().reportFileAccessEvent(event);
      return serverHandle;
    };
  }

  @Override
  public SmartServerHandle reportFileAccessEvent(
      FileAccessEvent event) throws IOException {
    Collection<Callable<SmartServerHandle>> reportFileTasks = smartServerHandles
        .handles()
        .stream()
        .map(server -> reportFileAccessTask(server, event))
        .collect(Collectors.toList());

    try {
      return accessReportExecutor.invokeAny(
          reportFileTasks, reportTasksTimeoutMs, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new IOException("Failed to report access event to SSM servers", e);
    }
  }

  @Override
  public void close() throws IOException {
    accessReportExecutor.shutdownNow();
  }
}
