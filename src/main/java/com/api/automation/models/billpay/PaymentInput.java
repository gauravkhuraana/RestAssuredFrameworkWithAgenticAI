package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Input model for creating/processing a new payment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInput {

    @JsonProperty("billId")
    private String billId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("paymentMethodId")
    private String paymentMethodId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("savePaymentMethod")
    private Boolean savePaymentMethod;

    @JsonProperty("metadata")
    private Object metadata;

    /**
     * Create a simple payment input with required fields
     */
    public static PaymentInput simple(String billId, String userId, String paymentMethodId, BigDecimal amount) {
        return PaymentInput.builder()
                .billId(billId)
                .userId(userId)
                .paymentMethodId(paymentMethodId)
                .amount(amount)
                .build();
    }
}
