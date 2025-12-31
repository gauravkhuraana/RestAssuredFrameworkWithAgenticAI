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
 * Supports: Bearer, Basic, API Key (Header/Query), Cookie Session, OAuth2
 */
public class AuthHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    public enum AuthType {
        BEARER,
        BASIC,
        API_KEY,
        API_KEY_QUERY,
        COOKIE_SESSION,
        OAUTH2,
        NONE
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
            case API_KEY_QUERY:
                return applyApiKeyAsQueryParam(requestSpec);
            case COOKIE_SESSION:
                return applyCookieSession(requestSpec);
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
     * Apply API Key as query parameter
     */
    public static RequestSpecification applyApiKeyAsQueryParam(RequestSpecification requestSpec) {
        String apiKey = config.getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            logger.debug("Applying API Key as query parameter");
            return requestSpec.queryParam("api_key", apiKey);
        } else {
            logger.warn("API Key is null or empty for query param authentication");
            return requestSpec;
        }
    }

    /**
     * Apply API Key as query parameter with custom key
     */
    public static RequestSpecification applyApiKeyAsQueryParam(RequestSpecification requestSpec, String apiKey) {
        if (apiKey != null && !apiKey.isEmpty()) {
            logger.debug("Applying custom API Key as query parameter");
            return requestSpec.queryParam("api_key", apiKey);
        } else {
            logger.warn("Custom API Key is null or empty for query param authentication");
            return requestSpec;
        }
    }

    /**
     * Apply Cookie Session authentication
     */
    public static RequestSpecification applyCookieSession(RequestSpecification requestSpec) {
        String sessionId = config.getSessionId();
        if (sessionId != null && !sessionId.isEmpty()) {
            logger.debug("Applying Cookie Session authentication");
            return requestSpec.cookie("session_id", sessionId);
        } else {
            logger.warn("Session ID is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply Cookie Session authentication with custom session ID
     */
    public static RequestSpecification applyCookieSession(RequestSpecification requestSpec, String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            logger.debug("Applying custom Cookie Session authentication");
            return requestSpec.cookie("session_id", sessionId);
        } else {
            logger.warn("Custom Session ID is null or empty");
            return requestSpec;
        }
    }

    /**
     * Apply OAuth2 authentication using client credentials flow
     * This retrieves a token using client_id and client_secret
     */
    private static RequestSpecification applyOAuth2(RequestSpecification requestSpec) {
        String clientId = config.getOAuth2ClientId();
        String clientSecret = config.getOAuth2ClientSecret();
        
        if (clientId != null && clientSecret != null && !clientId.isEmpty() && !clientSecret.isEmpty()) {
            logger.debug("Applying OAuth2 client credentials authentication");
            // For OAuth2, we would typically get a token first, then use it
            // For the demo API, we can use the demo token directly
            String token = config.getAuthToken();
            if (token != null && !token.isEmpty()) {
                return requestSpec.header("Authorization", "Bearer " + token);
            }
        }
        logger.warn("OAuth2 credentials are not properly configured");
        return requestSpec;
    }

    /**
     * Apply OAuth2 with custom client credentials
     * This is a simplified version - in production, you'd call the token endpoint
     */
    public static RequestSpecification applyOAuth2(RequestSpecification requestSpec, String clientId, String clientSecret) {
        if (clientId != null && clientSecret != null && !clientId.isEmpty() && !clientSecret.isEmpty()) {
            logger.debug("Applying custom OAuth2 client credentials for client: {}", clientId);
            // In a real implementation, you would:
            // 1. Call the token endpoint with client_id and client_secret
            // 2. Parse the token response
            // 3. Apply the access_token as Bearer token
            // For demo purposes, we use the demo token
            return requestSpec.header("Authorization", "Bearer demo-jwt-token-456");
        }
        logger.warn("Custom OAuth2 credentials are null or empty");
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
            case COOKIE_SESSION:
                String sessionId = config.getSessionId();
                if (sessionId != null && !sessionId.isEmpty()) {
                    headers.put("Cookie", "session_id=" + sessionId);
                }
                break;
            case OAUTH2:
                String oauthToken = config.getAuthToken();
                if (oauthToken != null && !oauthToken.isEmpty()) {
                    headers.put("Authorization", "Bearer " + oauthToken);
                }
                break;
            default:
                break;
        }
        
        return headers;
    }
}
