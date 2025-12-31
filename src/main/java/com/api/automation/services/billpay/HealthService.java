package com.api.automation.services.billpay;

import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.HealthResponse;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Health Service for Bill Payment API health check endpoints
 * No authentication required for these endpoints
 */
public class HealthService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);
    
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String HEALTH_DB_ENDPOINT = "/health/db";
    private static final String ROOT_ENDPOINT = "/";

    /**
     * Basic health check
     * GET /health
     */
    public Response getHealth() {
        logger.info("Checking API health");
        return get(HEALTH_ENDPOINT);
    }

    /**
     * Health check - alias for getHealth
     */
    public Response checkHealth() {
        return getHealth();
    }

    /**
     * Check root endpoint
     * GET /
     */
    public Response checkRoot() {
        logger.info("Checking API root endpoint");
        return get(ROOT_ENDPOINT);
    }

    /**
     * Database health check
     * GET /health/db
     */
    public Response getDatabaseHealth() {
        logger.info("Checking database health");
        return get(HEALTH_DB_ENDPOINT);
    }

    /**
     * Get health status as object
     */
    public HealthResponse getHealthAsObject() {
        Response response = getHealth();
        response.then().statusCode(200);
        return JsonUtils.jsonToObject(response.getBody().asString(), HealthResponse.class);
    }

    /**
     * Get database health status as object
     */
    public HealthResponse getDatabaseHealthAsObject() {
        Response response = getDatabaseHealth();
        response.then().statusCode(200);
        return JsonUtils.jsonToObject(response.getBody().asString(), HealthResponse.class);
    }

    /**
     * Check if API is healthy
     */
    public boolean isApiHealthy() {
        try {
            Response response = getHealth();
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return false;
        }
    }

    /**
     * Check if database is healthy
     */
    public boolean isDatabaseHealthy() {
        try {
            Response response = getDatabaseHealth();
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            return false;
        }
    }
}
