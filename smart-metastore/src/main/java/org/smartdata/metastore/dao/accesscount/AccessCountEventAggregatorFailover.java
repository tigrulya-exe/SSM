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
package org.smartdata.metastore.dao.accesscount;

import org.smartdata.metastore.model.AggregatedAccessCounts;

import java.util.Map;

public interface AccessCountEventAggregatorFailover {
  enum Strategy {
    FAIL,
    DROP_EVENTS,
    SUBMIT_FAILED_EVENTS_LATER,
    SUBMIT_NEW_FAILED_EVENTS_LATER
  }

  /**
   * Returns the map of {@link AggregatedAccessCounts} to be saved
   * for subsequent submission attempts.
   */
  Map<String, AggregatedAccessCounts> handleError(
      Map<String, AggregatedAccessCounts> accessCounts,
      Map<String, AggregatedAccessCounts> previousUnmergedAccessCounts,
      Exception error
  );

  static AccessCountEventAggregatorFailover fail() {
    return (accessCounts, previousUnmergedAccessCounts, error) -> {
      throw new RuntimeException(error);
    };
  }

  static AccessCountEventAggregatorFailover dropEvents() {
    return (accessCounts, previousUnmergedAccessCounts, error) ->
        previousUnmergedAccessCounts;
  }

  static AccessCountEventAggregatorFailover submitFailedEventsLater() {
    return (accessCounts, previousUnmergedAccessCounts, error) ->
        accessCounts;
  }

  static AccessCountEventAggregatorFailover submitNewFailedEventsLater() {
    return (accessCounts, previousUnmergedAccessCounts, error) -> {
      accessCounts.keySet().removeAll(previousUnmergedAccessCounts.keySet());
      return accessCounts;
    };
  }

  static AccessCountEventAggregatorFailover of(Strategy strategy) {
    switch (strategy) {
      case FAIL:
        return fail();
      case SUBMIT_FAILED_EVENTS_LATER:
        return submitFailedEventsLater();
      case SUBMIT_NEW_FAILED_EVENTS_LATER:
        return submitNewFailedEventsLater();
      default:
        return dropEvents();
    }
  }
}
