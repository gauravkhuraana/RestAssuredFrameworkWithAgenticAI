package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment statistics response model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentStats {

    @JsonProperty("totalPayments")
    private Integer totalPayments;

    @JsonProperty("completedPayments")
    private Integer completedPayments;

    @JsonProperty("failedPayments")
    private Integer failedPayments;

    @JsonProperty("pendingPayments")
    private Integer pendingPayments;

    @JsonProperty("totalAmount")
    private Money totalAmount;

    @JsonProperty("averageAmount")
    private Money averageAmount;

    @JsonProperty("successRate")
    private BigDecimal successRate;

    @JsonProperty("period")
    private String period;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    /**
     * Get success rate as percentage string
     */
    public String getSuccessRatePercentage() {
        if (successRate == null) return "0%";
        return successRate.multiply(BigDecimal.valueOf(100)).setScale(2) + "%";
    }
}
