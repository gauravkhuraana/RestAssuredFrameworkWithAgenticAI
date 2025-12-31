package com.api.automation.models.billpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing payment statuses in the Bill Payment API
 */
public enum PaymentStatus {
    INITIATED("initiated"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
