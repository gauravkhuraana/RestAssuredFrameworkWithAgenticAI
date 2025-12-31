package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth2 Token response model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    /**
     * Get the full Authorization header value
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }

    /**
     * Check if token has refresh token
     */
    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isEmpty();
    }
}
