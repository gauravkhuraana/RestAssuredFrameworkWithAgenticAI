package com.api.automation.tests.petstore;

import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.SwaggerImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Quick test to regenerate PetStore code with correct package structure
 */
@Tag("petstore")
public class RegeneratePetStoreTest extends BaseTest {

    @Test
    @DisplayName("Regenerate PetStore code with correct package structure")
    void regeneratePetStoreCode() {
        logStep("🔄 Regenerating PetStore Swagger code with correct package structure");
        
        try {
            SwaggerImporter.importSwaggerSpec("https://petstore.swagger.io/v2/swagger.json", "com.api.automation");
            logVerification("✅ PetStore code regenerated successfully!");
            
        } catch (Exception e) {
            logger.error("Failed to regenerate PetStore code: {}", e.getMessage(), e);
            logStep("❌ Error: " + e.getMessage());
            throw e;
        }
    }
}
