package com.api.automation.tests.examples;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Demonstration of what happens when SwaggerImporter encounters existing files
 */
@Tag("demo")
public class SwaggerOverwriteDemo extends BaseTest {

    @Test
    @DisplayName("Demo: SwaggerImporter file overwrite behavior")
    void demoFileOverwriteBehavior() {
        logStep("🔍 SCENARIO: What happens when files already exist?");
        
        // STEP 1: Show current situation
        logStep("📋 STEP 1: Check existing generated files");
        checkExistingFiles();
        
        // STEP 2: Modify an existing file to show overwrite
        logStep("✏️ STEP 2: Modify existing file (simulate custom changes)");
        modifyExistingFile();
        
        // STEP 3: Show the modification
        logStep("👀 STEP 3: Show our custom modification");
        showFileModification();
        
        // STEP 4: Run SwaggerImporter again
        logStep("⚙️ STEP 4: Run SwaggerImporter again (same spec)");
        try {
            SwaggerImporter.importSwaggerFile("sample-swagger.json", "com.api.automation");
            logStep("   ✅ SwaggerImporter completed");
        } catch (Exception e) {
            logStep("   ❌ Error: " + e.getMessage());
            return;
        }
        
        // STEP 5: Check what happened to our modification
        logStep("🕵️ STEP 5: Check if our custom changes survived");
        checkIfModificationSurvived();
        
        // STEP 6: Explain the behavior
        logStep("📖 STEP 6: Understanding the behavior");
        explainOverwriteBehavior();
        
        logVerification("🎯 File overwrite behavior demonstration completed!");
    }
    
    private void checkExistingFiles() {
        String[] filesToCheck = {
            "src/test/java/com/api/automation/tests/generated/services/UsersService.java",
            "src/test/java/com/api/automation/tests/generated/models/User.java",
            "src/test/java/com/api/automation/tests/generated/smoke/UsersSmokeTest.java"
        };
        
        for (String filePath : filesToCheck) {
            File file = new File(filePath);
            if (file.exists()) {
                logStep("   ✅ Found: " + file.getName() + " (" + file.length() + " bytes)");
            } else {
                logStep("   ❌ Missing: " + file.getName());
            }
        }
    }
    
    private void modifyExistingFile() {
        String filePath = "src/test/java/com/api/automation/tests/generated/services/UsersService.java";
        File file = new File(filePath);
        
        if (file.exists()) {
            try {
                String content = Files.readString(Paths.get(filePath));
                String modifiedContent = content.replace(
                    "public class UsersService extends BaseApiClient {",
                    "public class UsersService extends BaseApiClient {\n    \n    // CUSTOM MODIFICATION: Added by user\n    private boolean customFlag = true;"
                );
                Files.writeString(Paths.get(filePath), modifiedContent);
                logStep("   ✅ Added custom modification to UsersService.java");
            } catch (Exception e) {
                logStep("   ❌ Failed to modify file: " + e.getMessage());
            }
        } else {
            logStep("   ⚠️ File doesn't exist to modify");
        }
    }
    
    private void showFileModification() {
        String filePath = "src/test/java/com/api/automation/tests/generated/services/UsersService.java";
        try {
            String content = Files.readString(Paths.get(filePath));
            if (content.contains("CUSTOM MODIFICATION")) {
                logStep("   ✅ Custom modification is present in the file");
                logStep("   📝 Added: private boolean customFlag = true;");
            } else {
                logStep("   ❌ Custom modification not found");
            }
        } catch (Exception e) {
            logStep("   ❌ Error reading file: " + e.getMessage());
        }
    }
    
    private void checkIfModificationSurvived() {
        String filePath = "src/test/java/com/api/automation/tests/generated/services/UsersService.java";
        try {
            String content = Files.readString(Paths.get(filePath));
            if (content.contains("CUSTOM MODIFICATION")) {
                logStep("   ✅ Custom modification SURVIVED regeneration");
                logStep("   💡 This means SwaggerImporter preserves existing files");
            } else {
                logStep("   ❌ Custom modification was OVERWRITTEN");
                logStep("   ⚠️ This means SwaggerImporter replaces existing files");
            }
        } catch (Exception e) {
            logStep("   ❌ Error checking file: " + e.getMessage());
        }
    }
    
    private void explainOverwriteBehavior() {
        logStep("   📚 CURRENT BEHAVIOR:");
        logStep("   • SwaggerImporter uses 'new FileWriter(file)' mode");
        logStep("   • This OVERWRITES existing files completely");
        logStep("   • Any custom modifications are LOST");
        logStep("   ");
        logStep("   🎯 IMPLICATIONS:");
        logStep("   • ✅ Always generates fresh, clean code");
        logStep("   • ❌ Loses any manual customizations");
        logStep("   • ⚠️ Need strategies to preserve custom code");
        logStep("   ");
        logStep("   💡 SOLUTIONS:");
        logStep("   • Extend generated classes instead of modifying them");
        logStep("   • Use composition over modification");
        logStep("   • Keep custom code in separate files");
        logStep("   • Version control generated files to track changes");
    }
}
