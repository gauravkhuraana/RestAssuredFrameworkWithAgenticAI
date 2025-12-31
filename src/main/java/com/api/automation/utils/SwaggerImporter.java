package com.api.automation.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to import and generate test templates from Swagger/OpenAPI specifications
 */
public class SwaggerImporter {
    
    private static final Logger logger = LoggerFactory.getLogger(SwaggerImporter.class);
    private static final String OUTPUT_DIR = "src/test/java/com/api/automation/tests/generated";
    private static final String TEMPLATE_DIR = "target/swagger-templates";
    
    /**
     * Import Swagger/OpenAPI specification and generate test templates
     */
    public static void importSwaggerSpec(String swaggerUrl, String packageName) {
        try {
            logger.info("Importing Swagger specification from: {}", swaggerUrl);
            
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            OpenAPI openAPI = parser.read(swaggerUrl);
            
            if (openAPI == null) {
                throw new RuntimeException("Failed to parse Swagger specification from: " + swaggerUrl);
            }
            
            Info info = openAPI.getInfo();
            logger.info("Importing API: {} - {}", info.getTitle(), info.getVersion());
            
            // Create output directories
            createDirectories();
            
            // Generate API models
            generateApiModels(openAPI, packageName);
            
            // Generate service classes
            generateServiceClasses(openAPI, packageName);
            
            // Generate test templates
            generateTestTemplates(openAPI, packageName);
            
            // Generate API documentation
            generateApiDocumentation(openAPI);
            
            logger.info("Successfully imported Swagger specification and generated test templates");
            
        } catch (Exception e) {
            logger.error("Failed to import Swagger specification: {}", e.getMessage(), e);
            throw new RuntimeException("Swagger import failed", e);
        }
    }
    
    /**
     * Import from Swagger JSON file
     */
    public static void importSwaggerFile(String filePath, String packageName) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("Swagger file not found: " + filePath);
            }
            
            importSwaggerSpec(file.toURI().toString(), packageName);
            
        } catch (Exception e) {
            logger.error("Failed to import Swagger file: {}", e.getMessage(), e);
            throw new RuntimeException("Swagger file import failed", e);
        }
    }
    
    /**
     * Create necessary directories
     */
    private static void createDirectories() {
        new File(OUTPUT_DIR).mkdirs();
        new File(TEMPLATE_DIR).mkdirs();
        new File(OUTPUT_DIR + "/models").mkdirs();
        new File(OUTPUT_DIR + "/services").mkdirs();
        new File(OUTPUT_DIR + "/smoke").mkdirs();
    }
    
    /**
     * Generate API model classes from Swagger components
     */
    private static void generateApiModels(OpenAPI openAPI, String packageName) {
        if (openAPI.getComponents() == null || openAPI.getComponents().getSchemas() == null) {
            logger.info("No schemas found in Swagger specification");
            return;
        }
        
        openAPI.getComponents().getSchemas().forEach((schemaName, schema) -> {
            try {
                String modelClass = generateModelClass(schemaName, schema, packageName);
                saveToFile(OUTPUT_DIR + "/models/" + schemaName + ".java", modelClass);
                logger.info("Generated model class: {}", schemaName);
                
            } catch (Exception e) {
                logger.error("Failed to generate model for schema: {}", schemaName, e);
            }
        });
    }
    
    /**
     * Generate service classes from Swagger paths
     */
    private static void generateServiceClasses(OpenAPI openAPI, String packageName) {
        Map<String, StringBuilder> serviceClasses = new HashMap<>();
        
        if (openAPI.getPaths() == null) {
            logger.info("No paths found in Swagger specification");
            return;
        }
        
        openAPI.getPaths().forEach((path, pathItem) -> {
            String serviceName = extractServiceName(path);
            
            serviceClasses.computeIfAbsent(serviceName, k -> new StringBuilder())
                    .append(generateServiceMethods(path, pathItem, packageName));
        });
        
        serviceClasses.forEach((serviceName, serviceContent) -> {
            try {
                String fullServiceClass = generateServiceClass(serviceName, serviceContent.toString(), packageName);
                saveToFile(OUTPUT_DIR + "/services/" + serviceName + "Service.java", fullServiceClass);
                logger.info("Generated service class: {}Service", serviceName);
                
            } catch (Exception e) {
                logger.error("Failed to generate service class: {}", serviceName, e);
            }
        });
    }
    
    /**
     * Generate test templates from Swagger operations
     */
    private static void generateTestTemplates(OpenAPI openAPI, String packageName) {
        if (openAPI.getPaths() == null) {
            return;
        }
        
        openAPI.getPaths().forEach((path, pathItem) -> {
            String testClassName = extractTestClassName(path);
            
            try {
                String testClass = generateTestClass(testClassName, path, pathItem, packageName);
                saveToFile(OUTPUT_DIR + "/smoke/" + testClassName + "Test.java", testClass);
                logger.info("Generated test class: {}Test", testClassName);
                
            } catch (Exception e) {
                logger.error("Failed to generate test class: {}", testClassName, e);
            }
        });
    }
    
    /**
     * Generate API documentation
     */
    private static void generateApiDocumentation(OpenAPI openAPI) {
        try {
            StringBuilder docs = new StringBuilder();
            docs.append("# API Documentation\n\n");
            
            if (openAPI.getInfo() != null) {
                Info info = openAPI.getInfo();
                docs.append("## ").append(info.getTitle()).append("\n");
                docs.append("Version: ").append(info.getVersion()).append("\n\n");
                if (info.getDescription() != null) {
                    docs.append(info.getDescription()).append("\n\n");
                }
            }
            
            docs.append("## Available Endpoints\n\n");
            
            if (openAPI.getPaths() != null) {
                openAPI.getPaths().forEach((path, pathItem) -> {
                    docs.append("### ").append(path).append("\n\n");
                    
                    appendOperation(docs, "GET", pathItem.getGet());
                    appendOperation(docs, "POST", pathItem.getPost());
                    appendOperation(docs, "PUT", pathItem.getPut());
                    appendOperation(docs, "DELETE", pathItem.getDelete());
                    appendOperation(docs, "PATCH", pathItem.getPatch());
                });
            }
            
            saveToFile(TEMPLATE_DIR + "/API_Documentation.md", docs.toString());
            logger.info("Generated API documentation");
            
        } catch (Exception e) {
            logger.error("Failed to generate API documentation", e);
        }
    }
    
    /**
     * Extract service name from path
     */
    private static String extractServiceName(String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty() && !part.startsWith("{")) {
                return sanitizeClassName(capitalize(part));
            }
        }
        return "Default";
    }
    
    /**
     * Extract test class name from path
     */
    private static String extractTestClassName(String path) {
        return extractServiceName(path) + "Smoke";
    }
    
    /**
     * Sanitize a string to be a valid Java class name
     * - Replace hyphens, dots, and other invalid characters with empty or camelCase
     * - Ensure starts with a letter
     */
    private static String sanitizeClassName(String name) {
        if (name == null || name.isEmpty()) {
            return "Default";
        }
        
        // Replace hyphens and dots with nothing, but capitalize next letter
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            
            if (c == '-' || c == '.' || c == '_' || !Character.isJavaIdentifierPart(c)) {
                capitalizeNext = true;
            } else {
                if (capitalizeNext && Character.isLetter(c)) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else if (result.length() == 0 && !Character.isJavaIdentifierStart(c)) {
                    // Skip leading characters that can't start a class name
                    continue;
                } else {
                    result.append(c);
                }
            }
        }
        
        // Ensure the result starts with uppercase letter
        if (result.length() > 0) {
            char first = result.charAt(0);
            if (Character.isLetter(first) && Character.isLowerCase(first)) {
                result.setCharAt(0, Character.toUpperCase(first));
            }
        }
        
        String sanitized = result.toString();
        return sanitized.isEmpty() ? "Default" : sanitized;
    }
    
    /**
     * Generate model class content
     */
    private static String generateModelClass(String className, Object schema, String packageName) {
        StringBuilder classContent = new StringBuilder();
        
        classContent.append("package ").append("com.api.automation").append(".tests.generated.models;\n\n");
        classContent.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
        classContent.append("import lombok.Data;\n\n");
        classContent.append("/**\n");
        classContent.append(" * Generated model class for ").append(className).append("\n");
        classContent.append(" * Generated from Swagger/OpenAPI specification\n");
        classContent.append(" */\n");
        classContent.append("@Data\n");
        classContent.append("public class ").append(className).append(" {\n\n");
        
        // Add basic properties based on common API patterns
        classContent.append("    @JsonProperty(\"id\")\n");
        classContent.append("    private Long id;\n\n");
        
        classContent.append("    @JsonProperty(\"name\")\n");
        classContent.append("    private String name;\n\n");
        
        classContent.append("    @JsonProperty(\"description\")\n");
        classContent.append("    private String description;\n\n");
        
        classContent.append("    @JsonProperty(\"active\")\n");
        classContent.append("    private Boolean active;\n\n");
        
        classContent.append("    @JsonProperty(\"createdAt\")\n");
        classContent.append("    private String createdAt;\n\n");
        
        classContent.append("    @JsonProperty(\"updatedAt\")\n");
        classContent.append("    private String updatedAt;\n\n");
        
        classContent.append("    // TODO: Add more properties based on actual schema\n");
        classContent.append("    // Original schema details can be found in generated documentation\n");
        classContent.append("}\n");
        
        return classContent.toString();
    }
    
    /**
     * Generate service class content
     */
    private static String generateServiceClass(String serviceName, String methods, String packageName) {
        StringBuilder classContent = new StringBuilder();
        
        classContent.append("package ").append("com.api.automation").append(".tests.generated.services;\n\n");
        classContent.append("import com.api.automation.client.BaseApiClient;\n");
        classContent.append("import io.restassured.response.Response;\n");
        classContent.append("import org.slf4j.Logger;\n");
        classContent.append("import org.slf4j.LoggerFactory;\n\n");
        classContent.append("/**\n");
        classContent.append(" * Generated service class for ").append(serviceName).append("\n");
        classContent.append(" * Generated from Swagger/OpenAPI specification\n");
        classContent.append(" */\n");
        classContent.append("public class ").append(serviceName).append("Service extends BaseApiClient {\n");
        classContent.append("    private static final Logger logger = LoggerFactory.getLogger(").append(serviceName).append("Service.class);\n\n");
        classContent.append(methods);
        classContent.append("}\n");
        
        return classContent.toString();
    }
    
    /**
     * Generate service methods from path item
     */
    private static String generateServiceMethods(String path, PathItem pathItem, String packageName) {
        StringBuilder methods = new StringBuilder();
        
        if (pathItem.getGet() != null) {
            methods.append(generateServiceMethod("GET", path, pathItem.getGet()));
        }
        if (pathItem.getPost() != null) {
            methods.append(generateServiceMethod("POST", path, pathItem.getPost()));
        }
        if (pathItem.getPut() != null) {
            methods.append(generateServiceMethod("PUT", path, pathItem.getPut()));
        }
        if (pathItem.getDelete() != null) {
            methods.append(generateServiceMethod("DELETE", path, pathItem.getDelete()));
        }
        if (pathItem.getPatch() != null) {
            methods.append(generateServiceMethod("PATCH", path, pathItem.getPatch()));
        }
        
        return methods.toString();
    }
    
    /**
     * Generate individual service method
     */
    private static String generateServiceMethod(String httpMethod, String path, Operation operation) {
        StringBuilder method = new StringBuilder();
        
        String methodName = operation.getOperationId() != null ? 
            operation.getOperationId() : 
            httpMethod.toLowerCase() + path.replaceAll("[^a-zA-Z0-9]", "");
        
        method.append("    /**\n");
        method.append("     * ").append(operation.getSummary() != null ? operation.getSummary() : "Generated method").append("\n");
        method.append("     */\n");
        method.append("    public Response ").append(methodName).append("() {\n");
        method.append("        logger.info(\"Calling ").append(httpMethod).append(" ").append(path).append("\");\n");
        method.append("        return getRequestSpec()\n");
        method.append("                .when()\n");
        method.append("                .").append(httpMethod.toLowerCase()).append("(\"").append(path).append("\");\n");
        method.append("    }\n\n");
        
        return method.toString();
    }
    
    /**
     * Generate test class content
     */
    private static String generateTestClass(String className, String path, PathItem pathItem, String packageName) {
        StringBuilder classContent = new StringBuilder();
        
        classContent.append("package ").append("com.api.automation").append(".tests.generated.smoke;\n\n");
        classContent.append("import com.api.automation.tests.base.BaseTest;\n");
        classContent.append("import io.restassured.RestAssured;\n");
        classContent.append("import io.restassured.response.Response;\n");
        classContent.append("import org.junit.jupiter.api.DisplayName;\n");
        classContent.append("import org.junit.jupiter.api.Tag;\n");
        classContent.append("import org.junit.jupiter.api.Test;\n");
        classContent.append("import static org.hamcrest.Matchers.*;\n\n");
        classContent.append("/**\n");
        classContent.append(" * Generated test class for ").append(className).append("\n");
        classContent.append(" * Generated from Swagger/OpenAPI specification\n");
        classContent.append(" */\n");
        classContent.append("@Tag(\"smoke\")\n");
        classContent.append("@Tag(\"generated\")\n");
        classContent.append("public class ").append(className).append("Test extends BaseTest {\n\n");
        
        // Generate test methods
        if (pathItem.getGet() != null) {
            classContent.append(generateTestMethod("GET", path, pathItem.getGet()));
        }
        if (pathItem.getPost() != null) {
            classContent.append(generateTestMethod("POST", path, pathItem.getPost()));
        }
        if (pathItem.getPut() != null) {
            classContent.append(generateTestMethod("PUT", path, pathItem.getPut()));
        }
        if (pathItem.getDelete() != null) {
            classContent.append(generateTestMethod("DELETE", path, pathItem.getDelete()));
        }
        
        classContent.append("}\n");
        
        return classContent.toString();
    }
    
    /**
     * Generate individual test method
     */
    private static String generateTestMethod(String httpMethod, String path, Operation operation) {
        StringBuilder method = new StringBuilder();
        
        String methodName = "test" + capitalize(httpMethod) + path.replaceAll("[^a-zA-Z0-9]", "");
        String displayName = operation.getSummary() != null ? operation.getSummary() : httpMethod + " " + path;
        
        method.append("    @Test\n");
        method.append("    @DisplayName(\"").append(displayName).append("\")\n");
        method.append("    void ").append(methodName).append("() {\n");
        method.append("        logStep(\"Send ").append(httpMethod).append(" request to ").append(path).append("\");\n");
        method.append("        \n");
        method.append("        Response response = RestAssured.given()\n");
        method.append("                .when()\n");
        method.append("                .").append(httpMethod.toLowerCase()).append("(\"").append(path).append("\");\n");
        method.append("        \n");
        method.append("        logStep(\"Verify response\");\n");
        method.append("        response.then()\n");
        method.append("                .statusCode(200); // TODO: Add proper validations\n");
        method.append("        \n");
        method.append("        logVerification(\"").append(displayName).append(" test passed\");\n");
        method.append("    }\n\n");
        
        return method.toString();
    }
    
    /**
     * Append operation details to documentation
     */
    private static void appendOperation(StringBuilder docs, String method, Operation operation) {
        if (operation != null) {
            docs.append("#### ").append(method).append("\n");
            if (operation.getSummary() != null) {
                docs.append(operation.getSummary()).append("\n\n");
            }
            if (operation.getDescription() != null) {
                docs.append(operation.getDescription()).append("\n\n");
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
     * Capitalize first letter of string
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
