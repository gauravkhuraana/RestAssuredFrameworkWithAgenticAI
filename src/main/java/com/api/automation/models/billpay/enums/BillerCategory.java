package com.api.automation.models.billpay.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing biller categories in the Bill Payment API
 */
public enum BillerCategory {
    TELECOM("telecom"),
    ELECTRICITY("electricity"),
    WATER("water"),
    GAS("gas"),
    BROADBAND("broadband"),
    DTH("dth"),
    INSURANCE("insurance"),
    CREDIT_CARD("credit_card"),
    LOAN("loan"),
    MUNICIPAL_TAX("municipal_tax");

    private final String value;

    BillerCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BillerCategory fromValue(String value) {
        for (BillerCategory category : BillerCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown biller category: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
