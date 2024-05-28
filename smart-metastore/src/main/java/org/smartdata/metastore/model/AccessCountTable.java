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
package org.smartdata.metastore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.smartdata.metastore.utils.TimeGranularity;

import java.util.Random;

@Data
@EqualsAndHashCode(of = {"startTime", "endTime", "granularity"})
public class AccessCountTable {
  private final String tableName;
  private final long startTime;
  private final long endTime;
  private final TimeGranularity granularity;
  private final boolean isEphemeral;

  public AccessCountTable(long startTime, long endTime) {
    this(startTime, endTime, false);
  }

  public AccessCountTable(long startTime, long endTime, boolean isEphemeral) {
    this(getTableName(startTime, endTime, isEphemeral), startTime, endTime, isEphemeral);
  }

  public AccessCountTable(String name, long startTime, long endTime, boolean isEphemeral) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.granularity = TimeGranularity.of(endTime - startTime);
    this.tableName = name;
    this.isEphemeral = isEphemeral;
  }

  private static String getTableName(long startTime, long endTime, boolean isView) {
    String tableName = "accessCount_" + startTime + "_" + endTime;
    if (isView) {
      tableName += "_view_" + Math.abs(new Random().nextInt());
    }
    return tableName;
  }

  @Override
  public String toString() {
    return String.format(
        "AccessCountTable %s starts from %s ends with %s and granularity is %s",
        tableName, startTime, endTime, granularity);
  }

  public long interval() {
    return endTime - startTime;
  }

  public double intervalRatio(AccessCountTable other) {
    return ((double) interval()) / other.interval();
  }
}
