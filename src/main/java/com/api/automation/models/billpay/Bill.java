package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.BillStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bill entity model representing bills/invoices in the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bill {

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("billerId")
    private String billerId;

    @JsonProperty("billerName")
    private String billerName;

    @JsonProperty("consumerNumber")
    private String consumerNumber;

    @JsonProperty("consumerName")
    private String consumerName;

    @JsonProperty("billNumber")
    private String billNumber;

    @JsonProperty("billDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String billDate;

    @JsonProperty("dueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dueDate;

    @JsonProperty("amount")
    private Money amount;

    @JsonProperty("amountPaid")
    private Money amountPaid;

    @JsonProperty("amountDue")
    private Money amountDue;

    @JsonProperty("lateFee")
    private Money lateFee;

    @JsonProperty("status")
    private BillStatus status;

    @JsonProperty("billPeriod")
    private String billPeriod;

    @JsonProperty("description")
    private String description;

    @JsonProperty("pdfUrl")
    private String pdfUrl;

    @JsonProperty("metadata")
    private Object metadata;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String updatedAt;

    /**
     * Check if bill is overdue
     */
    public boolean isOverdue() {
        return status == BillStatus.OVERDUE;
    }

    /**
     * Check if bill is paid
     */
    public boolean isPaid() {
        return status == BillStatus.PAID;
    }

    /**
     * Check if bill is pending payment
     */
    public boolean isPending() {
        return status == BillStatus.PENDING;
    }
}
