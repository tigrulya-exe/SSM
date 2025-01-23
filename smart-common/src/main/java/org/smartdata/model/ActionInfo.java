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

package org.smartdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.smartdata.utils.FormattingUtil.actionToString;

@Data
@AllArgsConstructor
@Builder(setterPrefix = "set", toBuilder = true)
public class ActionInfo {
  // Old file id
  public static final String OLD_FILE_ID = "-oid";

  private final long actionId;
  private final long cmdletId;
  private final String actionName;
  private final long createTime;

  private Map<String, String> args;
  @lombok.Builder.Default
  private String result = "";
  @lombok.Builder.Default
  private String log = "";

  // For action set flexibility
  private boolean successful;
  private boolean finished;
  private Long startTime;
  private Long finishTime;
  private float progress;
  private String execHost;

  public String getActionText() {
    return actionToString(actionName, args);
  }

  public ActionSource getSource() {
    return Optional.ofNullable(args)
        .filter(map -> map.containsKey(CmdletDescriptor.RULE_ID))
        .map(hasRuleIdArg -> ActionSource.RULE)
        .orElse(ActionSource.USER);
  }

  // Applicable to some actions that need to create new file to replace
  // the old one.
  public void setOldFileIds(List<Long> oldFileIds) {
    args.put(OLD_FILE_ID, fileIdsToArg(oldFileIds));
  }

  // Applicable to some actions that need to create new file to replace
  // the old one.
  public List<Long> getOldFileIds() {
    return argToFileIds(args.get(OLD_FILE_ID));
  }

  private String fileIdsToArg(List<Long> fileIds) {
    if (CollectionUtils.isEmpty(fileIds)) {
      return "";
    }

    return fileIds.stream()
        .map(Object::toString)
        .collect(Collectors.joining(","));
  }

  private List<Long> argToFileIds(String fileIdsString) {
    if (StringUtils.isBlank(fileIdsString)) {
      return Collections.emptyList();
    }

    // replace for backward compatibility
    return Arrays.stream(fileIdsString.replaceAll("\\[|]|\\s", "").split(","))
        .map(Long::valueOf)
        .collect(Collectors.toList());
  }

  public void appendLog(String log) {
    this.log += log;
  }

  public void appendLogLine(String logLine) {
    this.log += logLine + "\n";
  }
}
