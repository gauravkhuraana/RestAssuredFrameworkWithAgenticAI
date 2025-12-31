package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.HealthResponse;
import com.api.automation.services.billpay.HealthService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Health Check Tests for Bill Payment API
 * Verifies API connectivity and service availability
 */
@Epic("Bill Payment API")
@Feature("Health Check")
public class HealthCheckTest {

    private static HealthService healthService;

    @BeforeAll
    static void setup() {
        healthService = new HealthService();
    }

    @Test
    @DisplayName("Verify API health endpoint returns healthy status")
    @Severity(SeverityLevel.BLOCKER)
    @Story("API Connectivity")
    @Description("Tests the /health endpoint to ensure the API is running and responsive")
    void testHealthEndpoint() {
        Response response = healthService.checkHealth();
        
        assertEquals(200, response.getStatusCode(), "Health endpoint should return 200 OK");
        
        HealthResponse healthResponse = response.as(HealthResponse.class);
        assertNotNull(healthResponse, "Health response should not be null");
        assertEquals("healthy", healthResponse.getStatus(), "API status should be healthy");
        assertNotNull(healthResponse.getData(), "Data should be present");
        assertNotNull(healthResponse.getData().getTimestamp(), "Timestamp should be present");
    }

    @Test
    @DisplayName("Verify API root endpoint returns welcome message")
    @Severity(SeverityLevel.CRITICAL)
    @Story("API Connectivity")
    @Description("Tests the root endpoint to verify API is accessible")
    void testRootEndpoint() {
        Response response = healthService.checkRoot();
        
        assertEquals(200, response.getStatusCode(), "Root endpoint should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.length() > 0, "Response body should not be empty");
    }

    @Test
    @DisplayName("Verify API version information")
    @Severity(SeverityLevel.NORMAL)
    @Story("API Information")
    @Description("Validates API returns version information in health check")
    void testApiVersionInfo() {
        Response response = healthService.checkHealth();
        
        assertEquals(200, response.getStatusCode(), "Health endpoint should return 200 OK");
        
        HealthResponse healthResponse = response.as(HealthResponse.class);
        assertNotNull(healthResponse.getVersion(), "API version should be present");
    }

    @Test
    @DisplayName("Verify health endpoint response time is acceptable")
    @Severity(SeverityLevel.NORMAL)
    @Story("API Performance")
    @Description("Ensures health endpoint responds within acceptable time limit")
    void testHealthEndpointResponseTime() {
        Response response = healthService.checkHealth();
        
        long responseTime = response.getTime();
        assertTrue(responseTime < 5000, "Health endpoint should respond within 5 seconds, actual: " + responseTime + "ms");
    }
}
