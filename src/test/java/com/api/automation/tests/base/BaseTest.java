package com.api.automation.tests.base;

import com.api.automation.config.ConfigManager;
import com.api.automation.config.RestAssuredConfig;
import com.api.automation.reporting.ExtentReportManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base test class for all API tests
 */
public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static ConfigManager config;

    @BeforeAll
    static void setUpClass() {
        logger.info("Setting up test class");
        
        // Initialize configuration
        config = ConfigManager.getInstance();
        
        // Initialize Rest Assured
        RestAssuredConfig.setup();
        
        // Add filters for logging and reporting
        RestAssured.filters(
            new RequestLoggingFilter(),
            new ResponseLoggingFilter(),
            new AllureRestAssured()
        );
        
        // Initialize Extent Reports
        ExtentReportManager.initReports();
        
        logger.info("Test class setup completed for environment: {}", config.getEnvironment());
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        String testClass = testInfo.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        
        logger.info("Starting test: {} in class: {}", testName, testClass);
        
        // Create test in Extent Reports
        ExtentReportManager.createTest(testName, "API Test: " + testName);
        ExtentReportManager.logInfo("Test started: " + testName);
        ExtentReportManager.logInfo("Environment: " + config.getEnvironment());
        ExtentReportManager.logInfo("Base URL: " + config.getBaseUrl());
        
        // Add tags based on test class
        if (testClass.toLowerCase().contains("smoke")) {
            ExtentReportManager.addTag("smoke");
        }
        if (testClass.toLowerCase().contains("regression")) {
            ExtentReportManager.addTag("regression");
        }
        if (testClass.toLowerCase().contains("auth")) {
            ExtentReportManager.addTag("auth");
        }
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        logger.info("Completed test: {}", testName);
        
        ExtentReportManager.logInfo("Test completed: " + testName);
        ExtentReportManager.endTest();
    }

    @AfterAll
    static void tearDownClass() {
        logger.info("Tearing down test class");
        
        // Flush reports
        ExtentReportManager.flushReports();
        
        // Reset Rest Assured
        RestAssuredConfig.reset();
        
        logger.info("Test class teardown completed");
    }

    /**
     * Log test step
     */
    protected void logStep(String step) {
        logger.info("STEP: {}", step);
        ExtentReportManager.logInfo("STEP: " + step);
    }

    /**
     * Log test verification
     */
    protected void logVerification(String verification) {
        logger.info("VERIFICATION: {}", verification);
        ExtentReportManager.logPass("VERIFICATION: " + verification);
    }

    /**
     * Log test failure
     */
    protected void logFailure(String message) {
        logger.error("FAILURE: {}", message);
        ExtentReportManager.logFail("FAILURE: " + message);
    }

    /**
     * Log test failure with exception
     */
    protected void logFailure(String message, Throwable throwable) {
        logger.error("FAILURE: {}", message, throwable);
        ExtentReportManager.logFail("FAILURE: " + message, throwable);
    }
}
