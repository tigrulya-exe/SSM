package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Operation of the audit event
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum AuditOperationDto {
  
  CREATE("CREATE"),
  
  DELETE("DELETE"),
  
  START("START"),
  
  STOP("STOP");

  private String value;

  AuditOperationDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AuditOperationDto fromValue(String value) {
    for (AuditOperationDto b : AuditOperationDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

