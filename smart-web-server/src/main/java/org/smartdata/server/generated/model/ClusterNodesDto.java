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
 * ClusterNodesDto
 */

@JsonTypeName("ClusterNodes")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ClusterNodesDto {

  private Long total;

  @Valid
  private List<@Valid ClusterNodeDto> items;

  public ClusterNodesDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClusterNodesDto(Long total) {
    this.total = total;
  }

  public ClusterNodesDto total(Long total) {
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

  public ClusterNodesDto items(List<@Valid ClusterNodeDto> items) {
    this.items = items;
    return this;
  }

  public ClusterNodesDto addItemsItem(ClusterNodeDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * List of SSM cluster nodes
   * @return items
  */
  @Valid 
  @Schema(name = "items", description = "List of SSM cluster nodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid ClusterNodeDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid ClusterNodeDto> items) {
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
    ClusterNodesDto clusterNodes = (ClusterNodesDto) o;
    return Objects.equals(this.total, clusterNodes.total) &&
        Objects.equals(this.items, clusterNodes.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClusterNodesDto {\n");
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

