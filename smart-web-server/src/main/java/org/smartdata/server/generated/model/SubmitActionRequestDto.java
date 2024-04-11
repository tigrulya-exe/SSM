package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/**
 * SubmitActionRequestDto
 */

@JsonTypeName("SubmitActionRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class SubmitActionRequestDto {

  private String action;

  public SubmitActionRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SubmitActionRequestDto(String action) {
    this.action = action;
  }

  public SubmitActionRequestDto action(String action) {
    this.action = action;
    return this;
  }

  /**
   * Action text representation
   * @return action
  */
  @NotNull 
  @Schema(name = "action", description = "Action text representation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("action")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmitActionRequestDto submitActionRequest = (SubmitActionRequestDto) o;
    return Objects.equals(this.action, submitActionRequest.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(action);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmitActionRequestDto {\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
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

