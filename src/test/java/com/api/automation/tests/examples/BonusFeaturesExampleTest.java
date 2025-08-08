package com.api.automation.tests.examples;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

/**
 * Example test class demonstrating the new bonus features:
 * - Swagger/OpenAPI import
 * - Postman collection import
 * - cURL generation
 * - HAR file generation
 * - Debug utilities
 */
@Tag("examples")
@Tag("bonus-features")
public class BonusFeaturesExampleTest extends BaseTest {
    
    private CurlGenerator curlGenerator;
    private HarGenerator harGenerator;
    
    @BeforeEach
    void setUpBonusFeatures() {
        // Initialize debug utilities
        curlGenerator = new CurlGenerator();
        harGenerator = new HarGenerator();
        
        // Add generators as filters to capture requests
        RestAssured.filters(curlGenerator, harGenerator);
    }
    
    @AfterEach
    void tearDownBonusFeatures() {
        // Disable debug mode and generate session summary
        harGenerator.disable();
        curlGenerator.disable();
        DebugUtils.disableDebug();
        DebugUtils.generateSessionSummary();
    }
    
    @Test
    @DisplayName("Example: Generate cURL and HAR for failed request")
    void testCurlAndHarGeneration() {
        // Enable debug mode for this test
        DebugUtils.enableDebugForTest("testCurlAndHarGeneration");
        curlGenerator.enableForTest("testCurlAndHarGeneration");
        harGenerator.enableForTest("testCurlAndHarGeneration");
        
        logStep("Send request that might fail to demonstrate debug utilities");
        
        try {
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body("{\"title\": \"Test Post\", \"body\": \"This is a test\", \"userId\": 1}")
                    .when()
                    .post("/posts");
            
            logStep("Verify response (this will generate debug artifacts if it fails)");
            response.then()
                    .statusCode(201); // This should pass for JSONPlaceholder
                    
            logVerification("Request succeeded - debug artifacts generated for demonstration");
            
        } catch (AssertionError e) {
            // If the test fails, debug artifacts will be automatically generated
            logFailure("Test failed - check debug artifacts in target/debug-output/", e);
            throw e;
        }
    }
    
    @Test
    @DisplayName("Example: Import Swagger specification")
    @Disabled("Enable this test when you have a Swagger URL to import")
    void testSwaggerImport() {
        logStep("Import Swagger/OpenAPI specification");
        
        // Example: Import from JSONPlaceholder Swagger (if it existed)
        // SwaggerImporter.importSwaggerSpec("https://jsonplaceholder.typicode.com/swagger.json", "com.api.automation");
        
        // Example: Import from local file
        // SwaggerImporter.importSwaggerFile("src/test/resources/api-spec.json", "com.api.automation");
        
        logVerification("Swagger import would generate test templates in src/test/java/com/api/automation/tests/generated/");
    }
    
    @Test
    @DisplayName("Example: Import Postman collection")
    @Disabled("Enable this test when you have a Postman collection to import")
    void testPostmanImport() {
        logStep("Import Postman collection");
        
        // Example: Import from Postman collection URL
        // PostmanImporter.importPostmanCollection("https://api.postman.com/collections/your-collection-id", "com.api.automation");
        
        // Example: Import from local file
        // PostmanImporter.importPostmanFile("src/test/resources/postman-collection.json", "com.api.automation");
        
        logVerification("Postman import would generate test templates in src/test/java/com/api/automation/tests/generated/");
    }
    
    @Test
    @DisplayName("Example: Manual cURL generation")
    void testManualCurlGeneration() {
        logStep("Generate cURL command manually");
        
        // Create a sample request and get response
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer sample-token")
                .body("{\"title\": \"Sample Post\"}")
                .when()
                .post("/posts");
        
        // Verify the response was successful
        response.then().statusCode(201);
        
        // Generate cURL command manually
        String curlCommand = CurlGenerator.generateSimpleCurl(
            "POST", 
            "https://jsonplaceholder.typicode.com/posts",
            java.util.Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer sample-token"
            ),
            "{\"title\": \"Sample Post\"}"
        );
        
        logStep("Generated cURL command: " + curlCommand);
        logVerification("cURL command generated successfully");
    }
    
    @Test
    @DisplayName("Example: Debug utilities for troubleshooting")
    void testDebugUtilities() {
        logStep("Demonstrate debug utilities for troubleshooting");
        
        // Enable debug mode
        DebugUtils.enableDebugForTest("testDebugUtilities");
        
        // Make a request that might need debugging
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/posts/1");
        
        // Verify response
        response.then()
                .statusCode(200)
                .body("id", org.hamcrest.Matchers.equalTo(1));
        
        logVerification("Debug utilities are ready to capture failed requests automatically");
    }
    
    @Test
    @DisplayName("Example: Complex JSON validation with JSONPath")
    void testJsonPathValidation() {
        logStep("Demonstrate complex JSON validation using JSONPath utilities");
        
        Response response = RestAssured.given()
                .when()
                .get("/posts");
        
        String responseBody = response.getBody().asString();
        
        // Use JSONPath utilities for complex validations
        logStep("Validate response structure using JSONPath");
        
        // Check if specific paths exist
        Assertions.assertTrue(JsonPathUtils.pathExists(responseBody, "[0].id"), 
            "First post should have an ID");
        
        // Extract and validate specific values
        Integer firstPostId = JsonPathUtils.extractInt(responseBody, "[0].id");
        Assertions.assertNotNull(firstPostId, "First post ID should not be null");
        
        // Count elements
        int totalPosts = JsonPathUtils.countElements(responseBody, "");
        Assertions.assertTrue(totalPosts > 0, "Should have at least one post");
        
        logVerification("JSONPath validation completed successfully with " + totalPosts + " posts found");
    }
    
    @Test
    @DisplayName("Example: Data-driven testing with CSV/Excel")
    void testDataDrivenFeatures() {
        logStep("Demonstrate data-driven testing capabilities");
        
        // Example of reading test data (these files would need to exist)
        try {
            // Read CSV data (if file exists)
            // List<Map<String, String>> csvData = TestDataUtils.readCsvTestData("test-data.csv");
            // logStep("CSV data would be loaded for data-driven tests");
            
            // Read Excel data (if file exists)  
            // List<Map<String, Object>> excelData = TestDataUtils.readExcelTestData("test-data.xlsx");
            // logStep("Excel data would be loaded for data-driven tests");
            
            // Generate random test data
            java.util.Map<String, Object> randomData = TestDataUtils.generateRandomTestData();
            logStep("Generated random test data: " + randomData);
            
            logVerification("Data-driven testing capabilities are available");
            
        } catch (Exception e) {
            logStep("Test data files not found - this is expected in the example");
            logVerification("Data-driven testing utilities are ready for use when test data files are provided");
        }
    }
}
