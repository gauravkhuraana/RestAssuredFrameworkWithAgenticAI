package com.api.automation.models.billpay;

import com.api.automation.models.billpay.enums.KycStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input model for creating a new user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInput {

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("password")
    private String password;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("kycDocumentType")
    private String kycDocumentType;

    @JsonProperty("kycDocumentNumber")
    private String kycDocumentNumber;

    /**
     * Create a simple user input with required fields
     */
    public static UserInput simple(String email, String firstName, String lastName, String phone) {
        return UserInput.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();
    }
}
