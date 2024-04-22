package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Type of the cmdlet executor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ExecutorTypeDto {
  
  LOCAL("LOCAL"),
  
  REMOTE_SSM("REMOTE_SSM"),
  
  AGENT("AGENT");

  private String value;

  ExecutorTypeDto(String value) {
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
  public static ExecutorTypeDto fromValue(String value) {
    for (ExecutorTypeDto b : ExecutorTypeDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

