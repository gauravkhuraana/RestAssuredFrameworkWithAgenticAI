package com.api.automation.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Utility class to generate cURL commands from REST API requests
 * Useful for debugging failed tests by reproducing the exact request
 */
public class CurlGenerator implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(CurlGenerator.class);
    private static final String CURL_OUTPUT_DIR = "target/curl-commands";
    private boolean enableCurlGeneration = false;
    private String testName = "";
    
    public CurlGenerator() {
        // Create curl output directory
        new File(CURL_OUTPUT_DIR).mkdirs();
    }
    
    public CurlGenerator enableForTest(String testName) {
        this.enableCurlGeneration = true;
        this.testName = testName;
        return this;
    }
    
    public CurlGenerator disable() {
        this.enableCurlGeneration = false;
        this.testName = "";
        return this;
    }
    
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, 
                          FilterableResponseSpecification responseSpec, 
                          FilterContext ctx) {
        
        Response response = ctx.next(requestSpec, responseSpec);
        
        // Generate curl command if enabled and test failed
        if (enableCurlGeneration && (response.getStatusCode() >= 400 || isTestFailed())) {
            String curlCommand = generateCurlCommand(requestSpec);
            saveCurlCommand(curlCommand, testName, response.getStatusCode());
        }
        
        return response;
    }
    
    /**
     * Generate cURL command from request specification
     */
    public static String generateCurlCommand(FilterableRequestSpecification requestSpec) {
        StringBuilder curl = new StringBuilder("curl");
        
        // Add method
        String method = requestSpec.getMethod();
        if (!"GET".equals(method)) {
            curl.append(" -X ").append(method);
        }
        
        // Add headers
        Map<String, String> headers = requestSpec.getHeaders().asList().stream()
            .collect(java.util.stream.Collectors.toMap(
                header -> header.getName(),
                header -> header.getValue(),
                (existing, replacement) -> existing
            ));
            
        for (Map.Entry<String, String> header : headers.entrySet()) {
            curl.append(" -H \"").append(header.getKey()).append(": ").append(header.getValue()).append("\"");
        }
        
        // Add body if present
        String body = requestSpec.getBody();
        if (body != null && !body.isEmpty()) {
            // Escape quotes and format JSON
            String escapedBody = body.replace("\"", "\\\"").replace("\n", "").replace("\r", "");
            curl.append(" -d \"").append(escapedBody).append("\"");
        }
        
        // Add URL
        String baseUri = requestSpec.getBaseUri();
        String basePath = requestSpec.getBasePath() != null ? requestSpec.getBasePath() : "";
        String uri = requestSpec.getURI();
        
        String fullUrl = baseUri + basePath + uri;
        curl.append(" \"").append(fullUrl).append("\"");
        
        return curl.toString();
    }
    
    /**
     * Save cURL command to file
     */
    private void saveCurlCommand(String curlCommand, String testName, int statusCode) {
        try {
            String fileName = String.format("%s/curl_%s_%d_%d.sh", 
                CURL_OUTPUT_DIR, 
                sanitizeFileName(testName), 
                statusCode,
                System.currentTimeMillis());
                
            File curlFile = new File(fileName);
            
            try (FileWriter writer = new FileWriter(curlFile)) {
                writer.write("#!/bin/bash\n");
                writer.write("# Generated cURL command for failed test: " + testName + "\n");
                writer.write("# Response Status Code: " + statusCode + "\n");
                writer.write("# Generated at: " + java.time.LocalDateTime.now() + "\n\n");
                writer.write(curlCommand + "\n");
            }
            
            // Make file executable on Unix-like systems
            curlFile.setExecutable(true);
            
            logger.info("cURL command saved to: {}", fileName);
            
        } catch (IOException e) {
            logger.error("Failed to save cURL command for test: {}", testName, e);
        }
    }
    
    /**
     * Generate cURL command from a simple request (utility method)
     */
    public static String generateSimpleCurl(String method, String url, Map<String, String> headers, String body) {
        StringBuilder curl = new StringBuilder("curl");
        
        // Add method
        if (!"GET".equals(method)) {
            curl.append(" -X ").append(method);
        }
        
        // Add headers
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                curl.append(" -H \"").append(header.getKey()).append(": ").append(header.getValue()).append("\"");
            }
        }
        
        // Add body
        if (body != null && !body.isEmpty()) {
            String escapedBody = body.replace("\"", "\\\"");
            curl.append(" -d \"").append(escapedBody).append("\"");
        }
        
        // Add URL
        curl.append(" \"").append(url).append("\"");
        
        return curl.toString();
    }
    
    /**
     * Check if current test has failed (simplified check)
     */
    private boolean isTestFailed() {
        // This is a simplified implementation
        // In a real scenario, you might want to integrate with test framework
        return false;
    }
    
    /**
     * Sanitize file name by removing invalid characters
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Enable curl generation for debugging
     */
    public static CurlGenerator enableForDebugging() {
        return new CurlGenerator().enableForTest("debug_session");
    }
}
