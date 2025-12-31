package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.ApiResponse;
import com.api.automation.models.billpay.BillPayUser;
import com.api.automation.models.billpay.TokenResponse;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auth Service for Bill Payment API authentication endpoints
 */
public class AuthService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private static final String AUTH_TOKEN_ENDPOINT = "/oauth/token";
    private static final String AUTH_ME_ENDPOINT = "/v1/auth/me";

    /**
     * Get OAuth2 access token using client credentials
     * POST /v1/auth/token
     */
    public Response getToken(String clientId, String clientSecret) {
        logger.info("Getting OAuth2 token for client: {}", clientId);
        return withContentType(io.restassured.http.ContentType.URLENC)
                .withFormParam("grant_type", "client_credentials")
                .withFormParam("client_id", clientId)
                .withFormParam("client_secret", clientSecret)
                .post(AUTH_TOKEN_ENDPOINT);
    }

    /**
     * Get OAuth2 token and return as object
     */
    public TokenResponse getTokenAsObject(String clientId, String clientSecret) {
        Response response = getToken(clientId, clientSecret);
        response.then().statusCode(200);
        return JsonUtils.jsonToObject(response.getBody().asString(), TokenResponse.class);
    }

    /**
     * Get current authenticated user info
     * GET /v1/auth/me
     * Requires authentication
     */
    public Response getCurrentUser() {
        logger.info("Getting current user info");
        return withAuth(AuthHandler.AuthType.BEARER)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user with API Key authentication
     */
    public Response getCurrentUserWithApiKey() {
        logger.info("Getting current user info with API Key");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user with Basic authentication
     */
    public Response getCurrentUserWithBasicAuth() {
        logger.info("Getting current user info with Basic Auth");
        return withAuth(AuthHandler.AuthType.BASIC)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user with Cookie session authentication
     */
    public Response getCurrentUserWithCookieSession() {
        logger.info("Getting current user info with Cookie Session");
        return withAuth(AuthHandler.AuthType.COOKIE_SESSION)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user info as object
     */
    public BillPayUser getCurrentUserAsObject() {
        Response response = getCurrentUser();
        response.then().statusCode(200);
        ApiResponse<BillPayUser> apiResponse = ApiResponse.fromJson(response.getBody().asString(), BillPayUser.class);
        return apiResponse.getData();
    }

    /**
     * Verify that authentication is working
     */
    public boolean isAuthenticated() {
        try {
            Response response = getCurrentUser();
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            logger.error("Authentication check failed", e);
            return false;
        }
    }

    /**
     * Get OAuth2 token using client credentials
     */
    public Response getOAuth2Token(String clientId, String clientSecret) {
        logger.info("Getting OAuth2 token for client: {}", clientId);
        return withContentType(io.restassured.http.ContentType.URLENC)
                .withFormParam("grant_type", "client_credentials")
                .withFormParam("client_id", clientId)
                .withFormParam("client_secret", clientSecret)
                .post(AUTH_TOKEN_ENDPOINT);
    }

    /**
     * Get current user with specified auth type
     */
    public Response getCurrentUser(AuthHandler.AuthType authType) {
        logger.info("Getting current user info with auth type: {}", authType);
        return withAuth(authType)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user with custom API key
     */
    public Response getCurrentUserWithApiKey(String apiKey) {
        logger.info("Getting current user info with custom API Key");
        return withHeader("X-API-Key", apiKey)
                .get(AUTH_ME_ENDPOINT);
    }

    /**
     * Get current user with custom Bearer token
     */
    public Response getCurrentUserWithBearerToken(String token) {
        logger.info("Getting current user info with custom Bearer token");
        return withHeader("Authorization", "Bearer " + token)
                .get(AUTH_ME_ENDPOINT);
    }
}
