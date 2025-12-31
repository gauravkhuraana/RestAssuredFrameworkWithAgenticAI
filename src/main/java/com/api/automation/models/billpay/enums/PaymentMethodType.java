package com.api.automation.models.billpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing payment method types in the Bill Payment API
 */
public enum PaymentMethodType {
    UPI("upi"),
    CREDIT_CARD("credit_card"),
    DEBIT_CARD("debit_card"),
    NET_BANKING("net_banking"),
    WALLET("wallet");

    private final String value;

    PaymentMethodType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentMethodType fromValue(String value) {
        for (PaymentMethodType type : PaymentMethodType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payment method type: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
