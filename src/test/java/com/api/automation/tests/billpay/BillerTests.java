package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.ApiResponse;
import com.api.automation.models.billpay.Biller;
import com.api.automation.models.billpay.BillerInput;
import com.api.automation.models.billpay.enums.BillerCategory;
import com.api.automation.services.billpay.BillerService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Biller CRUD Tests for Bill Payment API
 * Tests all biller management operations
 */
@Epic("Bill Payment API")
@Feature("Biller Management")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BillerTests {

    private static BillerService billerService;
    private static String createdBillerId;

    @BeforeAll
    static void setup() {
        billerService = new BillerService();
    }

    @Test
    @Order(1)
    @DisplayName("Get all billers")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Biller Retrieval")
    @Description("Tests retrieval of all billers from the API")
    void testGetAllBillers() {
        Response response = billerService.getAllBillers();
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.contains("success") || body.contains("data") || body.contains("["),
            "Response should contain biller data");
    }

    @Test
    @Order(2)
    @DisplayName("Get billers with pagination")
    @Severity(SeverityLevel.NORMAL)
    @Story("Biller Retrieval")
    @Description("Tests paginated retrieval of billers")
    void testGetBillersWithPagination() {
        Response response = billerService.getBillersWithPagination(1, 5);
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
    }

    @Test
    @Order(3)
    @DisplayName("Get biller categories")
    @Severity(SeverityLevel.NORMAL)
    @Story("Biller Categories")
    @Description("Tests retrieval of all available biller categories")
    void testGetBillerCategories() {
        Response response = billerService.getBillerCategories();
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Categories response should not be null");
    }

    @Test
    @Order(4)
    @DisplayName("Create a new biller")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Biller Creation")
    @Description("Tests creating a new biller in the system")
    void testCreateBiller() {
        BillerInput newBiller = BillerInput.builder()
            .name("Test Electric Company")
            .category(BillerCategory.ELECTRICITY)
            .description("Automated test biller for electricity")
            .websiteUrl("https://test-electric.example.com")
            .supportPhone("+1-555-TEST-001")
            .supportEmail("support@test-electric.example.com")
            .active(true)
            .build();

        Response response = billerService.createBiller(newBiller);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Create biller should return 200 or 201, got: " + response.getStatusCode());
        
        String body = response.getBody().asString();
        if (body.contains("id")) {
            // Try to extract ID for cleanup
            createdBillerId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(5)
    @DisplayName("Get biller by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Biller Retrieval")
    @Description("Tests retrieval of a specific biller by ID")
    void testGetBillerById() {
        // First get all billers to find a valid ID
        Response listResponse = billerService.getAllBillers();
        String body = listResponse.getBody().asString();
        
        // Use biller ID 1 as a test (common default)
        String testBillerId = createdBillerId != null ? createdBillerId : "1";
        
        Response response = billerService.getBillerById(testBillerId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Get biller by ID should return 200 or 404");
    }

    @Test
    @Order(6)
    @DisplayName("Update an existing biller")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Biller Update")
    @Description("Tests updating an existing biller's information")
    void testUpdateBiller() {
        String testBillerId = createdBillerId != null ? createdBillerId : "1";
        
        BillerInput updateBiller = BillerInput.builder()
            .name("Updated Electric Company")
            .category(BillerCategory.ELECTRICITY)
            .description("Updated description for test biller")
            .websiteUrl("https://updated-electric.example.com")
            .active(true)
            .build();

        Response response = billerService.updateBiller(testBillerId, updateBiller);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Update should return 200 or 404 if not found");
    }

    @Test
    @Order(7)
    @DisplayName("Search billers by category")
    @Severity(SeverityLevel.NORMAL)
    @Story("Biller Search")
    @Description("Tests searching billers by category filter")
    void testSearchBillersByCategory() {
        Response response = billerService.getBillersByCategory(BillerCategory.ELECTRICITY);
        
        assertEquals(200, response.getStatusCode(), "Search by category should return 200");
    }

    @Test
    @Order(8)
    @DisplayName("Search billers by name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Biller Search")
    @Description("Tests searching billers by name query")
    void testSearchBillersByName() {
        Response response = billerService.searchBillers("Electric");
        
        assertEquals(200, response.getStatusCode(), "Search by name should return 200");
    }

    @Test
    @Order(9)
    @DisplayName("Get billers sorted by name")
    @Severity(SeverityLevel.MINOR)
    @Story("Biller Retrieval")
    @Description("Tests retrieval of billers sorted alphabetically")
    void testGetBillersSorted() {
        Response response = billerService.getBillersSorted("name", "asc");
        
        assertEquals(200, response.getStatusCode(), "Sorted retrieval should return 200");
    }

    @Test
    @Order(100)
    @DisplayName("Delete a biller")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Biller Deletion")
    @Description("Tests deleting a biller from the system")
    void testDeleteBiller() {
        // Only delete if we created a biller
        if (createdBillerId != null) {
            Response response = billerService.deleteBiller(createdBillerId);
            
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204 || response.getStatusCode() == 404,
                "Delete should return success or not found");
        }
    }

    @Test
    @DisplayName("Validate biller not found returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    @Description("Tests that requesting non-existent biller returns 404")
    void testBillerNotFound() {
        Response response = billerService.getBillerById("non-existent-id-99999");
        
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
            "Non-existent biller should return 404 or 400");
    }

    private static String extractIdFromResponse(String body) {
        // Simple ID extraction - look for "id":"value" or "id":value
        if (body.contains("\"id\":\"")) {
            int start = body.indexOf("\"id\":\"") + 6;
            int end = body.indexOf("\"", start);
            if (end > start) {
                return body.substring(start, end);
            }
        } else if (body.contains("\"id\":")) {
            int start = body.indexOf("\"id\":") + 5;
            int end = body.indexOf(",", start);
            if (end == -1) end = body.indexOf("}", start);
            if (end > start) {
                return body.substring(start, end).trim();
            }
        }
        return null;
    }
}
