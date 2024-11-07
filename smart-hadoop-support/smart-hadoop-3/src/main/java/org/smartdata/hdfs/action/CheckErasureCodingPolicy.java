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
package org.smartdata.hdfs.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.smartdata.action.annotation.ActionSignature;

import java.util.Map;
import java.util.Optional;

/**
 * An action to check the EC policy for a file or dir.
 */
@ActionSignature(
    actionId = "checkec",
    displayName = "checkec",
    usage = HdfsAction.FILE_PATH + " $src"
)
public class CheckErasureCodingPolicy extends HdfsAction {
  public static final String RESULT_OF_NULL_EC_POLICY =
      "The EC policy is replication.";
  private String srcPath;

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    this.srcPath = args.get(HdfsAction.FILE_PATH);
  }

  @Override
  public void execute() throws Exception {
    if (StringUtils.isBlank(srcPath)) {
      throw new IllegalArgumentException("File parameter is missing! ");
    }

    String result = Optional.ofNullable(dfsClient.getErasureCodingPolicy(srcPath))
        .map(ErasureCodingPolicy::toString)
        .orElse(RESULT_OF_NULL_EC_POLICY);

    appendResult(result);
  }

  @Override
  public DfsClientType dfsClientType() {
    return DfsClientType.DEFAULT_HDFS;
  }
}
