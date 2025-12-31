package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bills summary response model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillsSummary {

    @JsonProperty("totalBills")
    private Integer totalBills;

    @JsonProperty("pendingBills")
    private Integer pendingBills;

    @JsonProperty("paidBills")
    private Integer paidBills;

    @JsonProperty("overdueBills")
    private Integer overdueBills;

    @JsonProperty("totalAmountDue")
    private Money totalAmountDue;

    @JsonProperty("totalAmountPaid")
    private Money totalAmountPaid;

    @JsonProperty("totalOverdueAmount")
    private Money totalOverdueAmount;

    @JsonProperty("upcomingDueDate")
    private String upcomingDueDate;

    /**
     * Check if there are any overdue bills
     */
    public boolean hasOverdueBills() {
        return overdueBills != null && overdueBills > 0;
    }

    /**
     * Check if there are pending bills
     */
    public boolean hasPendingBills() {
        return pendingBills != null && pendingBills > 0;
    }
}
