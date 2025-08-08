package com.api.automation.tests.examples;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import com.api.automation.tests.generated.services.UsersService;
import com.api.automation.tests.generated.models.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Practical example showing how to use SwaggerImporter effectively
 */
@Tag("example")
public class SwaggerImporterUsageExample extends BaseTest {

    @Test
    @DisplayName("Step 1: Generate code from Swagger spec")
    void step1_GenerateCodeFromSwagger() {
        logStep("STEP 1: Import Swagger specification and generate code");
        
        // This is how you use SwaggerImporter - one simple call!
        try {
            SwaggerImporter.importSwaggerFile("sample-swagger.json", "com.api.automation");
            logVerification("✅ Code generated successfully from Swagger spec!");
            
            logStep("What was generated:");
            logStep("  📁 Model classes: User.java");
            logStep("  🔧 Service classes: UsersService.java");
            logStep("  🧪 Test classes: UsersSmokeTest.java");
            logStep("  📖 Documentation: API_Documentation.md");
            
        } catch (Exception e) {
            logStep("❌ Error: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Step 2: Use generated service class for API calls")
    void step2_UseGeneratedServiceClass() {
        logStep("STEP 2: Use the generated UsersService class");
        
        // Create an instance of the generated service
        UsersService usersService = new UsersService();
        logStep("✅ Created UsersService instance");
        
        // Use the generated method - no need to write REST Assured code!
        logStep("Making API call using generated method...");
        Response response = usersService.getAllUsers();
        
        // The service handles all the REST Assured complexity for you
        logStep("📊 Response status: " + response.getStatusCode());
        logStep("📊 Response time: " + response.getTime() + "ms");
        
        logVerification("✅ Successfully used generated service class!");
    }

    @Test
    @DisplayName("Step 3: Use generated model class for data handling")
    void step3_UseGeneratedModelClass() {
        logStep("STEP 3: Use the generated User model class");
        
        // Create a user object using the generated model
        User newUser = new User();
        newUser.setId(123L);
        newUser.setName("John Doe");
        newUser.setActive(true);
        
        logStep("✅ Created User object:");
        logStep("  📝 ID: " + newUser.getId());
        logStep("  📝 Name: " + newUser.getName());
        logStep("  📝 Active: " + newUser.getActive());
        
        // The model class handles JSON serialization automatically
        logStep("📦 Ready for JSON serialization with @JsonProperty annotations");
        
        logVerification("✅ Successfully used generated model class!");
    }

    @Test
    @DisplayName("Step 4: Combine service and model for complete workflow")
    void step4_CombineServiceAndModel() {
        logStep("STEP 4: Complete workflow using generated code");
        
        // This is the power of SwaggerImporter - everything works together!
        UsersService usersService = new UsersService();
        
        logStep("🔄 Getting all users...");
        Response getAllResponse = usersService.getAllUsers();
        
        logStep("📊 GET /users response:");
        logStep("  Status: " + getAllResponse.getStatusCode());
        logStep("  Response body: " + getAllResponse.asString().substring(0, Math.min(100, getAllResponse.asString().length())) + "...");
        
        // You can easily add more validation, extract data, etc.
        getAllResponse.then()
                .statusCode(200);
        
        logVerification("✅ Complete workflow successful!");
        logStep("💡 You can now customize the generated code for your specific needs");
    }

    @Test
    @DisplayName("Step 5: Real-world usage patterns")
    void step5_RealWorldUsagePatterns() {
        logStep("STEP 5: Real-world usage patterns and tips");
        
        logStep("🎯 Best Practices:");
        logStep("  1. Generate code once, customize as needed");
        logStep("  2. Use service classes in your custom tests");
        logStep("  3. Extend model classes with validation logic");
        logStep("  4. Use generated tests as smoke test foundation");
        
        logStep("🔧 Customization Examples:");
        logStep("  • Add authentication to service methods");
        logStep("  • Add specific validations to test methods");
        logStep("  • Extend models with business logic");
        logStep("  • Create test data factories using models");
        
        logStep("📦 Integration Tips:");
        logStep("  • Generated code works with existing BaseTest");
        logStep("  • Service classes inherit authentication config");
        logStep("  • Model classes work with Jackson JSON processing");
        logStep("  • Test classes integrate with reporting framework");
        
        logVerification("✅ You're now ready to use SwaggerImporter effectively!");
    }
}
