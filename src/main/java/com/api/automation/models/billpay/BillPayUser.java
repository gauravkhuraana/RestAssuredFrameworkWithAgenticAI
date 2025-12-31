package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.KycStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User entity model for the Bill Payment API
 * Represents users who can pay bills, manage payment methods, etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillPayUser {

    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("dateOfBirth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("kycStatus")
    private KycStatus kycStatus;

    @JsonProperty("kycDocumentType")
    private String kycDocumentType;

    @JsonProperty("kycDocumentNumber")
    private String kycDocumentNumber;

    @JsonProperty("profilePictureUrl")
    private String profilePictureUrl;

    @JsonProperty("active")
    @Builder.Default
    private Boolean active = true;

    @JsonProperty("emailVerified")
    @Builder.Default
    private Boolean emailVerified = false;

    @JsonProperty("phoneVerified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @JsonProperty("preferredPaymentMethodId")
    private String preferredPaymentMethodId;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String updatedAt;

    /**
     * Get full name
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName);
        }
        if (lastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString();
    }

    /**
     * Check if KYC is verified
     */
    public boolean isKycVerified() {
        return kycStatus == KycStatus.VERIFIED;
    }

    /**
     * Check if user is fully verified (email + phone + KYC)
     */
    public boolean isFullyVerified() {
        return Boolean.TRUE.equals(emailVerified) 
                && Boolean.TRUE.equals(phoneVerified) 
                && isKycVerified();
    }
}
