package com.api.automation.tests.generated.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Generated model class for Order
 * Generated from Swagger/OpenAPI specification
 */
@Data
public class Order {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    // TODO: Add more properties based on actual schema
    // Original schema details can be found in generated documentation
}
