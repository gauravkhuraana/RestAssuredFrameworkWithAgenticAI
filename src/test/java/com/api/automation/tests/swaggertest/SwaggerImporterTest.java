package com.api.automation.tests.swaggertest;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Test class to demonstrate SwaggerImporter functionality
 */
@Tag("swagger")
public class SwaggerImporterTest extends BaseTest {

    @Test
    @DisplayName("Test SwaggerImporter with Local Sample File")
    void testSwaggerImportWithLocalFile() {
        logStep("Import Swagger specification from local sample file");
        
        String filePath = "sample-swagger.json";
        String packageName = "com.api.automation";
        
        try {
            // Check if file exists
            File swaggerFile = new File(filePath);
            if (!swaggerFile.exists()) {
                logStep("Sample swagger file not found at: " + filePath);
                return;
            }
            
            SwaggerImporter.importSwaggerFile(filePath, packageName);
            logVerification("Local Swagger specification imported successfully");
            
            // Check generated files
            logStep("Checking generated files...");
            checkGeneratedFiles();
            
        } catch (Exception e) {
            logger.error("Failed to import local Swagger file: {}", e.getMessage());
            logStep("Error importing local Swagger file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test SwaggerImporter with PetStore API")
    void testWithPetStoreAPI() {
        logStep("Import Swagger specification from PetStore API");
        
        // Using the official Swagger PetStore API (v3)
        String swaggerUrl = "https://petstore3.swagger.io/api/v3/openapi.json";
        String packageName = "com.api.automation";
        
        try {
            SwaggerImporter.importSwaggerSpec(swaggerUrl, packageName);
            logVerification("PetStore Swagger specification imported successfully");
            
            // Check generated files
            logStep("Checking generated files...");
            checkGeneratedFiles();
            
        } catch (Exception e) {
            logger.error("Failed to import PetStore API: {}", e.getMessage());
            logStep("Error importing PetStore API: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test SwaggerImporter with HTTPBin API")
    void testWithHTTPBinAPI() {
        logStep("Import Swagger specification from HTTPBin API");
        
        // Using HTTPBin OpenAPI spec
        String swaggerUrl = "https://httpbin.org/spec.json";
        String packageName = "com.api.automation";
        
        try {
            SwaggerImporter.importSwaggerSpec(swaggerUrl, packageName);
            logVerification("HTTPBin Swagger specification imported successfully");
            
            // Check generated files
            logStep("Checking generated files...");
            checkGeneratedFiles();
            
        } catch (Exception e) {
            logger.error("Failed to import HTTPBin API: {}", e.getMessage());
            logStep("Error importing HTTPBin API: " + e.getMessage());
        }
    }
    
    private void checkGeneratedFiles() {
        // Check if directories were created
        String[] directories = {
            "src/test/java/com/api/automation/tests/generated",
            "src/test/java/com/api/automation/tests/generated/models",
            "src/test/java/com/api/automation/tests/generated/services",
            "src/test/java/com/api/automation/tests/generated/smoke",
            "target/swagger-templates"
        };
        
        for (String dir : directories) {
            File directory = new File(dir);
            if (directory.exists()) {
                logStep("✓ Directory created: " + dir);
                // List files in directory
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        logStep("  - Generated file: " + file.getName());
                    }
                } else {
                    logStep("  - Directory is empty");
                }
            } else {
                logStep("✗ Directory not found: " + dir);
            }
        }
    }
}
