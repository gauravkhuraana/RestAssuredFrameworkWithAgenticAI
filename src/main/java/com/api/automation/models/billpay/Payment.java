package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment entity model representing payment transactions in the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {

    @JsonProperty("id")
    private String id;

    @JsonProperty("billId")
    private String billId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("paymentMethodId")
    private String paymentMethodId;

    @JsonProperty("amount")
    private Money amount;

    @JsonProperty("convenienceFee")
    private Money convenienceFee;

    @JsonProperty("totalAmount")
    private Money totalAmount;

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonProperty("gatewayResponse")
    private Object gatewayResponse;

    @JsonProperty("failureReason")
    private String failureReason;

    @JsonProperty("refundId")
    private String refundId;

    @JsonProperty("refundAmount")
    private Money refundAmount;

    @JsonProperty("refundReason")
    private String refundReason;

    @JsonProperty("paymentDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String paymentDate;

    @JsonProperty("completedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String completedAt;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String updatedAt;

    /**
     * Check if payment is completed
     */
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Check if payment failed
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    /**
     * Check if payment is processing
     */
    public boolean isProcessing() {
        return status == PaymentStatus.PROCESSING;
    }

    /**
     * Check if payment is refunded
     */
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }
}
