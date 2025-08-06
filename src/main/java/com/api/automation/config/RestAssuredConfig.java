package com.api.automation.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Rest Assured Configuration Setup
 */
public class RestAssuredConfig {
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredConfig.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    public static void setup() {
        logger.info("Setting up Rest Assured configuration for environment: {}", config.getEnvironment());
        
        // Base configuration
        RestAssured.baseURI = config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Default specifications
        RestAssured.requestSpecification = getDefaultRequestSpec();
        RestAssured.responseSpecification = getDefaultResponseSpec();
        
        logger.info("Rest Assured configuration completed");
    }

    public static RequestSpecification getDefaultRequestSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "API-Automation-Framework/1.0")
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification getDefaultResponseSpec() {
        return new ResponseSpecBuilder()
                .expectResponseTime(org.hamcrest.Matchers.lessThan(
                        TimeUnit.MILLISECONDS.convert(config.getApiTimeout(), TimeUnit.MILLISECONDS)))
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getAuthenticatedRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .addRequestSpecification(getDefaultRequestSpec());

        String authType = config.getAuthType().toLowerCase();
        
        switch (authType) {
            case "bearer":
                String token = config.getAuthToken();
                if (token != null && !token.isEmpty()) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
                break;
                
            case "basic":
                String username = config.getAuthUsername();
                String password = config.getAuthPassword();
                if (username != null && password != null) {
                    builder.setAuth(RestAssured.basic(username, password));
                }
                break;
                
            case "apikey":
                String apiKey = config.getApiKey();
                if (apiKey != null && !apiKey.isEmpty()) {
                    builder.addHeader("X-API-Key", apiKey);
                }
                break;
                
            default:
                logger.warn("Unknown authentication type: {}", authType);
        }

        return builder.build();
    }

    public static void reset() {
        RestAssured.reset();
        logger.info("Rest Assured configuration reset");
    }
}
