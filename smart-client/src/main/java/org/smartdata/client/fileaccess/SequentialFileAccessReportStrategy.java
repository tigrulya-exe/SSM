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

/**
 * A simple report strategy that tries to connect to smart servers one by one.
 */
public class SequentialFileAccessReportStrategy implements FileAccessReportStrategy {

  private final SmartServerHandles smartServerHandles;

  public SequentialFileAccessReportStrategy(
      SmartServerHandles smartServerHandles) {
    this.smartServerHandles = smartServerHandles;
  }

  @Override
  public SmartServerHandle reportFileAccessEvent(FileAccessEvent event) throws IOException {
    Exception lastException = null;

    for (SmartServerHandle serverHandle: smartServerHandles.handles()) {
      try {
        serverHandle.getProtocol().reportFileAccessEvent(event);
        return serverHandle;
      } catch (IOException exception) {
        lastException = exception;
      }
    }

    throw new IOException(
        "Failed to report access event to SSM servers", lastException);
  }

  @Override
  public void close() throws IOException {
    // do nothing
  }
}
