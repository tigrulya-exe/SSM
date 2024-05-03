package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * EventTimeIntervalDto
 */

@JsonTypeName("EventTimeInterval")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class EventTimeIntervalDto {

  private Long timestampFrom = null;

  private Long timestampTo = null;

  public EventTimeIntervalDto timestampFrom(Long timestampFrom) {
    this.timestampFrom = timestampFrom;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval start
   * minimum: 0
   * @return timestampFrom
  */
  @Min(0L) 
  @Schema(name = "timestampFrom", description = "UNIX timestamp (UTC) of the interval start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestampFrom")
  public Long getTimestampFrom() {
    return timestampFrom;
  }

  public void setTimestampFrom(Long timestampFrom) {
    this.timestampFrom = timestampFrom;
  }

  public EventTimeIntervalDto timestampTo(Long timestampTo) {
    this.timestampTo = timestampTo;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval end
   * minimum: 0
   * @return timestampTo
  */
  @Min(0L) 
  @Schema(name = "timestampTo", description = "UNIX timestamp (UTC) of the interval end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestampTo")
  public Long getTimestampTo() {
    return timestampTo;
  }

  public void setTimestampTo(Long timestampTo) {
    this.timestampTo = timestampTo;
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
    return Objects.equals(this.timestampFrom, eventTimeInterval.timestampFrom) &&
        Objects.equals(this.timestampTo, eventTimeInterval.timestampTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestampFrom, timestampTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventTimeIntervalDto {\n");
    sb.append("    timestampFrom: ").append(toIndentedString(timestampFrom)).append("\n");
    sb.append("    timestampTo: ").append(toIndentedString(timestampTo)).append("\n");
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

