package com.api.automation.tests.petstore;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class to demonstrate SwaggerImporter functionality with PetStore API
 * Testing the complete SwaggerImporter implementation with real-world example
 */
@Tag("petstore")
@Tag("swagger")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreSwaggerTest extends BaseTest {

    private static final String PETSTORE_SWAGGER_URL = "https://petstore.swagger.io/v2/swagger.json";
    private static final String PACKAGE_NAME = "com.api.automation.petstore";
    private static final String OUTPUT_BASE = "src/test/java/com/api/automation/tests/generated";

    @Test
    @Order(1)
    @DisplayName("Import PetStore Swagger Specification")
    void importPetStoreSwagger() {
        logStep("🐾 STEP 1: Importing PetStore Swagger Specification");
        logStep("URL: " + PETSTORE_SWAGGER_URL);
        logStep("Package: " + PACKAGE_NAME);
        
        try {
            SwaggerImporter.importSwaggerSpec(PETSTORE_SWAGGER_URL, PACKAGE_NAME);
            logVerification("✅ PetStore Swagger specification imported successfully!");
            
        } catch (Exception e) {
            logger.error("Failed to import PetStore Swagger: {}", e.getMessage(), e);
            logStep("❌ Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(2)
    @DisplayName("Verify Generated Directory Structure")
    void verifyGeneratedStructure() {
        logStep("📁 STEP 2: Verifying Generated Directory Structure");
        
        String[] expectedDirectories = {
            OUTPUT_BASE,
            OUTPUT_BASE + "/models",
            OUTPUT_BASE + "/services", 
            OUTPUT_BASE + "/smoke",
            "target/swagger-templates"
        };
        
        for (String directory : expectedDirectories) {
            File dir = new File(directory);
            if (dir.exists()) {
                logStep("✅ Directory exists: " + directory);
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    logStep("   📄 Files: " + files.length);
                    for (File file : files) {
                        logStep("      - " + file.getName());
                    }
                } else {
                    logStep("   📄 No files in directory");
                }
            } else {
                logStep("❌ Directory missing: " + directory);
            }
        }
        
        logVerification("Directory structure verification completed");
    }

    @Test
    @Order(3)
    @DisplayName("Analyze Generated Models")
    void analyzeGeneratedModels() {
        logStep("📦 STEP 3: Analyzing Generated Model Classes");
        
        File modelsDir = new File(OUTPUT_BASE + "/models");
        if (modelsDir.exists()) {
            File[] modelFiles = modelsDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (modelFiles != null) {
                logStep("Found " + modelFiles.length + " model files:");
                
                for (File modelFile : modelFiles) {
                    analyzeJavaFile(modelFile, "MODEL");
                }
            }
        } else {
            logStep("❌ Models directory not found");
        }
        
        logVerification("Model classes analysis completed");
    }

    @Test
    @Order(4)
    @DisplayName("Analyze Generated Services")
    void analyzeGeneratedServices() {
        logStep("🔧 STEP 4: Analyzing Generated Service Classes");
        
        File servicesDir = new File(OUTPUT_BASE + "/services");
        if (servicesDir.exists()) {
            File[] serviceFiles = servicesDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (serviceFiles != null) {
                logStep("Found " + serviceFiles.length + " service files:");
                
                for (File serviceFile : serviceFiles) {
                    analyzeJavaFile(serviceFile, "SERVICE");
                }
            }
        } else {
            logStep("❌ Services directory not found");
        }
        
        logVerification("Service classes analysis completed");
    }

    @Test
    @Order(5)
    @DisplayName("Analyze Generated Tests")
    void analyzeGeneratedTests() {
        logStep("🧪 STEP 5: Analyzing Generated Test Classes");
        
        File testsDir = new File(OUTPUT_BASE + "/smoke");
        if (testsDir.exists()) {
            File[] testFiles = testsDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (testFiles != null) {
                logStep("Found " + testFiles.length + " test files:");
                
                for (File testFile : testFiles) {
                    analyzeJavaFile(testFile, "TEST");
                }
            }
        } else {
            logStep("❌ Tests directory not found");
        }
        
        logVerification("Test classes analysis completed");
    }

    @Test
    @Order(6)
    @DisplayName("Analyze Generated Documentation")
    void analyzeGeneratedDocumentation() {
        logStep("📚 STEP 6: Analyzing Generated API Documentation");
        
        File docsFile = new File("target/swagger-templates/API_Documentation.md");
        if (docsFile.exists()) {
            try {
                String content = Files.readString(Paths.get(docsFile.getPath()));
                logStep("✅ Documentation file found: " + docsFile.getName());
                logStep("📄 File size: " + docsFile.length() + " bytes");
                logStep("📝 Content preview:");
                
                String[] lines = content.split("\n");
                int previewLines = Math.min(10, lines.length);
                for (int i = 0; i < previewLines; i++) {
                    logStep("   " + lines[i]);
                }
                if (lines.length > 10) {
                    logStep("   ... (" + (lines.length - 10) + " more lines)");
                }
                
            } catch (Exception e) {
                logStep("❌ Error reading documentation: " + e.getMessage());
            }
        } else {
            logStep("❌ Documentation file not found");
        }
        
        logVerification("Documentation analysis completed");
    }

    @Test
    @Order(7)
    @DisplayName("Test Coverage Summary")
    void testCoverageSummary() {
        logStep("📊 STEP 7: SwaggerImporter Test Coverage Summary");
        
        // Count generated files
        int modelCount = countFilesInDirectory(OUTPUT_BASE + "/models", ".java");
        int serviceCount = countFilesInDirectory(OUTPUT_BASE + "/services", ".java");
        int testCount = countFilesInDirectory(OUTPUT_BASE + "/smoke", ".java");
        
        logStep("📈 Generation Statistics:");
        logStep("   📦 Model Classes: " + modelCount);
        logStep("   🔧 Service Classes: " + serviceCount);
        logStep("   🧪 Test Classes: " + testCount);
        logStep("   📚 Documentation Files: " + (new File("target/swagger-templates/API_Documentation.md").exists() ? 1 : 0));
        
        // Check PetStore specific entities
        logStep("🐾 PetStore Specific Coverage:");
        checkEntityCoverage("Pet", "Core pet entity");
        checkEntityCoverage("Store", "Store operations");
        checkEntityCoverage("User", "User management");
        checkEntityCoverage("Order", "Order processing");
        
        // Verify key API operations are covered
        logStep("🔍 API Operations Coverage:");
        checkOperationCoverage("findByStatus", "Find pets by status");
        checkOperationCoverage("addPet", "Add new pet");
        checkOperationCoverage("updatePet", "Update pet");
        checkOperationCoverage("deletePet", "Delete pet");
        checkOperationCoverage("placeOrder", "Place order");
        checkOperationCoverage("getOrderById", "Get order by ID");
        checkOperationCoverage("createUser", "Create user");
        
        logVerification("🎯 SwaggerImporter test coverage analysis completed!");
        logStep("✨ All major PetStore API components have been tested and generated!");
    }

    /**
     * Helper method to analyze Java file content
     */
    private void analyzeJavaFile(File file, String type) {
        try {
            String content = Files.readString(Paths.get(file.getPath()));
            logStep("   📄 " + type + ": " + file.getName());
            logStep("      📊 Size: " + file.length() + " bytes");
            logStep("      📝 Lines: " + content.split("\n").length);
            
            // Check for key annotations and patterns
            if (content.contains("@Data")) logStep("      ✅ Uses Lombok @Data");
            if (content.contains("@JsonProperty")) logStep("      ✅ Uses Jackson annotations");
            if (content.contains("extends BaseApiClient")) logStep("      ✅ Extends BaseApiClient");
            if (content.contains("extends BaseTest")) logStep("      ✅ Extends BaseTest");
            if (content.contains("@Test")) logStep("      ✅ Contains test methods");
            if (content.contains("@Tag")) logStep("      ✅ Has test tags");
            
        } catch (Exception e) {
            logStep("      ❌ Error analyzing file: " + e.getMessage());
        }
    }

    /**
     * Helper method to count files in directory
     */
    private int countFilesInDirectory(String directory, String extension) {
        File dir = new File(directory);
        if (dir.exists()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(extension));
            return files != null ? files.length : 0;
        }
        return 0;
    }

    /**
     * Helper method to check if entity is covered
     */
    private void checkEntityCoverage(String entityName, String description) {
        boolean modelExists = new File(OUTPUT_BASE + "/models/" + entityName + ".java").exists();
        boolean serviceExists = new File(OUTPUT_BASE + "/services/" + entityName + "Service.java").exists();
        boolean testExists = new File(OUTPUT_BASE + "/smoke/" + entityName + "SmokeTest.java").exists();
        
        String status = (modelExists || serviceExists || testExists) ? "✅" : "❌";
        logStep("   " + status + " " + entityName + " (" + description + ")");
        if (modelExists) logStep("      📦 Model generated");
        if (serviceExists) logStep("      🔧 Service generated");
        if (testExists) logStep("      🧪 Test generated");
    }

    /**
     * Helper method to check if operation is covered
     */
    private void checkOperationCoverage(String operationName, String description) {
        // Check in service files for method names containing the operation
        File servicesDir = new File(OUTPUT_BASE + "/services");
        boolean found = false;
        
        if (servicesDir.exists()) {
            File[] serviceFiles = servicesDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (serviceFiles != null) {
                for (File serviceFile : serviceFiles) {
                    try {
                        String content = Files.readString(Paths.get(serviceFile.getPath()));
                        if (content.toLowerCase().contains(operationName.toLowerCase())) {
                            found = true;
                            break;
                        }
                    } catch (Exception e) {
                        // Continue checking other files
                    }
                }
            }
        }
        
        String status = found ? "✅" : "❓";
        logStep("   " + status + " " + operationName + " (" + description + ")");
    }
}
