package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Type of the audit object
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum AuditObjectTypeDto {
  
  RULE("RULE"),
  
  CMDLET("CMDLET");

  private String value;

  AuditObjectTypeDto(String value) {
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
  public static AuditObjectTypeDto fromValue(String value) {
    for (AuditObjectTypeDto b : AuditObjectTypeDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

