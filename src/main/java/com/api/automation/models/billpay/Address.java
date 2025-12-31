package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Address model for billing and user addresses in the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    @JsonProperty("line1")
    private String line1;

    @JsonProperty("line2")
    private String line2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("country")
    @Builder.Default
    private String country = "India";

    /**
     * Create a simple address with essential fields
     */
    public static Address simple(String line1, String city, String state, String postalCode) {
        return Address.builder()
                .line1(line1)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country("India")
                .build();
    }
}
