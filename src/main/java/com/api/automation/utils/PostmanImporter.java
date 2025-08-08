package com.api.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class to import Postman collections and generate test templates
 */
public class PostmanImporter {
    
    private static final Logger logger = LoggerFactory.getLogger(PostmanImporter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OUTPUT_DIR = "src/test/java/com/api/automation/tests/generated";
    private static final String TEMPLATE_DIR = "target/postman-templates";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    
    /**
     * Import Postman collection from URL and generate test templates
     */
    public static void importPostmanCollection(String collectionUrl, String packageName) {
        try {
            logger.info("Importing Postman collection from: {}", collectionUrl);
            
            String collectionJson = downloadCollection(collectionUrl);
            JsonNode collection = objectMapper.readTree(collectionJson);
            
            // Create output directories
            createDirectories();
            
            // Generate test templates from collection
            generateTestsFromCollection(collection, packageName);
            
            // Generate collection documentation
            generateCollectionDocumentation(collection);
            
            logger.info("Successfully imported Postman collection and generated test templates");
            
        } catch (Exception e) {
            logger.error("Failed to import Postman collection: {}", e.getMessage(), e);
            throw new RuntimeException("Postman collection import failed", e);
        }
    }
    
    /**
     * Import Postman collection from local file
     */
    public static void importPostmanFile(String filePath, String packageName) {
        try {
            logger.info("Importing Postman collection from file: {}", filePath);
            
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("Postman collection file not found: " + filePath);
            }
            
            JsonNode collection = objectMapper.readTree(file);
            
            // Create output directories
            createDirectories();
            
            // Generate test templates from collection
            generateTestsFromCollection(collection, packageName);
            
            // Generate collection documentation
            generateCollectionDocumentation(collection);
            
            logger.info("Successfully imported Postman collection file and generated test templates");
            
        } catch (Exception e) {
            logger.error("Failed to import Postman collection file: {}", e.getMessage(), e);
            throw new RuntimeException("Postman collection file import failed", e);
        }
    }
    
    /**
     * Download collection from URL
     */
    private static String downloadCollection(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to download collection. Status: " + response.statusCode());
            }
            
            return response.body();
            
        } catch (Exception e) {
            logger.error("Failed to download Postman collection from: {}", url, e);
            throw new RuntimeException("Collection download failed", e);
        }
    }
    
    /**
     * Create necessary directories
     */
    private static void createDirectories() {
        new File(OUTPUT_DIR).mkdirs();
        new File(TEMPLATE_DIR).mkdirs();
        new File(OUTPUT_DIR + "/services").mkdirs();
        new File(OUTPUT_DIR + "/smoke").mkdirs();
    }
    
    /**
     * Generate test templates from Postman collection
     */
    private static void generateTestsFromCollection(JsonNode collection, String packageName) {
        JsonNode info = collection.get("info");
        String collectionName = info != null && info.has("name") ? 
            info.get("name").asText() : "PostmanCollection";
        
        JsonNode items = collection.get("item");
        if (items != null && items.isArray()) {
            processItems(items, collectionName, packageName, "");
        }
    }
    
    /**
     * Process collection items recursively
     */
    private static void processItems(JsonNode items, String collectionName, String packageName, String folderPrefix) {
        for (JsonNode item : items) {
            if (item.has("item")) {
                // This is a folder, process recursively
                String folderName = item.get("name").asText();
                processItems(item.get("item"), collectionName, packageName, folderPrefix + folderName + "_");
            } else if (item.has("request")) {
                // This is a request, generate test
                generateTestFromRequest(item, collectionName, packageName, folderPrefix);
            }
        }
    }
    
    /**
     * Generate test class from Postman request
     */
    private static void generateTestFromRequest(JsonNode item, String collectionName, String packageName, String folderPrefix) {
        try {
            String requestName = item.get("name").asText();
            JsonNode request = item.get("request");
            
            String method = request.has("method") ? request.get("method").asText() : "GET";
            String url = extractUrl(request);
            
            String className = sanitizeClassName(folderPrefix + requestName);
            String testClass = generatePostmanTestClass(className, requestName, method, url, request, packageName);
            
            saveToFile(OUTPUT_DIR + "/smoke/" + className + "Test.java", testClass);
            logger.info("Generated test class from Postman request: {}Test", className);
            
        } catch (Exception e) {
            logger.error("Failed to generate test from Postman request: {}", item.get("name").asText(), e);
        }
    }
    
    /**
     * Extract URL from request object
     */
    private static String extractUrl(JsonNode request) {
        if (request.has("url")) {
            JsonNode url = request.get("url");
            if (url.isTextual()) {
                return url.asText();
            } else if (url.has("raw")) {
                return url.get("raw").asText();
            } else if (url.has("protocol") && url.has("host") && url.has("path")) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(url.get("protocol").asText()).append("://");
                
                JsonNode host = url.get("host");
                if (host.isArray()) {
                    for (int i = 0; i < host.size(); i++) {
                        if (i > 0) urlBuilder.append(".");
                        urlBuilder.append(host.get(i).asText());
                    }
                } else {
                    urlBuilder.append(host.asText());
                }
                
                JsonNode path = url.get("path");
                if (path.isArray()) {
                    for (JsonNode pathSegment : path) {
                        urlBuilder.append("/").append(pathSegment.asText());
                    }
                }
                
                return urlBuilder.toString();
            }
        }
        return "/";
    }
    
    /**
     * Generate test class content from Postman request
     */
    private static String generatePostmanTestClass(String className, String requestName, String method, 
                                                 String url, JsonNode request, String packageName) {
        StringBuilder classContent = new StringBuilder();
        
        classContent.append("package ").append(packageName).append(".tests.generated.smoke;\n\n");
        classContent.append("import com.api.automation.tests.base.BaseTest;\n");
        classContent.append("import io.restassured.response.Response;\n");
        classContent.append("import org.junit.jupiter.api.DisplayName;\n");
        classContent.append("import org.junit.jupiter.api.Tag;\n");
        classContent.append("import org.junit.jupiter.api.Test;\n");
        classContent.append("import static org.hamcrest.Matchers.*;\n\n");
        
        classContent.append("/**\n");
        classContent.append(" * Generated test class for Postman request: ").append(requestName).append("\n");
        classContent.append(" * Generated from Postman collection\n");
        classContent.append(" */\n");
        classContent.append("@Tag(\"smoke\")\n");
        classContent.append("@Tag(\"postman\")\n");
        classContent.append("public class ").append(className).append("Test extends BaseTest {\n\n");
        
        // Generate test method
        String methodName = "test" + sanitizeMethodName(requestName);
        classContent.append("    @Test\n");
        classContent.append("    @DisplayName(\"").append(requestName).append("\")\n");
        classContent.append("    void ").append(methodName).append("() {\n");
        classContent.append("        logStep(\"Send ").append(method).append(" request to ").append(url).append("\");\n");
        classContent.append("        \n");
        
        // Add headers if present
        if (request.has("header") && request.get("header").isArray()) {
            classContent.append("        Response response = getRequestSpecification()\n");
            for (JsonNode header : request.get("header")) {
                if (header.has("key") && header.has("value")) {
                    String key = header.get("key").asText();
                    String value = header.get("value").asText();
                    classContent.append("                .header(\"").append(key).append("\", \"").append(value).append("\")\n");
                }
            }
        } else {
            classContent.append("        Response response = getRequestSpecification()\n");
        }
        
        // Add body if present
        if (request.has("body") && request.get("body").has("raw")) {
            String body = request.get("body").get("raw").asText();
            classContent.append("                .body(\"").append(body.replace("\"", "\\\"")).append("\")\n");
        }
        
        classContent.append("                .when()\n");
        classContent.append("                .").append(method.toLowerCase()).append("(\"").append(url).append("\");\n");
        classContent.append("        \n");
        classContent.append("        logStep(\"Verify response\");\n");
        classContent.append("        response.then()\n");
        classContent.append("                .statusCode(200); // TODO: Add proper validations based on Postman tests\n");
        classContent.append("        \n");
        classContent.append("        logVerification(\"").append(requestName).append(" test passed\");\n");
        classContent.append("    }\n");
        classContent.append("}\n");
        
        return classContent.toString();
    }
    
    /**
     * Generate collection documentation
     */
    private static void generateCollectionDocumentation(JsonNode collection) {
        try {
            StringBuilder docs = new StringBuilder();
            docs.append("# Postman Collection Documentation\n\n");
            
            JsonNode info = collection.get("info");
            if (info != null) {
                if (info.has("name")) {
                    docs.append("## ").append(info.get("name").asText()).append("\n\n");
                }
                if (info.has("description")) {
                    docs.append(info.get("description").asText()).append("\n\n");
                }
            }
            
            docs.append("## Requests\n\n");
            
            JsonNode items = collection.get("item");
            if (items != null && items.isArray()) {
                documentItems(docs, items, 0);
            }
            
            saveToFile(TEMPLATE_DIR + "/Postman_Collection_Documentation.md", docs.toString());
            logger.info("Generated Postman collection documentation");
            
        } catch (Exception e) {
            logger.error("Failed to generate Postman collection documentation", e);
        }
    }
    
    /**
     * Document collection items recursively
     */
    private static void documentItems(StringBuilder docs, JsonNode items, int level) {
        String prefix = "#".repeat(Math.min(level + 3, 6));
        
        for (JsonNode item : items) {
            if (item.has("item")) {
                // This is a folder
                docs.append(prefix).append(" ").append(item.get("name").asText()).append("\n\n");
                documentItems(docs, item.get("item"), level + 1);
            } else if (item.has("request")) {
                // This is a request
                String name = item.get("name").asText();
                JsonNode request = item.get("request");
                String method = request.has("method") ? request.get("method").asText() : "GET";
                String url = extractUrl(request);
                
                docs.append(prefix).append(" ").append(name).append("\n");
                docs.append("- **Method:** ").append(method).append("\n");
                docs.append("- **URL:** ").append(url).append("\n\n");
            }
        }
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
     * Sanitize class name
     */
    private static String sanitizeClassName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "")
                  .replaceAll("^[0-9]", "Test")
                  .substring(0, Math.min(name.length(), 50));
    }
    
    /**
     * Sanitize method name
     */
    private static String sanitizeMethodName(String name) {
        String methodName = name.replaceAll("[^a-zA-Z0-9]", "");
        return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
    }
}
