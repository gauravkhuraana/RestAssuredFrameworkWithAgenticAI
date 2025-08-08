package com.api.automation.tests.examples;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Simple demonstration of SwaggerImporter capabilities
 */
@Tag("demo")
public class SwaggerImporterDemo extends BaseTest {

    @Test
    @DisplayName("Complete SwaggerImporter demonstration")
    void completeSwaggerImporterDemo() {
        logStep("🚀 SwaggerImporter Complete Demonstration");
        
        // STEP 1: Show what we're starting with
        logStep("📋 STEP 1: What is SwaggerImporter?");
        logStep("   SwaggerImporter reads API specifications and generates:");
        logStep("   • Model classes (data objects)");
        logStep("   • Service classes (API client methods)");
        logStep("   • Test templates (smoke tests)");
        logStep("   • Documentation (API reference)");
        
        // STEP 2: Show the input
        logStep("📄 STEP 2: Input - Our Swagger specification");
        File swaggerFile = new File("sample-swagger.json");
        if (swaggerFile.exists()) {
            logStep("   ✅ Found sample-swagger.json");
            logStep("   📊 File size: " + swaggerFile.length() + " bytes");
            logStep("   📝 Contains: User API with CRUD operations");
        } else {
            logStep("   ❌ sample-swagger.json not found");
        }
        
        // STEP 3: Generate the code
        logStep("⚙️ STEP 3: Code Generation in Action");
        try {
            SwaggerImporter.importSwaggerFile("sample-swagger.json", "com.api.automation");
            logStep("   ✅ Code generation completed successfully!");
        } catch (Exception e) {
            logStep("   ❌ Error: " + e.getMessage());
            return;
        }
        
        // STEP 4: Show what was generated
        logStep("📁 STEP 4: Generated Files Overview");
        checkAndShowGeneratedFiles();
        
        // STEP 5: Show practical usage
        logStep("💡 STEP 5: How to use the generated code");
        logStep("   🔧 Service Class Usage:");
        logStep("      UsersService service = new UsersService();");
        logStep("      Response response = service.getAllUsers();");
        logStep("   ");
        logStep("   📦 Model Class Usage:");
        logStep("      User user = new User();");
        logStep("      user.setName(\"John Doe\");");
        logStep("   ");
        logStep("   🧪 Test Class Usage:");
        logStep("      mvn test -Dtest=UsersSmokeTest");
        
        // STEP 6: Benefits summary
        logStep("🎯 STEP 6: Benefits of Using SwaggerImporter");
        logStep("   ⚡ Speed: Generates hundreds of lines of code instantly");
        logStep("   🎯 Accuracy: Code matches API specification exactly");
        logStep("   🔄 Consistency: All generated code follows same patterns");
        logStep("   🛠️ Maintainability: Easy to regenerate when API changes");
        logStep("   📚 Documentation: Auto-generates API reference");
        
        logVerification("🎉 SwaggerImporter demonstration completed successfully!");
        logStep("💼 You're now ready to use SwaggerImporter in your projects!");
    }
    
    private void checkAndShowGeneratedFiles() {
        String[] directories = {
            "src/test/java/com/api/automation/tests/generated/models",
            "src/test/java/com/api/automation/tests/generated/services", 
            "src/test/java/com/api/automation/tests/generated/smoke",
            "target/swagger-templates"
        };
        
        for (String dir : directories) {
            File directory = new File(dir);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    String folderType = dir.contains("models") ? "📦 Models" : 
                                      dir.contains("services") ? "🔧 Services" :
                                      dir.contains("smoke") ? "🧪 Tests" : "📖 Docs";
                    logStep("   " + folderType + ":");
                    for (File file : files) {
                        logStep("      ✅ " + file.getName());
                    }
                }
            }
        }
    }
}
