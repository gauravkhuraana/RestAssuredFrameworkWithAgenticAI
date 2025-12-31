package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Input model for creating a new bill
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillInput {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("billerId")
    private String billerId;

    @JsonProperty("customerIdentifier")
    private String customerIdentifier;

    @JsonProperty("consumerNumber")
    private String consumerNumber;

    @JsonProperty("consumerName")
    private String consumerName;

    @JsonProperty("billNumber")
    private String billNumber;

    @JsonProperty("billDate")
    private String billDate;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("billPeriod")
    private String billPeriod;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metadata")
    private Object metadata;

    /**
     * Create a simple bill input with required fields
     */
    public static BillInput simple(String userId, String billerId, String customerIdentifier, BigDecimal amount) {
        return BillInput.builder()
                .userId(userId)
                .billerId(billerId)
                .customerIdentifier(customerIdentifier)
                .amount(amount)
                .build();
    }
}
