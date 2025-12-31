package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination metadata for list responses in the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationMeta {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("hasMore")
    private Boolean hasMore;

    /**
     * Check if there are more pages available
     */
    public boolean hasNextPage() {
        return hasMore != null && hasMore;
    }

    /**
     * Calculate remaining items
     */
    public int getRemainingItems() {
        if (total == null || page == null || limit == null) {
            return 0;
        }
        int fetched = page * limit;
        return Math.max(0, total - fetched);
    }
}
