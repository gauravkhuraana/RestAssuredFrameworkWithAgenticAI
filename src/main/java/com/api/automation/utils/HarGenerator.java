package com.api.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate HAR (HTTP Archive) files from REST API requests
 * HAR files can be imported into browser dev tools for detailed analysis
 */
public class HarGenerator implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(HarGenerator.class);
    private static final String HAR_OUTPUT_DIR = "target/har-files";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private boolean enableHarGeneration = false;
    private String testName = "";
    private List<ObjectNode> entries = new ArrayList<>();
    
    public HarGenerator() {
        // Create HAR output directory
        new File(HAR_OUTPUT_DIR).mkdirs();
    }
    
    public HarGenerator enableForTest(String testName) {
        this.enableHarGeneration = true;
        this.testName = testName;
        this.entries.clear();
        return this;
    }
    
    public HarGenerator disable() {
        if (enableHarGeneration && !entries.isEmpty()) {
            generateHarFile();
        }
        this.enableHarGeneration = false;
        this.testName = "";
        this.entries.clear();
        return this;
    }
    
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, 
                          FilterableResponseSpecification responseSpec, 
                          FilterContext ctx) {
        
        long startTime = System.currentTimeMillis();
        Response response = ctx.next(requestSpec, responseSpec);
        long endTime = System.currentTimeMillis();
        
        // Capture request/response if HAR generation is enabled
        if (enableHarGeneration) {
            ObjectNode entry = createHarEntry(requestSpec, response, startTime, endTime);
            entries.add(entry);
        }
        
        return response;
    }
    
    /**
     * Create a HAR entry for the request/response pair
     */
    private ObjectNode createHarEntry(FilterableRequestSpecification requestSpec, 
                                    Response response, long startTime, long endTime) {
        
        ObjectNode entry = objectMapper.createObjectNode();
        
        // Entry metadata
        entry.put("startedDateTime", formatTimestamp(startTime));
        entry.put("time", endTime - startTime);
        
        // Request object
        ObjectNode request = createRequestObject(requestSpec);
        entry.set("request", request);
        
        // Response object
        ObjectNode responseObj = createResponseObject(response);
        entry.set("response", responseObj);
        
        // Cache (empty for API calls)
        entry.set("cache", objectMapper.createObjectNode());
        
        // Timings
        ObjectNode timings = objectMapper.createObjectNode();
        timings.put("send", 0);
        timings.put("wait", endTime - startTime);
        timings.put("receive", 0);
        entry.set("timings", timings);
        
        return entry;
    }
    
    /**
     * Create HAR request object
     */
    private ObjectNode createRequestObject(FilterableRequestSpecification requestSpec) {
        ObjectNode request = objectMapper.createObjectNode();
        
        request.put("method", requestSpec.getMethod());
        
        // URL
        String baseUri = requestSpec.getBaseUri();
        String basePath = requestSpec.getBasePath() != null ? requestSpec.getBasePath() : "";
        String uri = requestSpec.getURI();
        String fullUrl = baseUri + basePath + uri;
        request.put("url", fullUrl);
        
        request.put("httpVersion", "HTTP/1.1");
        
        // Headers
        ArrayNode headers = objectMapper.createArrayNode();
        requestSpec.getHeaders().asList().forEach(header -> {
            ObjectNode headerObj = objectMapper.createObjectNode();
            headerObj.put("name", header.getName());
            headerObj.put("value", header.getValue());
            headers.add(headerObj);
        });
        request.set("headers", headers);
        
        // Query parameters
        ArrayNode queryString = objectMapper.createArrayNode();
        if (requestSpec.getQueryParams() != null) {
            requestSpec.getQueryParams().forEach((key, value) -> {
                ObjectNode param = objectMapper.createObjectNode();
                param.put("name", key);
                param.put("value", value.toString());
                queryString.add(param);
            });
        }
        request.set("queryString", queryString);
        
        // Request body
        ObjectNode postData = objectMapper.createObjectNode();
        String body = requestSpec.getBody();
        if (body != null && !body.isEmpty()) {
            postData.put("mimeType", "application/json");
            postData.put("text", body);
        }
        request.set("postData", postData);
        
        // Header and body sizes
        request.put("headersSize", -1);
        request.put("bodySize", body != null ? body.length() : 0);
        
        return request;
    }
    
    /**
     * Create HAR response object
     */
    private ObjectNode createResponseObject(Response response) {
        ObjectNode responseObj = objectMapper.createObjectNode();
        
        responseObj.put("status", response.getStatusCode());
        responseObj.put("statusText", response.getStatusLine());
        responseObj.put("httpVersion", "HTTP/1.1");
        
        // Headers
        ArrayNode headers = objectMapper.createArrayNode();
        response.getHeaders().asList().forEach(header -> {
            ObjectNode headerObj = objectMapper.createObjectNode();
            headerObj.put("name", header.getName());
            headerObj.put("value", header.getValue());
            headers.add(headerObj);
        });
        responseObj.set("headers", headers);
        
        // Content
        ObjectNode content = objectMapper.createObjectNode();
        String responseBody = response.getBody().asString();
        content.put("size", responseBody.length());
        content.put("mimeType", response.getContentType() != null ? response.getContentType() : "text/plain");
        content.put("text", responseBody);
        responseObj.set("content", content);
        
        // Redirect URL (empty for API calls)
        responseObj.put("redirectURL", "");
        
        // Header and body sizes
        responseObj.put("headersSize", -1);
        responseObj.put("bodySize", responseBody.length());
        
        return responseObj;
    }
    
    /**
     * Generate the complete HAR file
     */
    private void generateHarFile() {
        try {
            ObjectNode har = objectMapper.createObjectNode();
            
            // HAR format version
            ObjectNode log = objectMapper.createObjectNode();
            log.put("version", "1.2");
            
            // Creator info
            ObjectNode creator = objectMapper.createObjectNode();
            creator.put("name", "REST Assured Framework");
            creator.put("version", "1.0.0");
            log.set("creator", creator);
            
            // Entries
            ArrayNode entriesArray = objectMapper.createArrayNode();
            entries.forEach(entriesArray::add);
            log.set("entries", entriesArray);
            
            har.set("log", log);
            
            // Save to file
            String fileName = String.format("%s/har_%s_%d.har", 
                HAR_OUTPUT_DIR, 
                sanitizeFileName(testName), 
                System.currentTimeMillis());
                
            File harFile = new File(fileName);
            
            try (FileWriter writer = new FileWriter(harFile)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, har);
            }
            
            logger.info("HAR file saved to: {}", fileName);
            
        } catch (IOException e) {
            logger.error("Failed to generate HAR file for test: {}", testName, e);
        }
    }
    
    /**
     * Format timestamp for HAR format
     */
    private String formatTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
    }
    
    /**
     * Sanitize file name by removing invalid characters
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Create a standalone HAR generator for debugging
     */
    public static HarGenerator enableForDebugging() {
        return new HarGenerator().enableForTest("debug_session");
    }
    
    /**
     * Save single request/response as HAR file (utility method)
     */
    public static void saveAsHar(String testName, FilterableRequestSpecification requestSpec, Response response) {
        HarGenerator generator = new HarGenerator();
        generator.enableForTest(testName);
        
        long startTime = System.currentTimeMillis();
        ObjectNode entry = generator.createHarEntry(requestSpec, response, startTime, startTime + response.getTime());
        generator.entries.add(entry);
        
        generator.generateHarFile();
    }
}
