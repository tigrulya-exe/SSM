package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Result of the audit event
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum AuditEventResultDto {
  
  SUCCESS("SUCCESS"),
  
  FAILURE("FAILURE");

  private String value;

  AuditEventResultDto(String value) {
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
  public static AuditEventResultDto fromValue(String value) {
    for (AuditEventResultDto b : AuditEventResultDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

