package com.api.automation.models.billpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing bill statuses in the Bill Payment API
 */
public enum BillStatus {
    PENDING("pending"),
    PAID("paid"),
    OVERDUE("overdue"),
    PARTIALLY_PAID("partially_paid"),
    CANCELLED("cancelled");

    private final String value;

    BillStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BillStatus fromValue(String value) {
        for (BillStatus status : BillStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown bill status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
