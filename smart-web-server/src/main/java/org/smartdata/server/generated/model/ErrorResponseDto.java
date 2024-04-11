package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/**
 * ErrorResponseDto
 */

@JsonTypeName("ErrorResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ErrorResponseDto {

  private String code;

  private String message;

  private Object body;

  public ErrorResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ErrorResponseDto(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public ErrorResponseDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Error code
   * @return code
  */
  @NotNull 
  @Schema(name = "code", description = "Error code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ErrorResponseDto message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Detailed error message
   * @return message
  */
  @NotNull 
  @Schema(name = "message", description = "Detailed error message", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ErrorResponseDto body(Object body) {
    this.body = body;
    return this;
  }

  /**
   * Additional information about the error
   * @return body
  */
  
  @Schema(name = "body", description = "Additional information about the error", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("body")
  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponseDto errorResponse = (ErrorResponseDto) o;
    return Objects.equals(this.code, errorResponse.code) &&
        Objects.equals(this.message, errorResponse.message) &&
        Objects.equals(this.body, errorResponse.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, body);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponseDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    body: ").append(toIndentedString(body)).append("\n");
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

