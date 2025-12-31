package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input model for creating a new payment method
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodInput {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("type")
    private PaymentMethodType type;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("isDefault")
    private Boolean isDefault;

    // UPI specific fields
    @JsonProperty("upiId")
    private String upiId;

    // Card specific fields
    @JsonProperty("cardNumber")
    private String cardNumber;

    @JsonProperty("cardLastFour")
    private String cardLastFour;

    @JsonProperty("cardNetwork")
    private String cardNetwork;

    @JsonProperty("cardHolderName")
    private String cardHolderName;

    @JsonProperty("cardExpiryMonth")
    private Integer cardExpiryMonth;

    @JsonProperty("cardExpiryYear")
    private Integer cardExpiryYear;

    @JsonProperty("expiryMonth")
    private String expiryMonth;

    @JsonProperty("expiryYear")
    private String expiryYear;

    @JsonProperty("cvv")
    private String cvv;

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

    /**
     * Create UPI payment method input
     */
    public static PaymentMethodInput upi(String userId, String upiId, String name) {
        return PaymentMethodInput.builder()
                .userId(userId)
                .type(PaymentMethodType.UPI)
                .upiId(upiId)
                .name(name)
                .build();
    }

    /**
     * Create Credit Card payment method input
     */
    public static PaymentMethodInput creditCard(String userId, String cardNumber, String cardHolderName,
                                                 String expiryMonth, String expiryYear, String cvv) {
        return PaymentMethodInput.builder()
                .userId(userId)
                .type(PaymentMethodType.CREDIT_CARD)
                .cardNumber(cardNumber)
                .cardHolderName(cardHolderName)
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cvv(cvv)
                .build();
    }

    /**
     * Create Net Banking payment method input
     */
    public static PaymentMethodInput netBanking(String userId, String bankName, String accountNumber, String ifscCode) {
        return PaymentMethodInput.builder()
                .userId(userId)
                .type(PaymentMethodType.NET_BANKING)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .ifscCode(ifscCode)
                .build();
    }
}
