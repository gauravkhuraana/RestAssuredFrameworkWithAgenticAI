package com.api.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Extent Reports manager for test reporting
 */
public class ExtentReportManager {
    private static final Logger logger = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    private static final String REPORT_DIR = "test-output/extent-reports";
    private static final String REPORT_NAME = "API-Test-Report";

    /**
     * Initialize Extent Reports
     */
    public static void initReports() {
        if (extent == null) {
            createReportDirectory();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = REPORT_DIR + File.separator + REPORT_NAME + "_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            setSystemInfo();
            
            logger.info("Extent Reports initialized: {}", reportPath);
        }
    }

    /**
     * Configure Spark Reporter
     */
    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("API Automation Test Results");
        sparkReporter.config().setReportName("Rest Assured API Tests");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setEncoding("utf-8");
    }

    /**
     * Set system information
     */
    private static void setSystemInfo() {
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Environment", System.getProperty("env", "dev"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
    }

    /**
     * Create report directory
     */
    private static void createReportDirectory() {
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
    }

    /**
     * Create a new test
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        logger.debug("Created test: {}", testName);
        return extentTest;
    }

    /**
     * Create a new test with description
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        logger.debug("Created test: {} with description: {}", testName, description);
        return extentTest;
    }

    /**
     * Get current test
     */
    public static ExtentTest getTest() {
        return test.get();
    }

    /**
     * Log info message
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().info(message);
        }
        logger.info(message);
    }

    /**
     * Log pass message
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
        logger.info("PASS: {}", message);
    }

    /**
     * Log fail message
     */
    public static void logFail(String message) {
        if (getTest() != null) {
            getTest().fail(message);
        }
        logger.error("FAIL: {}", message);
    }

    /**
     * Log fail message with throwable
     */
    public static void logFail(String message, Throwable throwable) {
        if (getTest() != null) {
            getTest().fail(message).fail(throwable);
        }
        logger.error("FAIL: {}", message, throwable);
    }

    /**
     * Log skip message
     */
    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().skip(message);
        }
        logger.warn("SKIP: {}", message);
    }

    /**
     * Log warning message
     */
    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().warning(message);
        }
        logger.warn("WARNING: {}", message);
    }

    /**
     * Log request details
     */
    public static void logRequest(String method, String url, String headers, String body) {
        if (getTest() != null) {
            StringBuilder requestDetails = new StringBuilder();
            requestDetails.append("<details><summary><b>").append(method).append(" Request Details</b></summary>");
            requestDetails.append("<p><b>URL:</b> ").append(url).append("</p>");
            if (headers != null && !headers.isEmpty()) {
                requestDetails.append("<p><b>Headers:</b><br><pre>").append(headers).append("</pre></p>");
            }
            if (body != null && !body.isEmpty()) {
                requestDetails.append("<p><b>Body:</b><br><pre>").append(body).append("</pre></p>");
            }
            requestDetails.append("</details>");
            getTest().info(requestDetails.toString());
        }
    }

    /**
     * Log response details
     */
    public static void logResponse(int statusCode, String headers, String body, long responseTime) {
        if (getTest() != null) {
            StringBuilder responseDetails = new StringBuilder();
            responseDetails.append("<details><summary><b>Response Details</b></summary>");
            responseDetails.append("<p><b>Status Code:</b> ").append(statusCode).append("</p>");
            responseDetails.append("<p><b>Response Time:</b> ").append(responseTime).append(" ms</p>");
            if (headers != null && !headers.isEmpty()) {
                responseDetails.append("<p><b>Headers:</b><br><pre>").append(headers).append("</pre></p>");
            }
            if (body != null && !body.isEmpty()) {
                responseDetails.append("<p><b>Body:</b><br><pre>").append(body).append("</pre></p>");
            }
            responseDetails.append("</details>");
            getTest().info(responseDetails.toString());
        }
    }

    /**
     * Add tag to current test
     */
    public static void addTag(String tag) {
        if (getTest() != null) {
            getTest().assignCategory(tag);
        }
    }

    /**
     * Flush reports
     */
    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent Reports flushed");
        }
    }

    /**
     * End current test
     */
    public static void endTest() {
        test.remove();
    }
}
