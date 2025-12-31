package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Money model representing currency amounts in the Bill Payment API
 * Used for bill amounts, payment amounts, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Money {

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("currency")
    @Builder.Default
    private String currency = "INR";

    /**
     * Convenience constructor for quick money creation
     */
    public Money(BigDecimal value) {
        this.value = value;
        this.currency = "INR";
    }

    /**
     * Create Money from double value
     */
    public static Money of(double value) {
        return Money.builder()
                .value(BigDecimal.valueOf(value))
                .currency("INR")
                .build();
    }

    /**
     * Create Money from double value with currency
     */
    public static Money of(double value, String currency) {
        return Money.builder()
                .value(BigDecimal.valueOf(value))
                .currency(currency)
                .build();
    }
}
