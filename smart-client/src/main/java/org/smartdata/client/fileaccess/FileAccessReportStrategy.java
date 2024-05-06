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
import org.smartdata.client.SmartServerHandle;
import org.smartdata.client.SmartServerHandles;
import org.smartdata.conf.SmartConfKeys;
import org.smartdata.metrics.FileAccessEvent;

import java.io.Closeable;
import java.io.IOException;

public interface FileAccessReportStrategy extends Closeable {

  /**
   * Reports file access event to the active Smart Server.
   * @return Handle of the Smart Server to which the event was successfully sent.
   */
  SmartServerHandle reportFileAccessEvent(FileAccessEvent event) throws IOException;

  static FileAccessReportStrategy from(
      Configuration config, SmartServerHandles smartServerHandles) {
    boolean parallelReportEnabled = config.getBoolean(
        SmartConfKeys.SMART_CLIENT_CONCURRENT_REPORT_ENABLED,
        SmartConfKeys.SMART_CLIENT_CONCURRENT_REPORT_ENABLED_DEFAULT);

    if (parallelReportEnabled) {
      long reportTasksTimeoutMs = config.getLong(
          SmartConfKeys.SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_KEY,
          SmartConfKeys.SMART_CLIENT_REPORT_TASKS_TIMEOUT_MS_DEFAULT);

      return new ParallelFileAccessReportStrategy(
          smartServerHandles, reportTasksTimeoutMs);
    }

    return new SequentialFileAccessReportStrategy(smartServerHandles);
  }
}
