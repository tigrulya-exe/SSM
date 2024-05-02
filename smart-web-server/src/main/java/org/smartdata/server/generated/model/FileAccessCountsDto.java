package org.smartdata.server.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * FileAccessCountsDto
 */

@JsonTypeName("FileAccessCounts")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class FileAccessCountsDto {

  private Long total;

  @Valid
  private List<@Valid FileAccessInfoDto> items;

  public FileAccessCountsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FileAccessCountsDto(Long total) {
    this.total = total;
  }

  public FileAccessCountsDto total(Long total) {
    this.total = total;
    return this;
  }

  /**
   * Total number of objects
   * @return total
  */
  @NotNull 
  @Schema(name = "total", description = "Total number of objects", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total")
  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public FileAccessCountsDto items(List<@Valid FileAccessInfoDto> items) {
    this.items = items;
    return this;
  }

  public FileAccessCountsDto addItemsItem(FileAccessInfoDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of files
   * @return items
  */
  @Valid 
  @Schema(name = "items", description = "List of files", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid FileAccessInfoDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid FileAccessInfoDto> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileAccessCountsDto fileAccessCounts = (FileAccessCountsDto) o;
    return Objects.equals(this.total, fileAccessCounts.total) &&
        Objects.equals(this.items, fileAccessCounts.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FileAccessCountsDto {\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

