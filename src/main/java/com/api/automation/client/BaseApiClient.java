package com.api.automation.client;

import com.api.automation.auth.AuthHandler;
import com.api.automation.config.ConfigManager;
import com.api.automation.config.RestAssuredConfig;
import com.api.automation.retry.RetryHandler;
import com.api.automation.utils.JsonUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Thread-Safe Base API client with common HTTP operations
 * Each instance maintains its own isolated RequestSpecification
 */
public class BaseApiClient {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected static final ConfigManager config;
    
    static {
        try {
            config = ConfigManager.getInstance();
            logger.debug("ConfigManager initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ConfigManager", e);
            throw new RuntimeException("Failed to initialize ConfigManager", e);
        }
    }
    
    // Use ThreadLocal to ensure complete isolation between parallel tests
    private final RequestSpecBuilder specBuilder;
    protected RequestSpecification requestSpec;

    public BaseApiClient() {
        try {
            // Create a completely new request specification builder for isolation
            this.specBuilder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "API-Automation-Framework/1.0")
                .log(LogDetail.ALL);
            
            // Set base URI directly in the specification
            if (config != null) {
                String baseUrl = config.getBaseUrl();
                if (baseUrl != null && !baseUrl.isEmpty()) {
                    this.specBuilder.setBaseUri(baseUrl);
                    logger.debug("BaseApiClient initialized with base URI: {}", baseUrl);
                } else {
                    logger.warn("Base URL is null or empty");
                }
            } else {
                logger.error("ConfigManager is null");
            }
            
            // Build the request spec
            this.requestSpec = this.specBuilder.build();
        } catch (Exception e) {
            logger.error("Error initializing BaseApiClient: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize BaseApiClient", e);
        }
    }

    public BaseApiClient(RequestSpecification requestSpec) {
        this.specBuilder = null;
        this.requestSpec = requestSpec;
    }

    /**
     * Create an authenticated API client with fresh isolated spec
     */
    public static BaseApiClient withAuthentication() {
        BaseApiClient client = new BaseApiClient();
        return client.withAuth(AuthHandler.AuthType.BEARER);
    }

    /**
     * Set authentication for the request
     */
    public BaseApiClient withAuth(AuthHandler.AuthType authType) {
        this.requestSpec = AuthHandler.applyAuth(this.requestSpec, authType);
        return this;
    }

    /**
     * Set custom headers
     */
    public BaseApiClient withHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.requestSpec.headers(headers);
        }
        return this;
    }

    /**
     * Set single header
     */
    public BaseApiClient withHeader(String name, String value) {
        this.requestSpec.header(name, value);
        return this;
    }

    /**
     * Set content type
     */
    public BaseApiClient withContentType(ContentType contentType) {
        this.requestSpec.contentType(contentType);
        return this;
    }

    /**
     * Set request body as string
     */
    public BaseApiClient withBody(String body) {
        this.requestSpec.body(body);
        return this;
    }

    /**
     * Set request body as object (will be serialized to JSON)
     */
    public BaseApiClient withBody(Object body) {
        String jsonBody = JsonUtils.objectToJson(body);
        this.requestSpec.body(jsonBody);
        return this;
    }

    /**
     * Set query parameters
     */
    public BaseApiClient withQueryParams(Map<String, Object> queryParams) {
        if (queryParams != null && !queryParams.isEmpty()) {
            this.requestSpec.queryParams(queryParams);
        }
        return this;
    }

    /**
     * Set single query parameter
     */
    public BaseApiClient withQueryParam(String name, Object value) {
        this.requestSpec.queryParam(name, value);
        return this;
    }

    /**
     * Set path parameters
     */
    public BaseApiClient withPathParams(Map<String, Object> pathParams) {
        if (pathParams != null && !pathParams.isEmpty()) {
            this.requestSpec.pathParams(pathParams);
        }
        return this;
    }

    /**
     * Set single path parameter
     */
    public BaseApiClient withPathParam(String name, Object value) {
        this.requestSpec.pathParam(name, value);
        return this;
    }

    /**
     * Set multipart file for upload
     */
    public BaseApiClient withMultiPart(String controlName, File file) {
        this.requestSpec.multiPart(controlName, file);
        return this;
    }

    /**
     * Set multipart file for upload with custom content type
     */
    public BaseApiClient withMultiPart(String controlName, File file, String mimeType) {
        this.requestSpec.multiPart(controlName, file, mimeType);
        return this;
    }

    /**
     * Set multipart file for upload with filename
     */
    public BaseApiClient withMultiPart(String controlName, String fileName, byte[] content, String mimeType) {
        this.requestSpec.multiPart(controlName, fileName, content, mimeType);
        return this;
    }

    /**
     * Set multiple files for upload
     */
    public BaseApiClient withMultiParts(String controlName, File... files) {
        for (File file : files) {
            this.requestSpec.multiPart(controlName, file);
        }
        return this;
    }

    /**
     * Set form parameter (for multipart requests)
     */
    public BaseApiClient withFormParam(String name, Object value) {
        this.requestSpec.formParam(name, value);
        return this;
    }

    /**
     * Set multiple form parameters
     */
    public BaseApiClient withFormParams(Map<String, Object> formParams) {
        if (formParams != null && !formParams.isEmpty()) {
            this.requestSpec.formParams(formParams);
        }
        return this;
    }

    /**
     * Execute GET request
     */
    public Response get(String endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("Endpoint cannot be null");
        }
        
        logger.info("Executing GET request to: {}", endpoint);
        
        // Validate that requestSpec is properly initialized
        if (requestSpec == null) {
            logger.error("Request specification is null - configuration may not be properly initialized");
            throw new RuntimeException("Request specification is null - BaseApiClient not properly initialized");
        }
        
        try {
            return RetryHandler.executeWithRetry(() -> {
                try {
                    return RestAssured.given()
                        .spec(requestSpec)
                        .when()
                        .get(endpoint);
                } catch (Exception e) {
                    logger.debug("Exception during GET request execution: {}", e.getMessage());
                    throw e;
                }
            });
        } catch (Exception e) {
            logger.error("GET request failed for endpoint: {} - Error: {}", endpoint, e.getMessage(), e);
            throw new RuntimeException("GET request failed for endpoint: " + endpoint + " - " + e.getMessage(), e);
        }
    }

    /**
     * Execute POST request
     */
    public Response post(String endpoint) {
        logger.info("Executing POST request to: {}", endpoint);
        try {
            return RetryHandler.executeWithRetry(() -> 
                RestAssured.given()
                    .spec(requestSpec)
                    .when()
                    .post(endpoint)
            );
        } catch (Exception e) {
            logger.error("POST request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute PUT request
     */
    public Response put(String endpoint) {
        logger.info("Executing PUT request to: {}", endpoint);
        try {
            return RetryHandler.executeWithRetry(() -> 
                RestAssured.given()
                    .spec(requestSpec)
                    .when()
                    .put(endpoint)
            );
        } catch (Exception e) {
            logger.error("PUT request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("PUT request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute PATCH request
     */
    public Response patch(String endpoint) {
        logger.info("Executing PATCH request to: {}", endpoint);
        try {
            return RetryHandler.executeWithRetry(() -> 
                RestAssured.given()
                    .spec(requestSpec)
                    .when()
                    .patch(endpoint)
            );
        } catch (Exception e) {
            logger.error("PATCH request failed for endpoint: {}", endpoint, e);
            throw new RuntimeException("PATCH request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Execute DELETE request
     */
    public Response delete(String endpoint) {
        logger.info("Executing DELETE request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> 
            RestAssured.given()
                .spec(requestSpec)
                .when()
                .delete(endpoint)
        );
    }

    /**
     * Execute HEAD request
     */
    public Response head(String endpoint) {
        logger.info("Executing HEAD request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> 
            RestAssured.given()
                .spec(requestSpec)
                .when()
                .head(endpoint)
        );
    }

    /**
     * Execute OPTIONS request
     */
    public Response options(String endpoint) {
        logger.info("Executing OPTIONS request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.options(endpoint));
    }

    /**
     * Get a new instance with fresh request specification
     */
    public BaseApiClient fresh() {
        return new BaseApiClient();
    }

    /**
     * Reset the request specification
     */
    public BaseApiClient reset() {
        RequestSpecification baseSpec = RestAssuredConfig.getDefaultRequestSpec();
        
        if (config != null) {
            String baseUrl = config.getBaseUrl();
            if (baseUrl != null && !baseUrl.isEmpty()) {
                this.requestSpec = new RequestSpecBuilder()
                    .addRequestSpecification(baseSpec)
                    .setBaseUri(baseUrl)
                    .build();
            } else {
                this.requestSpec = baseSpec;
            }
        } else {
            this.requestSpec = baseSpec;
        }
        return this;
    }

    /**
     * Get the current request specification (for advanced usage)
     */
    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }
}
