package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Generated;

/**
 * TimeIntervalDto
 */

@JsonTypeName("TimeInterval")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class TimeIntervalDto {

  private Long from = null;

  private Long to = null;

  public TimeIntervalDto from(Long from) {
    this.from = from;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval start
   * @return from
  */
  
  @Schema(name = "from", description = "UNIX timestamp (UTC) of the interval start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("from")
  public Long getFrom() {
    return from;
  }

  public void setFrom(Long from) {
    this.from = from;
  }

  public TimeIntervalDto to(Long to) {
    this.to = to;
    return this;
  }

  /**
   * UNIX timestamp (UTC) of the interval end
   * @return to
  */
  
  @Schema(name = "to", description = "UNIX timestamp (UTC) of the interval end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("to")
  public Long getTo() {
    return to;
  }

  public void setTo(Long to) {
    this.to = to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimeIntervalDto timeInterval = (TimeIntervalDto) o;
    return Objects.equals(this.from, timeInterval.from) &&
        Objects.equals(this.to, timeInterval.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimeIntervalDto {\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

