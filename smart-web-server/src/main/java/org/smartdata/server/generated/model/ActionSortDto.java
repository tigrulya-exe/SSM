/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Generated;

/**
 * Sort field names prefixed with '-' for descending order
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ActionSortDto {
  
  ID("id"),
  
  EXECHOST("execHost"),
  
  CREATETIME("createTime"),
  
  FINISHTIME("finishTime"),
  
  STATUS("status"),
  
  SOURCE("source"),

  _ID("-id"),
  
  _EXECHOST("-execHost"),
  
  _CREATETIME("-createTime"),
  
  _FINISHTIME("-finishTime"),
  
  _STATUS("-status"),

  _SOURCE("-source");

  private String value;

  ActionSortDto(String value) {
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
  public static ActionSortDto fromValue(String value) {
    for (ActionSortDto b : ActionSortDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

