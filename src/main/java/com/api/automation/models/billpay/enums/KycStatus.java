package com.api.automation.models.billpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing KYC statuses in the Bill Payment API
 */
public enum KycStatus {
    PENDING("pending"),
    VERIFIED("verified"),
    REJECTED("rejected");

    private final String value;

    KycStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static KycStatus fromValue(String value) {
        for (KycStatus status : KycStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown KYC status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
