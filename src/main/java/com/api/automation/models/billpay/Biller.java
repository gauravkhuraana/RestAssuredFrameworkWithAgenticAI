package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.BillerCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Biller entity model representing service providers in the Bill Payment API
 * Examples: Electricity board, Telecom provider, Water supply, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Biller {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private BillerCategory category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("logoUrl")
    private String logoUrl;

    @JsonProperty("websiteUrl")
    private String websiteUrl;

    @JsonProperty("supportEmail")
    private String supportEmail;

    @JsonProperty("supportPhone")
    private String supportPhone;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("active")
    @Builder.Default
    private Boolean active = true;

    @JsonProperty("billerCode")
    private String billerCode;

    @JsonProperty("acceptedPaymentMethods")
    private List<String> acceptedPaymentMethods;

    @JsonProperty("minimumPayment")
    private Money minimumPayment;

    @JsonProperty("maximumPayment")
    private Money maximumPayment;

    @JsonProperty("processingTime")
    private String processingTime;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String updatedAt;
}
