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
 * ActionsDto
 */

@JsonTypeName("Actions")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ActionsDto {

  private Long total;

  @Valid
  private List<@Valid ActionInfoDto> items;

  public ActionsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ActionsDto(Long total) {
    this.total = total;
  }

  public ActionsDto total(Long total) {
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

  public ActionsDto items(List<@Valid ActionInfoDto> items) {
    this.items = items;
    return this;
  }

  public ActionsDto addItemsItem(ActionInfoDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of actions
   * @return items
  */
  @Valid 
  @Schema(name = "items", description = "List of actions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid ActionInfoDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid ActionInfoDto> items) {
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
    ActionsDto actions = (ActionsDto) o;
    return Objects.equals(this.total, actions.total) &&
        Objects.equals(this.items, actions.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ActionsDto {\n");
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

