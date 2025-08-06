package com.api.automation.auth;

import com.api.automation.config.ConfigManager;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Handler for different authentication types
 */
public class AuthHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    public enum AuthType {
        BEARER, BASIC, API_KEY, OAUTH2, NONE
    }

    /**
     * Apply authentication to the request specification
     */
    public static RequestSpecification applyAuth(RequestSpecification requestSpec, AuthType authType) {
        switch (authType) {
            case BEARER:
                return applyBearerToken(requestSpec);
            case BASIC:
                return applyBasicAuth(requestSpec);
            case API_KEY:
                return applyApiKey(requestSpec);
            case OAUTH2:
                return applyOAuth2(requestSpec);
            case NONE:
            default:
                logger.debug("No authentication applied");
                return requestSpec;
        }
    }

    /**
     * Apply Bearer token authentication
     */
    public static RequestSpecification applyBearerToken(RequestSpecification requestSpec) {
        String token = config.getAuthToken();
        if (token != null && !token.isEmpty()) {
            logger.debug("Applying Bearer token authentication");
            return requestSpec.header("Authorization", "Bearer " + token);
        } else {
            logger.warn("Bearer token is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply Bearer token authentication with custom token
     */
    public static RequestSpecification applyBearerToken(RequestSpecification requestSpec, String token) {
        if (token != null && !token.isEmpty()) {
            logger.debug("Applying custom Bearer token authentication");
            return requestSpec.header("Authorization", "Bearer " + token);
        } else {
            logger.warn("Custom bearer token is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply Basic authentication
     */
    public static RequestSpecification applyBasicAuth(RequestSpecification requestSpec) {
        String username = config.getAuthUsername();
        String password = config.getAuthPassword();
        
        if (username != null && password != null) {
            logger.debug("Applying Basic authentication for user: {}", username);
            String credentials = username + ":" + password;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            return requestSpec.header("Authorization", "Basic " + encodedCredentials);
        } else {
            logger.warn("Username or password is null for Basic authentication");
            return requestSpec;
        }
    }

    /**
     * Apply Basic authentication with custom credentials
     */
    public static RequestSpecification applyBasicAuth(RequestSpecification requestSpec, String username, String password) {
        if (username != null && password != null) {
            logger.debug("Applying custom Basic authentication for user: {}", username);
            String credentials = username + ":" + password;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            return requestSpec.header("Authorization", "Basic " + encodedCredentials);
        } else {
            logger.warn("Custom username or password is null for Basic authentication");
            return requestSpec;
        }
    }

    /**
     * Apply API Key authentication
     */
    public static RequestSpecification applyApiKey(RequestSpecification requestSpec) {
        return applyApiKey(requestSpec, "X-API-Key");
    }

    /**
     * Apply API Key authentication with custom header name
     */
    public static RequestSpecification applyApiKey(RequestSpecification requestSpec, String headerName) {
        String apiKey = config.getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            logger.debug("Applying API Key authentication with header: {}", headerName);
            return requestSpec.header(headerName, apiKey);
        } else {
            logger.warn("API Key is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply API Key authentication with custom key and header
     */
    public static RequestSpecification applyApiKey(RequestSpecification requestSpec, String headerName, String apiKey) {
        if (apiKey != null && !apiKey.isEmpty()) {
            logger.debug("Applying custom API Key authentication with header: {}", headerName);
            return requestSpec.header(headerName, apiKey);
        } else {
            logger.warn("Custom API Key is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply OAuth2 authentication (placeholder for future implementation)
     */
    private static RequestSpecification applyOAuth2(RequestSpecification requestSpec) {
        logger.info("OAuth2 authentication not implemented yet");
        return requestSpec;
    }

    /**
     * Apply custom headers for authentication
     */
    public static RequestSpecification applyCustomHeaders(RequestSpecification requestSpec, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            logger.debug("Applying custom authentication headers: {}", headers.keySet());
            return requestSpec.headers(headers);
        }
        return requestSpec;
    }

    /**
     * Get authentication headers as a map
     */
    public static Map<String, String> getAuthHeaders(AuthType authType) {
        Map<String, String> headers = new HashMap<>();
        
        switch (authType) {
            case BEARER:
                String token = config.getAuthToken();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                break;
            case BASIC:
                String username = config.getAuthUsername();
                String password = config.getAuthPassword();
                if (username != null && password != null) {
                    String credentials = username + ":" + password;
                    String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
                    headers.put("Authorization", "Basic " + encodedCredentials);
                }
                break;
            case API_KEY:
                String apiKey = config.getApiKey();
                if (apiKey != null && !apiKey.isEmpty()) {
                    headers.put("X-API-Key", apiKey);
                }
                break;
            default:
                break;
        }
        
        return headers;
    }
}
