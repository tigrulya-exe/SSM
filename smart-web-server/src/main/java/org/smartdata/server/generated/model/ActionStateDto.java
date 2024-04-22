package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * State of the action
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ActionStateDto {
  
  RUNNING("RUNNING"),
  
  SUCCESSFUL("SUCCESSFUL"),
  
  FAILED("FAILED");

  private String value;

  ActionStateDto(String value) {
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
  public static ActionStateDto fromValue(String value) {
    for (ActionStateDto b : ActionStateDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

