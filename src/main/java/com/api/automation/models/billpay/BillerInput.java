package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.BillerCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Input model for creating a new biller
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillerInput {

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

    /**
     * Create a simple biller input with required fields
     */
    public static BillerInput simple(String name, BillerCategory category) {
        return BillerInput.builder()
                .name(name)
                .category(category)
                .active(true)
                .build();
    }
}
