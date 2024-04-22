package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Source of the action creation
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ActionSourceDto {
  
  RULE("RULE"),
  
  USER("USER");

  private String value;

  ActionSourceDto(String value) {
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
  public static ActionSourceDto fromValue(String value) {
    for (ActionSourceDto b : ActionSourceDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

