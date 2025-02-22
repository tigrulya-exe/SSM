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
package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.Min;

import java.util.Objects;

/**
 * EventTimeIntervalDto
 */

@JsonTypeName("EventTimeInterval")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class EventTimeIntervalDto {

  private Long eventTimeFrom = null;

  private Long eventTimeTo = null;

  public EventTimeIntervalDto eventTimeFrom(Long eventTimeFrom) {
    this.eventTimeFrom = eventTimeFrom;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval start
   * minimum: 0
   * @return eventTimeFrom
   */
  @Min(0L)
  @Schema(name = "eventTimeFrom", description = "UNIX timestamp (UTC) of the interval start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eventTimeFrom")
  public Long getEventTimeFrom() {
    return eventTimeFrom;
  }

  public void setEventTimeFrom(Long eventTimeFrom) {
    this.eventTimeFrom = eventTimeFrom;
  }

  public EventTimeIntervalDto eventTimeTo(Long eventTimeTo) {
    this.eventTimeTo = eventTimeTo;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval end
   * minimum: 0
   * @return eventTimeTo
   */
  @Min(0L)
  @Schema(name = "eventTimeTo", description = "UNIX timestamp (UTC) of the interval end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eventTimeTo")
  public Long getEventTimeTo() {
    return eventTimeTo;
  }

  public void setEventTimeTo(Long eventTimeTo) {
    this.eventTimeTo = eventTimeTo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventTimeIntervalDto eventTimeInterval = (EventTimeIntervalDto) o;
    return Objects.equals(this.eventTimeFrom, eventTimeInterval.eventTimeFrom) &&
        Objects.equals(this.eventTimeTo, eventTimeInterval.eventTimeTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventTimeFrom, eventTimeTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventTimeIntervalDto {\n");
    sb.append("    eventTimeFrom: ").append(toIndentedString(eventTimeFrom)).append("\n");
    sb.append("    eventTimeTo: ").append(toIndentedString(eventTimeTo)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

