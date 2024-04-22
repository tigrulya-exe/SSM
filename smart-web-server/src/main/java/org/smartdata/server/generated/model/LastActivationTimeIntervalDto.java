package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * LastActivationTimeIntervalDto
 */

@JsonTypeName("LastActivationTimeInterval")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class LastActivationTimeIntervalDto {

  private Long lastActivationTimeFrom = null;

  private Long lastActivationTimeTo = null;

  public LastActivationTimeIntervalDto lastActivationTimeFrom(Long lastActivationTimeFrom) {
    this.lastActivationTimeFrom = lastActivationTimeFrom;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval start
   * minimum: 0
   * @return lastActivationTimeFrom
  */
  @Min(0L) 
  @Schema(name = "lastActivationTimeFrom", description = "UNIX timestamp (UTC) of the interval start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastActivationTimeFrom")
  public Long getLastActivationTimeFrom() {
    return lastActivationTimeFrom;
  }

  public void setLastActivationTimeFrom(Long lastActivationTimeFrom) {
    this.lastActivationTimeFrom = lastActivationTimeFrom;
  }

  public LastActivationTimeIntervalDto lastActivationTimeTo(Long lastActivationTimeTo) {
    this.lastActivationTimeTo = lastActivationTimeTo;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval end
   * minimum: 0
   * @return lastActivationTimeTo
  */
  @Min(0L) 
  @Schema(name = "lastActivationTimeTo", description = "UNIX timestamp (UTC) of the interval end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastActivationTimeTo")
  public Long getLastActivationTimeTo() {
    return lastActivationTimeTo;
  }

  public void setLastActivationTimeTo(Long lastActivationTimeTo) {
    this.lastActivationTimeTo = lastActivationTimeTo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LastActivationTimeIntervalDto lastActivationTimeInterval = (LastActivationTimeIntervalDto) o;
    return Objects.equals(this.lastActivationTimeFrom, lastActivationTimeInterval.lastActivationTimeFrom) &&
        Objects.equals(this.lastActivationTimeTo, lastActivationTimeInterval.lastActivationTimeTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastActivationTimeFrom, lastActivationTimeTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LastActivationTimeIntervalDto {\n");
    sb.append("    lastActivationTimeFrom: ").append(toIndentedString(lastActivationTimeFrom)).append("\n");
    sb.append("    lastActivationTimeTo: ").append(toIndentedString(lastActivationTimeTo)).append("\n");
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

