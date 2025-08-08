package com.api.automation.utils;

import com.api.automation.tests.base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced SwaggerImporter with smart file handling options
 */
@Tag("demo")
public class SmartSwaggerImporter extends BaseTest {

    @Test
    @DisplayName("Demo: Smart file handling options")
    void demoSmartFileHandling() {
        logStep("🧠 Smart SwaggerImporter - File Handling Options");
        
        // Option 1: Backup before regeneration
        logStep("💾 OPTION 1: Backup existing files before regeneration");
        demonstrateBackupOption();
        
        // Option 2: Incremental generation
        logStep("📈 OPTION 2: Incremental generation (add only new)");
        demonstrateIncrementalOption();
        
        // Option 3: Extension pattern setup
        logStep("🔧 OPTION 3: Auto-create extension templates");
        demonstrateExtensionOption();
        
        logVerification("✨ Smart file handling options demonstrated!");
    }
    
    private void demonstrateBackupOption() {
        logStep("   Creating backup workflow...");
        
        String sourceFile = "src/test/java/com/api/automation/tests/generated/services/UsersService.java";
        String backupDir = "backup/swagger-generated/" + getCurrentTimestamp();
        
        if (new File(sourceFile).exists()) {
            try {
                // Create backup directory
                Files.createDirectories(Paths.get(backupDir));
                
                // Copy file to backup
                String backupFile = backupDir + "/UsersService.java";
                Files.copy(Paths.get(sourceFile), Paths.get(backupFile), StandardCopyOption.REPLACE_EXISTING);
                
                logStep("   ✅ Backup created: " + backupFile);
                logStep("   💡 Now safe to regenerate - backup preserved");
                
                // Show backup usage
                logStep("   📖 Backup Usage:");
                logStep("   • Compare changes: diff backup/... generated/...");
                logStep("   • Restore if needed: cp backup/... generated/...");
                logStep("   • Track evolution: git add backup/...");
                
            } catch (Exception e) {
                logStep("   ❌ Backup failed: " + e.getMessage());
            }
        } else {
            logStep("   ⚠️ No existing file to backup");
        }
    }
    
    private void demonstrateIncrementalOption() {
        logStep("   Incremental generation concept...");
        logStep("   🎯 Idea: Only generate NEW endpoints, preserve existing");
        logStep("   ");
        logStep("   📋 How it would work:");
        logStep("   1. Parse existing generated files");
        logStep("   2. Compare with new Swagger spec");
        logStep("   3. Generate only NEW methods/classes");
        logStep("   4. Append to existing files (with markers)");
        logStep("   ");
        logStep("   ⚠️ Complexity: Method signature changes, deletions");
        logStep("   💡 Better approach: Extension pattern (next demo)");
    }
    
    private void demonstrateExtensionOption() {
        logStep("   Creating extension template...");
        
        // Create enhanced service template
        String enhancedServiceContent = generateEnhancedServiceTemplate();
        String enhancedServicePath = "src/test/java/com/api/automation/tests/custom/enhanced/EnhancedUsersService.java";
        
        try {
            Files.createDirectories(Paths.get("src/test/java/com/api/automation/tests/custom/enhanced"));
            Files.writeString(Paths.get(enhancedServicePath), enhancedServiceContent);
            
            logStep("   ✅ Created: " + enhancedServicePath);
            logStep("   🎯 Purpose: Custom methods safe from regeneration");
            
            // Create workflow manager template
            String workflowContent = generateWorkflowManagerTemplate();
            String workflowPath = "src/test/java/com/api/automation/tests/custom/workflows/UserWorkflowManager.java";
            
            Files.createDirectories(Paths.get("src/test/java/com/api/automation/tests/custom/workflows"));
            Files.writeString(Paths.get(workflowPath), workflowContent);
            
            logStep("   ✅ Created: " + workflowPath);
            logStep("   🎯 Purpose: Complex business logic workflows");
            
            logStep("   ");
            logStep("   🏗️ ARCHITECTURE BENEFITS:");
            logStep("   • Generated code: Pure, clean, auto-updated");
            logStep("   • Enhanced code: Custom logic, preserved");
            logStep("   • Workflow code: Business scenarios, safe");
            
        } catch (Exception e) {
            logStep("   ❌ Template creation failed: " + e.getMessage());
        }
    }
    
    private String generateEnhancedServiceTemplate() {
        return """
package com.api.automation.tests.custom.enhanced;

import com.api.automation.tests.generated.services.UsersService;
import com.api.automation.tests.generated.models.User;
import io.restassured.response.Response;
import java.util.List;

/**
 * Enhanced Users Service with custom business logic
 * Extends generated UsersService - safe from regeneration
 */
public class EnhancedUsersService extends UsersService {

    /**
     * Get only active users
     */
    public Response getActiveUsers() {
        logStep("Getting active users only");
        return getRequestSpec()
                .queryParam("status", "active")
                .when()
                .get("/users");
    }
    
    /**
     * Create user with validation
     */
    public Response createValidatedUser(User user) {
        logStep("Creating user with validation: " + user.getName());
        
        // Pre-validation
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        
        // Use parent method
        return super.createUser();
    }
    
    /**
     * Get users by role with retry logic
     */
    public Response getUsersByRole(String role, int maxRetries) {
        logStep("Getting users by role: " + role + " (max retries: " + maxRetries + ")");
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                Response response = getRequestSpec()
                        .queryParam("role", role)
                        .when()
                        .get("/users");
                
                if (response.getStatusCode() == 200) {
                    return response;
                }
                
                logStep("Attempt " + (i + 1) + " failed, retrying...");
                Thread.sleep(1000);
                
            } catch (Exception e) {
                logStep("Error on attempt " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + " attempts");
    }
    
    /**
     * Bulk operations with progress tracking
     */
    public Response bulkCreateUsersWithProgress(List<User> users) {
        logStep("Bulk creating " + users.size() + " users with progress tracking");
        
        for (int i = 0; i < users.size(); i++) {
            logStep("Progress: " + (i + 1) + "/" + users.size() + " users processed");
            // Custom logic here
        }
        
        return super.createUser(); // Use generated method
    }
}
""";
    }
    
    private String generateWorkflowManagerTemplate() {
        return """
package com.api.automation.tests.custom.workflows;

import com.api.automation.tests.custom.enhanced.EnhancedUsersService;
import com.api.automation.tests.generated.models.User;
import io.restassured.response.Response;

/**
 * User Workflow Manager for complex business scenarios
 * Orchestrates multiple API calls and business logic
 */
public class UserWorkflowManager {
    
    private final EnhancedUsersService usersService;
    
    public UserWorkflowManager() {
        this.usersService = new EnhancedUsersService();
    }
    
    /**
     * Complete user registration workflow
     */
    public Response completeUserRegistration(String name, String email, String role) {
        logStep("Starting complete user registration workflow");
        
        // Step 1: Create user
        User user = new User();
        user.setName(name);
        user.setActive(true);
        Response createResponse = usersService.createValidatedUser(user);
        
        if (createResponse.getStatusCode() != 201) {
            throw new RuntimeException("User creation failed");
        }
        
        // Step 2: Assign role (would use another generated service)
        Long userId = createResponse.jsonPath().getLong("id");
        logStep("User created with ID: " + userId);
        
        // Step 3: Send welcome email (would use notification service)
        logStep("Sending welcome email to: " + email);
        
        // Step 4: Return final status
        return usersService.getUserById();
    }
    
    /**
     * User cleanup workflow
     */
    public void cleanupTestUsers(String namePrefix) {
        logStep("Cleaning up test users with prefix: " + namePrefix);
        
        // Get all users
        Response allUsers = usersService.getAllUsers();
        
        // Filter and delete test users
        // Implementation would parse response and delete matching users
        logStep("Cleanup completed");
    }
    
    /**
     * User data integrity check
     */
    public boolean validateUserDataIntegrity(Long userId) {
        logStep("Validating data integrity for user: " + userId);
        
        Response userResponse = usersService.getUserById();
        
        // Custom validation logic
        boolean isValid = userResponse.getStatusCode() == 200;
        
        logStep("Data integrity check result: " + (isValid ? "PASSED" : "FAILED"));
        return isValid;
    }
    
    private void logStep(String message) {
        System.out.println("[UserWorkflow] " + message);
    }
}
""";
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }
}
