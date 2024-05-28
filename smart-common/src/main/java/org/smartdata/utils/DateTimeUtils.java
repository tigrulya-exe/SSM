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
package org.smartdata.utils;

import org.smartdata.model.TimeInterval;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

public class DateTimeUtils {
  public static Long intervalStartToEpoch(TimeInterval timeInterval) {
    return intervalToEpoch(timeInterval, TimeInterval::getFrom);
  }

  public static Long intervalEndToEpoch(TimeInterval timeInterval) {
    return intervalToEpoch(timeInterval, TimeInterval::getTo);
  }

  private static Long intervalToEpoch(
      TimeInterval timeInterval, Function<TimeInterval, Instant> instantGetter) {
    return Optional.ofNullable(timeInterval)
        .map(instantGetter)
        .map(Instant::toEpochMilli)
        .orElse(null);
  }
}
