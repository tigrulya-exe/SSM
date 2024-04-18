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
package org.smartdata.model;

import java.time.Instant;

public class TimeInterval {
  private final Instant from;
  private final Instant to;

  public TimeInterval(Instant from, Instant to) {
    this.from = from;
    this.to = to;
  }

  public Instant getFrom() {
    return from;
  }

  public Instant getTo() {
    return to;
  }

  public long getFromMillis() {
    return from.toEpochMilli();
  }

  public long getToMillis() {
    return to.toEpochMilli();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Instant from;
    private Instant to;

    public Builder from(Instant from) {
      this.from = from;
      return this;
    }

    public Builder to(Instant to) {
      this.to = to;
      return this;
    }

    public Builder fromMillis(long fromMillis) {
      this.from = Instant.ofEpochMilli(fromMillis);
      return this;
    }

    public Builder toMillis(long toMillis) {
      this.to = Instant.ofEpochMilli(toMillis);
      return this;
    }

    public TimeInterval build() {
      return new TimeInterval(from, to);
    }
  }
}
