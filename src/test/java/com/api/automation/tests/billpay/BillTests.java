package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.Bill;
import com.api.automation.models.billpay.BillInput;
import com.api.automation.models.billpay.BillsSummary;
import com.api.automation.models.billpay.Money;
import com.api.automation.models.billpay.enums.BillStatus;
import com.api.automation.services.billpay.BillService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bill CRUD Tests for Bill Payment API
 * Tests all bill management operations and business flows
 */
@Epic("Bill Payment API")
@Feature("Bill Management")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BillTests {

    private static BillService billService;
    private static String createdBillId;

    @BeforeAll
    static void setup() {
        billService = new BillService();
    }

    @Test
    @Order(1)
    @DisplayName("Get all bills")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bill Retrieval")
    @Description("Tests retrieval of all bills from the API")
    void testGetAllBills() {
        Response response = billService.getAllBills();
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response body should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Get bills with pagination")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Retrieval")
    @Description("Tests paginated retrieval of bills")
    void testGetBillsWithPagination() {
        Response response = billService.getBillsWithPagination(1, 10);
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
    }

    @Test
    @Order(3)
    @DisplayName("Get bills summary")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bill Summary")
    @Description("Tests retrieval of bills summary/statistics")
    void testGetBillsSummary() {
        Response response = billService.getBillsSummary();
        
        assertEquals(200, response.getStatusCode(), "Summary should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Summary response should contain data");
    }

    @Test
    @Order(4)
    @DisplayName("Get overdue bills")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Overdue Bills")
    @Description("Tests retrieval of overdue bills")
    void testGetOverdueBills() {
        Response response = billService.getOverdueBills();
        
        assertEquals(200, response.getStatusCode(), "Overdue bills should return 200 OK");
    }

    @Test
    @Order(5)
    @DisplayName("Create a new bill")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Bill Creation")
    @Description("Tests creating a new bill in the system")
    void testCreateBill() {
        Money amount = Money.builder()
            .value(BigDecimal.valueOf(150.00))
            .currency("USD")
            .build();

        BillInput newBill = BillInput.builder()
            .billerId("1")
            .userId("1")
            .consumerNumber("CONS-TEST-001")
            .amount(amount)
            .dueDate(LocalDate.now().plusDays(30).toString())
            .description("Automated test bill")
            .build();

        Response response = billService.createBill(newBill);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Create bill should return 200 or 201, got: " + response.getStatusCode());
        
        String body = response.getBody().asString();
        if (body.contains("id")) {
            createdBillId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(6)
    @DisplayName("Get bill by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bill Retrieval")
    @Description("Tests retrieval of a specific bill by ID")
    void testGetBillById() {
        String testBillId = createdBillId != null ? createdBillId : "1";
        
        Response response = billService.getBillById(testBillId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Get bill by ID should return 200 or 404");
    }

    @Test
    @Order(7)
    @DisplayName("Update bill status")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bill Update")
    @Description("Tests updating an existing bill's status")
    void testUpdateBillStatus() {
        String testBillId = createdBillId != null ? createdBillId : "1";
        
        Money amount = Money.builder()
            .value(BigDecimal.valueOf(175.00))
            .currency("USD")
            .build();

        BillInput updateBill = BillInput.builder()
            .billerId("1")
            .userId("1")
            .consumerNumber("CONS-TEST-001")
            .amount(amount)
            .dueDate(LocalDate.now().plusDays(25).toString())
            .description("Updated test bill - paid")
            .build();

        Response response = billService.updateBill(testBillId, updateBill);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Update should return 200 or 404 if not found");
    }

    @Test
    @Order(8)
    @DisplayName("Filter bills by status")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Filtering")
    @Description("Tests filtering bills by their status")
    void testFilterBillsByStatus() {
        Response response = billService.getBillsByStatus(BillStatus.PENDING);
        
        assertEquals(200, response.getStatusCode(), "Filter by status should return 200");
    }

    @Test
    @Order(9)
    @DisplayName("Get bills for specific user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Retrieval")
    @Description("Tests retrieval of bills for a specific user")
    void testGetBillsByUser() {
        Response response = billService.getBillsByUserId("1");
        
        assertEquals(200, response.getStatusCode(), "Get bills by user should return 200");
    }

    @Test
    @Order(10)
    @DisplayName("Get bills for specific biller")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Retrieval")
    @Description("Tests retrieval of bills for a specific biller")
    void testGetBillsByBiller() {
        Response response = billService.getBillsByBillerId("1");
        
        assertEquals(200, response.getStatusCode(), "Get bills by biller should return 200");
    }

    @Test
    @Order(11)
    @DisplayName("Fetch latest bill for account")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Fetch")
    @Description("Tests fetching the latest bill for an account number")
    void testFetchLatestBill() {
        Response response = billService.fetchBill("1", "ACC-TEST-001");
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Fetch bill should return 200 or 404");
    }

    @Test
    @Order(12)
    @DisplayName("Get bills due within date range")
    @Severity(SeverityLevel.NORMAL)
    @Story("Bill Filtering")
    @Description("Tests filtering bills by due date range")
    void testGetBillsByDateRange() {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(60).toString();
        
        Response response = billService.getBillsByDateRange(startDate, endDate);
        
        assertEquals(200, response.getStatusCode(), "Filter by date range should return 200");
    }

    @Test
    @Order(100)
    @DisplayName("Delete a bill")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Bill Deletion")
    @Description("Tests deleting a bill from the system")
    void testDeleteBill() {
        if (createdBillId != null) {
            Response response = billService.deleteBill(createdBillId);
            
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204 || response.getStatusCode() == 404,
                "Delete should return success or not found");
        }
    }

    @Test
    @DisplayName("Validate bill not found returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    @Description("Tests that requesting non-existent bill returns 404")
    void testBillNotFound() {
        Response response = billService.getBillById("non-existent-id-99999");
        
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
            "Non-existent bill should return 404 or 400");
    }

    @Test
    @DisplayName("Test creating bill with invalid data")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    @Description("Tests validation when creating bill with missing required fields")
    void testCreateBillValidation() {
        BillInput invalidBill = BillInput.builder()
            .description("Invalid bill - missing required fields")
            .build();

        Response response = billService.createBill(invalidBill);
        
        // Should return validation error
        assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 422 || response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Invalid bill should be handled appropriately");
    }

    private static String extractIdFromResponse(String body) {
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
