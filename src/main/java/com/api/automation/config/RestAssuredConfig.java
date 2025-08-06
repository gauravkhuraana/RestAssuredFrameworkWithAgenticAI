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
 * Thread-Safe Rest Assured Configuration Setup
 */
public class RestAssuredConfig {
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredConfig.class);
    private static final ConfigManager config = ConfigManager.getInstance();
    private static volatile boolean isInitialized = false;
    private static final Object lock = new Object();

    public static void setup() {
        if (!isInitialized) {
            synchronized (lock) {
                if (!isInitialized) {
                    logger.info("Setting up Rest Assured configuration for environment: {}", config.getEnvironment());
                    
                    // CI Environment specific configuration
                    if (isRunningInCI()) {
                        logger.info("Detected CI environment, applying CI-specific RestAssured configurations");
                        System.setProperty("groovy.indy", "false");
                        System.setProperty("groovy.target.indy", "false");
                        System.setProperty("groovy.antlr4", "false");
                        System.setProperty("restassured.config.redirect.followRedirects", "true");
                        System.setProperty("restassured.config.connection.closeIdleConnectionsAfterEachResponse", "true");
                    }
                    
                    // Base configuration with null safety
                    String baseUrl = config.getBaseUrl();
                    if (baseUrl != null && !baseUrl.isEmpty()) {
                        RestAssured.baseURI = baseUrl;
                        logger.info("Set base URI to: {}", baseUrl);
                    } else {
                        logger.warn("Base URL is null or empty, using default");
                        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
                    }
                    
                    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
                    
                    // Don't set static specifications to avoid thread conflicts
                    // Each test will create its own specifications
                    
                    isInitialized = true;
                    logger.info("Rest Assured configuration completed");
                }
            }
        }
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
                    // Create basic auth header manually to avoid static RestAssured
                    String credentials = java.util.Base64.getEncoder()
                        .encodeToString((username + ":" + password).getBytes());
                    builder.addHeader("Authorization", "Basic " + credentials);
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
        synchronized (lock) {
            RestAssured.reset();
            isInitialized = false;
            logger.info("Rest Assured configuration reset");
        }
    }
    
    /**
     * Check if running in CI environment
     */
    private static boolean isRunningInCI() {
        String ci = System.getenv("CI");
        String githubActions = System.getenv("GITHUB_ACTIONS");
        String azureDevops = System.getenv("SYSTEM_TEAMPROJECT");
        
        return "true".equalsIgnoreCase(ci) || 
               "true".equalsIgnoreCase(githubActions) || 
               azureDevops != null;
    }
}
