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

package org.smartdata.hdfs.action;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.tools.DistCp;
import org.apache.hadoop.tools.DistCpOptions;
import org.apache.hadoop.tools.OptionsParser;
import org.smartdata.action.Utils;
import org.smartdata.action.annotation.ActionSignature;

@ActionSignature(
    actionId = "distCp",
    displayName = "DistCp",
    usage = DistCpAction.FILE_PATH + " $path "
        + DistCpAction.TARGET_ARG + " $target "
        + " [additional options from "
        + "https://hadoop.apache.org/docs/stable/hadoop-distcp/DistCp.html#Command_Line_Options]"
)
public class DistCpAction extends HdfsAction {

  public static final String TARGET_ARG = "-target";
  public static final String SOURCE_PATH_LIST_FILE = "-f";

  private static final String SOURCE_PATHS_DELIMITER = ",";
  private static final Set<String> NON_DISTCP_OPTIONS = Sets.newHashSet(FILE_PATH, TARGET_ARG);

  private DistCpOptions options;

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    validateRequiredOptions(args);

    List<String> rawArgs = args.entrySet()
        .stream()
        .filter(entry -> !NON_DISTCP_OPTIONS.contains(entry.getKey()))
        .flatMap(entry -> mapOptionToStr(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    if (!args.containsKey(SOURCE_PATH_LIST_FILE)) {
      rawArgs.addAll(parseSourcePaths(args.get(FILE_PATH)));
    }
    rawArgs.add(args.get(TARGET_ARG));

    options = OptionsParser.parse(rawArgs.toArray(new String[0]));
  }

  @Override
  protected void execute() throws Exception {
    DistCp distCp = new DistCp(getContext().getConf(), options);
    appendLog(
        String.format("DistCp Action started at %s for options %s",
            Utils.getFormatedCurrentTime(), options));

    try (JobCloseableWrapper jobWrapper = new JobCloseableWrapper(distCp.execute())) {
      distCp.waitForJobCompletion(jobWrapper.job);
    }
  }

  DistCpOptions getOptions() {
    return options;
  }

  private List<String> parseSourcePaths(String sourcePaths) {
    return Arrays.asList(sourcePaths.split(SOURCE_PATHS_DELIMITER));
  }

  private void validateRequiredOptions(Map<String, String> args) {
    if (!args.containsKey(FILE_PATH) && !args.containsKey(SOURCE_PATH_LIST_FILE)) {
      throw new IllegalArgumentException("Source paths not provided, please provide either "
          + FILE_PATH + " either " + SOURCE_PATH_LIST_FILE + " argument");
    }

    if (args.containsKey(FILE_PATH) && args.containsKey(SOURCE_PATH_LIST_FILE)) {
      throw new IllegalArgumentException(FILE_PATH + " and " + SOURCE_PATH_LIST_FILE
          + " can't be used at the same time. Use only one of the options for specifying source paths.");
    }

    if (!args.containsKey(TARGET_ARG)) {
      throw new IllegalArgumentException("Required argument not present: " + TARGET_ARG);
    }
  }

  private Stream<String> mapOptionToStr(String key, String value) {
    if (value.isEmpty()) {
      return Stream.of(key);
    }
    return Stream.of(key, value);
  }

  /** Used to gracefully close MapReduce job (MR Job is not AutoCloseable inheritor in Hadoop 2.7) */
  private static class JobCloseableWrapper implements AutoCloseable {

    private final Job job;

    private JobCloseableWrapper(Job job) {
      this.job = job;
    }

    @Override
    public void close() throws Exception {
      Cluster cluster = job.getCluster();
      if (cluster != null) {
        cluster.close();
      }
    }
  }
}
