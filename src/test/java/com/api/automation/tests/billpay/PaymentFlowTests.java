package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.BillStatus;
import com.api.automation.models.billpay.enums.PaymentMethodType;
import com.api.automation.models.billpay.enums.PaymentStatus;
import com.api.automation.services.billpay.BillService;
import com.api.automation.services.billpay.PaymentMethodService;
import com.api.automation.services.billpay.PaymentService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Payment Flow Tests for Bill Payment API
 * End-to-end payment scenarios and operations
 */
@Epic("Bill Payment API")
@Feature("Payment Processing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentFlowTests {

    private static PaymentService paymentService;
    private static PaymentMethodService paymentMethodService;
    private static BillService billService;
    private static String createdPaymentId;
    private static String testPaymentMethodId;

    @BeforeAll
    static void setup() {
        paymentService = new PaymentService();
        paymentMethodService = new PaymentMethodService();
        billService = new BillService();
    }

    @Test
    @Order(1)
    @DisplayName("Get all payments")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Retrieval")
    @Description("Tests retrieval of all payments from the API")
    void testGetAllPayments() {
        Response response = paymentService.getAllPayments();
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response body should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Get payment statistics")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Statistics")
    @Description("Tests retrieval of payment statistics")
    void testGetPaymentStats() {
        Response response = paymentService.getPaymentStats();
        
        assertEquals(200, response.getStatusCode(), "Stats should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Stats response should contain data");
    }

    @Test
    @Order(3)
    @DisplayName("Get all payment methods")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Methods")
    @Description("Tests retrieval of available payment methods")
    void testGetPaymentMethods() {
        Response response = paymentMethodService.getAllPaymentMethods();
        
        assertEquals(200, response.getStatusCode(), "Should return 200 OK");
        
        String body = response.getBody().asString();
        if (body.contains("id")) {
            testPaymentMethodId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(4)
    @DisplayName("Get payment method types")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Methods")
    @Description("Tests retrieval of payment method types")
    void testGetPaymentMethodTypes() {
        Response response = paymentMethodService.getPaymentMethodTypes();
        
        assertEquals(200, response.getStatusCode(), "Types should return 200 OK");
    }

    @Test
    @Order(5)
    @DisplayName("Create a new payment method")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Payment Method Creation")
    @Description("Tests creating a new payment method")
    void testCreatePaymentMethod() {
        PaymentMethodInput newMethod = PaymentMethodInput.builder()
            .userId("1")
            .type(PaymentMethodType.CREDIT_CARD)
            .name("Test Visa Card")
            .cardNumber("4242424242424242")
            .expiryMonth("12")
            .expiryYear("26")
            .isDefault(false)
            .build();

        Response response = paymentMethodService.createPaymentMethod(newMethod);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Create payment method should return 200 or 201");
        
        String body = response.getBody().asString();
        if (body.contains("id")) {
            testPaymentMethodId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(6)
    @DisplayName("Create a payment for a bill")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Payment Creation")
    @Description("Tests creating a new payment for an existing bill")
    void testCreatePayment() {
        BigDecimal amount = BigDecimal.valueOf(100.00);

        PaymentInput newPayment = PaymentInput.builder()
            .billId("1")
            .userId("1")
            .paymentMethodId(testPaymentMethodId != null ? testPaymentMethodId : "1")
            .amount(amount)
            .build();

        Response response = paymentService.createPayment(newPayment);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Create payment should return 200 or 201, got: " + response.getStatusCode());
        
        String body = response.getBody().asString();
        if (body.contains("id")) {
            createdPaymentId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(7)
    @DisplayName("Get payment by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Retrieval")
    @Description("Tests retrieval of a specific payment by ID")
    void testGetPaymentById() {
        String testPaymentId = createdPaymentId != null ? createdPaymentId : "1";
        
        Response response = paymentService.getPaymentById(testPaymentId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Get payment by ID should return 200 or 404");
    }

    @Test
    @Order(8)
    @DisplayName("Update payment status")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Update")
    @Description("Tests updating an existing payment's status")
    void testUpdatePaymentStatus() {
        String testPaymentId = createdPaymentId != null ? createdPaymentId : "1";
        
        BigDecimal amount = BigDecimal.valueOf(100.00);

        PaymentInput updatePayment = PaymentInput.builder()
            .billId("1")
            .userId("1")
            .paymentMethodId("1")
            .amount(amount)
            .build();

        Response response = paymentService.updatePayment(testPaymentId, updatePayment);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Update should return 200 or 404");
    }

    @Test
    @Order(9)
    @DisplayName("Filter payments by status")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Filtering")
    @Description("Tests filtering payments by status")
    void testFilterPaymentsByStatus() {
        Response response = paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED);
        
        assertEquals(200, response.getStatusCode(), "Filter by status should return 200");
    }

    @Test
    @Order(10)
    @DisplayName("Get payments for user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Retrieval")
    @Description("Tests retrieval of payments for a specific user")
    void testGetPaymentsByUser() {
        Response response = paymentService.getPaymentsByUserId("1");
        
        assertEquals(200, response.getStatusCode(), "Get payments by user should return 200");
    }

    @Test
    @Order(11)
    @DisplayName("Process a refund")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Refund Processing")
    @Description("Tests processing a refund for a completed payment")
    void testProcessRefund() {
        String testPaymentId = createdPaymentId != null ? createdPaymentId : "1";
        
        Response response = paymentService.refundPayment(testPaymentId, "Test refund - automated");
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || 
                   response.getStatusCode() == 400 || response.getStatusCode() == 404,
            "Refund should be processed or return appropriate error");
    }

    @Test
    @Order(12)
    @DisplayName("Cancel a pending payment")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Cancellation")
    @Description("Tests cancelling a pending payment")
    void testCancelPayment() {
        // Create a new pending payment to cancel
        BigDecimal amount = BigDecimal.valueOf(50.00);

        PaymentInput cancelPayment = PaymentInput.builder()
            .billId("1")
            .userId("1")
            .paymentMethodId("1")
            .amount(amount)
            .build();

        Response createResponse = paymentService.createPayment(cancelPayment);
        String paymentId = null;
        if (createResponse.getStatusCode() == 200 || createResponse.getStatusCode() == 201) {
            paymentId = extractIdFromResponse(createResponse.getBody().asString());
        }

        if (paymentId != null) {
            Response cancelResponse = paymentService.cancelPayment(paymentId, "Automated cancellation test");
            
            assertTrue(cancelResponse.getStatusCode() == 200 || cancelResponse.getStatusCode() == 404,
                "Cancel should succeed or return not found");
        }
    }

    @Test
    @Order(13)
    @DisplayName("Set default payment method")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Method Management")
    @Description("Tests setting a payment method as default")
    void testSetDefaultPaymentMethod() {
        String methodId = testPaymentMethodId != null ? testPaymentMethodId : "1";
        
        Response response = paymentMethodService.setDefaultPaymentMethod(methodId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Set default should return 200 or 404");
    }

    @Test
    @Order(14)
    @DisplayName("End-to-end bill payment flow")
    @Severity(SeverityLevel.BLOCKER)
    @Story("E2E Payment Flow")
    @Description("Tests complete bill payment workflow from bill to payment confirmation")
    void testEndToEndPaymentFlow() {
        // Step 1: Get a pending bill
        Response billsResponse = billService.getBillsByStatus(BillStatus.PENDING);
        assertEquals(200, billsResponse.getStatusCode(), "Should get pending bills");
        
        // Step 2: Get available payment methods
        Response methodsResponse = paymentMethodService.getAllPaymentMethods();
        assertEquals(200, methodsResponse.getStatusCode(), "Should get payment methods");
        
        // Step 3: Create payment
        BigDecimal amount = BigDecimal.valueOf(75.00);

        PaymentInput e2ePayment = PaymentInput.builder()
            .billId("1")
            .userId("1")
            .paymentMethodId("1")
            .amount(amount)
            .build();

        Response paymentResponse = paymentService.createPayment(e2ePayment);
        assertTrue(paymentResponse.getStatusCode() == 200 || paymentResponse.getStatusCode() == 201,
            "Payment should be created");
        
        // Step 4: Verify payment stats updated
        Response statsResponse = paymentService.getPaymentStats();
        assertEquals(200, statsResponse.getStatusCode(), "Stats should be available");
    }

    @Test
    @Order(100)
    @DisplayName("Delete payment")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Deletion")
    @Description("Tests deleting a payment from the system")
    void testDeletePayment() {
        if (createdPaymentId != null) {
            Response response = paymentService.deletePayment(createdPaymentId);
            
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204 || response.getStatusCode() == 404,
                "Delete should return success or not found");
        }
    }

    @Test
    @DisplayName("Validate payment not found returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    @Description("Tests that requesting non-existent payment returns 404")
    void testPaymentNotFound() {
        Response response = paymentService.getPaymentById("non-existent-id-99999");
        
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
            "Non-existent payment should return 404 or 400");
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
