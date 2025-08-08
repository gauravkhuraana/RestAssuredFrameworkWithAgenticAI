package com.api.automation.utils;

import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Debug utility that automatically generates curl commands and HAR files for failed tests
 * This helps in debugging API test failures by providing reproducible requests
 */
public class DebugUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugUtils.class);
    private static final String DEBUG_OUTPUT_DIR = "target/debug-output";
    private static final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    private static boolean debugEnabled = false;
    private static String currentTestName = "";
    
    static {
        // Create debug output directory
        new File(DEBUG_OUTPUT_DIR).mkdirs();
        new File(DEBUG_OUTPUT_DIR + "/curl").mkdirs();
        new File(DEBUG_OUTPUT_DIR + "/har").mkdirs();
        new File(DEBUG_OUTPUT_DIR + "/reports").mkdirs();
    }
    
    /**
     * Enable debug mode for a specific test
     */
    public static void enableDebugForTest(String testName) {
        debugEnabled = true;
        currentTestName = testName;
        logger.info("Debug mode enabled for test: {}", testName);
    }
    
    /**
     * Disable debug mode
     */
    public static void disableDebug() {
        debugEnabled = false;
        currentTestName = "";
        logger.info("Debug mode disabled");
    }
    
    /**
     * Check if debug mode is enabled
     */
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    /**
     * Generate debug artifacts for a failed test
     */
    public static void generateDebugArtifacts(FilterableRequestSpecification requestSpec, 
                                            Response response, String testName, Throwable error) {
        if (!debugEnabled) {
            return;
        }
        
        try {
            String timestamp = LocalDateTime.now().format(timestampFormatter);
            String baseFileName = String.format("%s_%s_%d", 
                sanitizeFileName(testName), timestamp, response.getStatusCode());
            
            // Generate cURL command
            String curlCommand = CurlGenerator.generateCurlCommand(requestSpec);
            saveCurlCommand(curlCommand, baseFileName, response.getStatusCode(), error);
            
            // Generate HAR file
            HarGenerator.saveAsHar(baseFileName, requestSpec, response);
            
            // Generate debug report
            generateDebugReport(baseFileName, requestSpec, response, error);
            
            logger.info("Debug artifacts generated for failed test: {} in directory: {}", 
                testName, DEBUG_OUTPUT_DIR);
            
        } catch (Exception e) {
            logger.error("Failed to generate debug artifacts for test: {}", testName, e);
        }
    }
    
    /**
     * Generate a comprehensive debug report
     */
    private static void generateDebugReport(String baseFileName, FilterableRequestSpecification requestSpec, 
                                          Response response, Throwable error) {
        try {
            StringBuilder report = new StringBuilder();
            
            // Header
            report.append("# Debug Report for Test Failure\\n");
            report.append("**Generated at:** ").append(LocalDateTime.now()).append("\\n");
            report.append("**Test Name:** ").append(currentTestName).append("\\n");
            report.append("**Status Code:** ").append(response.getStatusCode()).append("\\n\\n");
            
            // Error Information
            if (error != null) {
                report.append("## Error Details\\n");
                report.append("**Error Type:** ").append(error.getClass().getSimpleName()).append("\\n");
                report.append("**Error Message:** ").append(error.getMessage()).append("\\n");
                
                if (error.getCause() != null) {
                    report.append("**Root Cause:** ").append(error.getCause().getMessage()).append("\\n");
                }
                report.append("\\n");
            }
            
            // Request Details
            report.append("## Request Details\\n");
            report.append("**Method:** ").append(requestSpec.getMethod()).append("\\n");
            report.append("**URL:** ").append(requestSpec.getBaseUri())
                  .append(requestSpec.getBasePath() != null ? requestSpec.getBasePath() : "")
                  .append(requestSpec.getURI()).append("\\n");
            
            // Request Headers
            if (!requestSpec.getHeaders().asList().isEmpty()) {
                report.append("**Headers:**\\n");
                requestSpec.getHeaders().asList().forEach(header -> 
                    report.append("  - ").append(header.getName()).append(": ").append(header.getValue()).append("\\n"));
            }
            
            // Request Body
            String requestBody = requestSpec.getBody();
            if (requestBody != null && !requestBody.isEmpty()) {
                report.append("**Request Body:**\\n");
                report.append("```json\\n").append(requestBody).append("\\n```\\n\\n");
            }
            
            // Response Details
            report.append("## Response Details\\n");
            report.append("**Status Code:** ").append(response.getStatusCode()).append("\\n");
            report.append("**Status Line:** ").append(response.getStatusLine()).append("\\n");
            report.append("**Content Type:** ").append(response.getContentType()).append("\\n");
            report.append("**Response Time:** ").append(response.getTime()).append(" ms\\n");
            
            // Response Headers
            if (!response.getHeaders().asList().isEmpty()) {
                report.append("**Response Headers:**\\n");
                response.getHeaders().asList().forEach(header -> 
                    report.append("  - ").append(header.getName()).append(": ").append(header.getValue()).append("\\n"));
            }
            
            // Response Body
            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.isEmpty()) {
                report.append("**Response Body:**\\n");
                if (response.getContentType() != null && response.getContentType().contains("json")) {
                    report.append("```json\\n").append(responseBody).append("\\n```\\n\\n");
                } else {
                    report.append("```\\n").append(responseBody).append("\\n```\\n\\n");
                }
            }
            
            // Troubleshooting Tips
            report.append("## Troubleshooting Tips\\n");
            report.append("1. **Reproduce the request:** Use the generated cURL command in `curl/").append(baseFileName).append(".sh`\\n");
            report.append("2. **Analyze in browser:** Import the HAR file from `har/").append(baseFileName).append(".har` into browser dev tools\\n");
            report.append("3. **Check status code:** ").append(getStatusCodeSuggestion(response.getStatusCode())).append("\\n");
            report.append("4. **Validate request format:** Ensure headers and body format match API expectations\\n");
            report.append("5. **Check authentication:** Verify tokens, API keys, or credentials are valid\\n\\n");
            
            // Related Files
            report.append("## Related Debug Files\\n");
            report.append("- **cURL Command:** `curl/").append(baseFileName).append(".sh`\\n");
            report.append("- **HAR File:** `har/").append(baseFileName).append(".har`\\n");
            report.append("- **This Report:** `reports/").append(baseFileName).append(".md`\\n");
            
            // Save the report
            String reportPath = DEBUG_OUTPUT_DIR + "/reports/" + baseFileName + ".md";
            saveToFile(reportPath, report.toString());
            
        } catch (Exception e) {
            logger.error("Failed to generate debug report for: {}", baseFileName, e);
        }
    }
    
    /**
     * Save cURL command with additional debug info
     */
    private static void saveCurlCommand(String curlCommand, String baseFileName, int statusCode, Throwable error) {
        try {
            StringBuilder curlScript = new StringBuilder();
            curlScript.append("#!/bin/bash\\n");
            curlScript.append("# Debug cURL command for failed test: ").append(currentTestName).append("\\n");
            curlScript.append("# Status Code: ").append(statusCode).append("\\n");
            curlScript.append("# Generated at: ").append(LocalDateTime.now()).append("\\n");
            
            if (error != null) {
                curlScript.append("# Error: ").append(error.getMessage()).append("\\n");
            }
            
            curlScript.append("\\n");
            curlScript.append("echo 'Reproducing failed request...'\\n");
            curlScript.append("echo 'Original Status Code: ").append(statusCode).append("'\\n");
            curlScript.append("echo ''\\n");
            curlScript.append("\\n");
            
            // Add the actual curl command
            curlScript.append(curlCommand);
            
            // Add some debugging options
            curlScript.append(" \\\\\\n");
            curlScript.append("  --verbose \\\\\\n");
            curlScript.append("  --include \\\\\\n");
            curlScript.append("  --location \\\\\\n");
            curlScript.append("  --max-time 30\\n");
            
            String curlPath = DEBUG_OUTPUT_DIR + "/curl/" + baseFileName + ".sh";
            saveToFile(curlPath, curlScript.toString());
            
            // Make file executable on Unix-like systems
            new File(curlPath).setExecutable(true);
            
        } catch (Exception e) {
            logger.error("Failed to save cURL command for: {}", baseFileName, e);
        }
    }
    
    /**
     * Get troubleshooting suggestion based on status code
     */
    private static String getStatusCodeSuggestion(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request - Check request body format and required fields";
            case 401 -> "Unauthorized - Verify authentication credentials";
            case 403 -> "Forbidden - Check user permissions and access rights";
            case 404 -> "Not Found - Verify the endpoint URL and path parameters";
            case 405 -> "Method Not Allowed - Check if the HTTP method is correct";
            case 409 -> "Conflict - Resource may already exist or be in use";
            case 422 -> "Unprocessable Entity - Validate request data format";
            case 429 -> "Too Many Requests - Implement rate limiting or retry logic";
            case 500 -> "Internal Server Error - Server-side issue, check logs";
            case 502 -> "Bad Gateway - Check upstream services";
            case 503 -> "Service Unavailable - Service may be down or overloaded";
            case 504 -> "Gateway Timeout - Increase timeout or check service performance";
            default -> "Review API documentation and error response details";
        };
    }
    
    /**
     * Save content to file
     */
    private static void saveToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
    
    /**
     * Sanitize file name
     */
    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Generate debug session summary
     */
    public static void generateSessionSummary() {
        if (!debugEnabled) {
            return;
        }
        
        try {
            File debugDir = new File(DEBUG_OUTPUT_DIR);
            if (!debugDir.exists()) {
                return;
            }
            
            StringBuilder summary = new StringBuilder();
            summary.append("# Debug Session Summary\\n");
            summary.append("**Session for test:** ").append(currentTestName).append("\\n");
            summary.append("**Generated at:** ").append(LocalDateTime.now()).append("\\n\\n");
            
            // Count files in each directory
            File curlDir = new File(DEBUG_OUTPUT_DIR + "/curl");
            File harDir = new File(DEBUG_OUTPUT_DIR + "/har");
            File reportsDir = new File(DEBUG_OUTPUT_DIR + "/reports");
            
            summary.append("## Generated Artifacts\\n");
            summary.append("- **cURL Commands:** ").append(countFiles(curlDir)).append("\\n");
            summary.append("- **HAR Files:** ").append(countFiles(harDir)).append("\\n");
            summary.append("- **Debug Reports:** ").append(countFiles(reportsDir)).append("\\n\\n");
            
            summary.append("## Usage Instructions\\n");
            summary.append("1. Run cURL commands from the `curl/` directory to reproduce requests\\n");
            summary.append("2. Import HAR files from the `har/` directory into browser dev tools\\n");
            summary.append("3. Review detailed debug reports in the `reports/` directory\\n");
            
            String summaryPath = DEBUG_OUTPUT_DIR + "/debug_session_summary.md";
            saveToFile(summaryPath, summary.toString());
            
            logger.info("Debug session summary saved to: {}", summaryPath);
            
        } catch (Exception e) {
            logger.error("Failed to generate debug session summary", e);
        }
    }
    
    /**
     * Count files in directory
     */
    private static int countFiles(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return 0;
        }
        
        File[] files = directory.listFiles();
        return files != null ? files.length : 0;
    }
}
