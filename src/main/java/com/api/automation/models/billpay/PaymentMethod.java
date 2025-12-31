package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentMethod entity model representing saved payment methods in the Bill Payment API
 * Examples: UPI ID, Credit Card, Debit Card, Net Banking, Wallet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethod {

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("type")
    private PaymentMethodType type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("isDefault")
    @Builder.Default
    private Boolean isDefault = false;

    @JsonProperty("isVerified")
    @Builder.Default
    private Boolean isVerified = false;

    // UPI specific fields
    @JsonProperty("upiId")
    private String upiId;

    // Card specific fields
    @JsonProperty("cardNumber")
    private String cardNumber;

    @JsonProperty("cardHolderName")
    private String cardHolderName;

    @JsonProperty("expiryMonth")
    private String expiryMonth;

    @JsonProperty("expiryYear")
    private String expiryYear;

    @JsonProperty("cardBrand")
    private String cardBrand;

    @JsonProperty("last4")
    private String last4;

    // Bank specific fields
    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("ifscCode")
    private String ifscCode;

    // Wallet specific fields
    @JsonProperty("walletProvider")
    private String walletProvider;

    @JsonProperty("walletId")
    private String walletId;

    @JsonProperty("active")
    @Builder.Default
    private Boolean active = true;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String updatedAt;

    /**
     * Check if this is the default payment method
     */
    public boolean isDefaultMethod() {
        return isDefault != null && isDefault;
    }

    /**
     * Get masked card number (last 4 digits)
     */
    public String getMaskedCardNumber() {
        if (last4 != null) {
            return "**** **** **** " + last4;
        }
        return null;
    }
}
