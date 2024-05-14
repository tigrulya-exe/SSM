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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * PageRequestDto
 */

@JsonTypeName("PageRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class PageRequestDto {

  private Integer limit = null;

  private Long offset = null;

  @Valid
  private List<String> sort;

  public PageRequestDto limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Maximum number of records in result set
   * minimum: 1
   * @return limit
  */
  @Min(1) 
  @Schema(name = "limit", description = "Maximum number of records in result set", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limit")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public PageRequestDto offset(Long offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Offset of the first record to search from
   * minimum: 0
   * @return offset
  */
  @Min(0L) 
  @Schema(name = "offset", description = "Offset of the first record to search from", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offset")
  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public PageRequestDto sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public PageRequestDto addSortItem(String sortItem) {
    if (this.sort == null) {
      this.sort = new ArrayList<>();
    }
    this.sort.add(sortItem);
    return this;
  }

  /**
   * Sort field names prefixed with '-' for descending order
   * @return sort
  */
  
  @Schema(name = "sort", description = "Sort field names prefixed with '-' for descending order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sort")
  public List<String> getSort() {
    return sort;
  }

  public void setSort(List<String> sort) {
    this.sort = sort;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageRequestDto pageRequest = (PageRequestDto) o;
    return Objects.equals(this.limit, pageRequest.limit) &&
        Objects.equals(this.offset, pageRequest.offset) &&
        Objects.equals(this.sort, pageRequest.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(limit, offset, sort);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageRequestDto {\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
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

