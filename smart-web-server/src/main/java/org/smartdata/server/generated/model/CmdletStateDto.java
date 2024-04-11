package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Current state of the cmdlet
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum CmdletStateDto {
  
  NOT_INITED("NOT_INITED"),
  
  PENDING("PENDING"),
  
  SCHEDULED("SCHEDULED"),
  
  DISPATCHED("DISPATCHED"),
  
  EXECUTING("EXECUTING"),
  
  CANCELLED("CANCELLED"),
  
  DISABLED("DISABLED"),
  
  FAILED("FAILED"),
  
  DONE("DONE");

  private String value;

  CmdletStateDto(String value) {
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
  public static CmdletStateDto fromValue(String value) {
    for (CmdletStateDto b : CmdletStateDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

