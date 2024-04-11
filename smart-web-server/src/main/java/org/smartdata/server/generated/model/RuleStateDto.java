package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Current state of the rule
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum RuleStateDto {
  
  ACTIVE("ACTIVE"),
  
  DISABLED("DISABLED");

  private String value;

  RuleStateDto(String value) {
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
  public static RuleStateDto fromValue(String value) {
    for (RuleStateDto b : RuleStateDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

