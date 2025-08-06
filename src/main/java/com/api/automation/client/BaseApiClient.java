package com.api.automation.client;

import com.api.automation.auth.AuthHandler;
import com.api.automation.config.ConfigManager;
import com.api.automation.retry.RetryHandler;
import com.api.automation.utils.JsonUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Base API client with common HTTP operations
 */
public class BaseApiClient {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected static final ConfigManager config = ConfigManager.getInstance();
    
    protected RequestSpecification requestSpec;

    public BaseApiClient() {
        this.requestSpec = RestAssured.given();
    }

    public BaseApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
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
     * Execute GET request
     */
    public Response get(String endpoint) {
        logger.info("Executing GET request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.get(endpoint));
    }

    /**
     * Execute POST request
     */
    public Response post(String endpoint) {
        logger.info("Executing POST request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.post(endpoint));
    }

    /**
     * Execute PUT request
     */
    public Response put(String endpoint) {
        logger.info("Executing PUT request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.put(endpoint));
    }

    /**
     * Execute PATCH request
     */
    public Response patch(String endpoint) {
        logger.info("Executing PATCH request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.patch(endpoint));
    }

    /**
     * Execute DELETE request
     */
    public Response delete(String endpoint) {
        logger.info("Executing DELETE request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.delete(endpoint));
    }

    /**
     * Execute HEAD request
     */
    public Response head(String endpoint) {
        logger.info("Executing HEAD request to: {}", endpoint);
        return RetryHandler.executeWithRetry(() -> requestSpec.head(endpoint));
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
        this.requestSpec = RestAssured.given();
        return this;
    }

    /**
     * Get the current request specification (for advanced usage)
     */
    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }
}
