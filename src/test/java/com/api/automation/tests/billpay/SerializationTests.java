package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.*;
import com.api.automation.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Serialization Tests for Bill Payment API Models
 * Validates proper JSON serialization/deserialization of all models
 */
@Epic("Bill Payment API")
@Feature("Model Serialization")
class SerializationTests {

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Test Money model serialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests Money POJO serializes and deserializes correctly")
    void testMoneySerialization() throws Exception {
        Money money = Money.builder()
            .value(BigDecimal.valueOf(150.50))
            .currency("USD")
            .build();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(money);
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("150.5") || json.contains("150.50"), "JSON should contain value");
        assertTrue(json.contains("USD"), "JSON should contain currency");

        // Deserialize back
        Money deserialized = objectMapper.readValue(json, Money.class);
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(150.50, deserialized.getValue().doubleValue(), 0.01, "Value should match");
        assertEquals("USD", deserialized.getCurrency(), "Currency should match");
    }

    @Test
    @DisplayName("Test Address model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests Address POJO serializes and deserializes correctly")
    void testAddressSerialization() throws Exception {
        Address address = Address.builder()
            .line1("123 Main Street")
            .line2("Apt 4B")
            .city("New York")
            .state("NY")
            .postalCode("10001")
            .country("USA")
            .build();

        String json = objectMapper.writeValueAsString(address);
        assertNotNull(json, "JSON should not be null");

        Address deserialized = objectMapper.readValue(json, Address.class);
        assertEquals("123 Main Street", deserialized.getLine1());
        assertEquals("New York", deserialized.getCity());
        assertEquals("10001", deserialized.getPostalCode());
    }

    @Test
    @DisplayName("Test Biller model serialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests Biller POJO serializes and deserializes correctly")
    void testBillerSerialization() throws Exception {
        Biller biller = Biller.builder()
            .id("biller-123")
            .name("Electric Company")
            .category(BillerCategory.ELECTRICITY)
            .description("Power utility company")
            .websiteUrl("https://electric.example.com")
            .supportPhone("+1-555-0100")
            .supportEmail("support@electric.example.com")
            .active(true)
            .build();

        String json = objectMapper.writeValueAsString(biller);
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("Electric Company"), "JSON should contain biller name");

        Biller deserialized = objectMapper.readValue(json, Biller.class);
        assertEquals("biller-123", deserialized.getId());
        assertEquals("Electric Company", deserialized.getName());
        assertEquals(BillerCategory.ELECTRICITY, deserialized.getCategory());
        assertTrue(deserialized.getActive());
    }

    @Test
    @DisplayName("Test BillerInput model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests BillerInput POJO for API requests")
    void testBillerInputSerialization() throws Exception {
        BillerInput input = BillerInput.builder()
            .name("New Biller")
            .category(BillerCategory.TELECOM)
            .active(true)
            .build();

        String json = objectMapper.writeValueAsString(input);
        assertNotNull(json, "JSON should not be null");

        BillerInput deserialized = objectMapper.readValue(json, BillerInput.class);
        assertEquals("New Biller", deserialized.getName());
        assertEquals(BillerCategory.TELECOM, deserialized.getCategory());
    }

    @Test
    @DisplayName("Test Bill model serialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests Bill POJO serializes and deserializes correctly")
    void testBillSerialization() throws Exception {
        Money amount = Money.builder()
            .value(BigDecimal.valueOf(200.00))
            .currency("USD")
            .build();

        Bill bill = Bill.builder()
            .id("bill-456")
            .billerId("biller-123")
            .userId("user-789")
            .consumerNumber("CONS-001")
            .amount(amount)
            .dueDate("2024-12-31")
            .status(BillStatus.PENDING)
            .description("Monthly electric bill")
            .build();

        String json = objectMapper.writeValueAsString(bill);
        assertNotNull(json, "JSON should not be null");

        Bill deserialized = objectMapper.readValue(json, Bill.class);
        assertEquals("bill-456", deserialized.getId());
        assertEquals(BillStatus.PENDING, deserialized.getStatus());
        assertNotNull(deserialized.getAmount());
        assertEquals(200.00, deserialized.getAmount().getValue().doubleValue(), 0.01);
    }

    @Test
    @DisplayName("Test Payment model serialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests Payment POJO serializes and deserializes correctly")
    void testPaymentSerialization() throws Exception {
        Money amount = Money.builder()
            .value(BigDecimal.valueOf(100.00))
            .currency("USD")
            .build();

        Payment payment = Payment.builder()
            .id("payment-111")
            .billId("bill-456")
            .userId("user-789")
            .paymentMethodId("method-222")
            .amount(amount)
            .status(PaymentStatus.COMPLETED)
            .transactionId("TXN-12345")
            .build();

        String json = objectMapper.writeValueAsString(payment);
        assertNotNull(json, "JSON should not be null");

        Payment deserialized = objectMapper.readValue(json, Payment.class);
        assertEquals("payment-111", deserialized.getId());
        assertEquals(PaymentStatus.COMPLETED, deserialized.getStatus());
        assertEquals("TXN-12345", deserialized.getTransactionId());
    }

    @Test
    @DisplayName("Test PaymentMethod model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests PaymentMethod POJO serializes and deserializes correctly")
    void testPaymentMethodSerialization() throws Exception {
        PaymentMethod method = PaymentMethod.builder()
            .id("method-333")
            .userId("user-789")
            .type(PaymentMethodType.CREDIT_CARD)
            .name("My Visa Card")
            .cardNumber("**** 4242")
            .expiryMonth("12")
            .expiryYear("25")
            .isDefault(true)
            .build();

        String json = objectMapper.writeValueAsString(method);
        assertNotNull(json, "JSON should not be null");

        PaymentMethod deserialized = objectMapper.readValue(json, PaymentMethod.class);
        assertEquals(PaymentMethodType.CREDIT_CARD, deserialized.getType());
        assertTrue(deserialized.getIsDefault());
    }

    @Test
    @DisplayName("Test BillPayUser model serialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests BillPayUser POJO serializes and deserializes correctly")
    void testBillPayUserSerialization() throws Exception {
        Address address = Address.builder()
            .line1("456 Oak Ave")
            .city("Boston")
            .state("MA")
            .postalCode("02101")
            .country("USA")
            .build();

        BillPayUser user = BillPayUser.builder()
            .id("user-789")
            .email("john.doe@example.com")
            .firstName("John")
            .lastName("Doe")
            .phone("+1-555-0123")
            .address(address)
            .kycStatus(KycStatus.VERIFIED)
            .active(true)
            .build();

        String json = objectMapper.writeValueAsString(user);
        assertNotNull(json, "JSON should not be null");

        BillPayUser deserialized = objectMapper.readValue(json, BillPayUser.class);
        assertEquals("john.doe@example.com", deserialized.getEmail());
        assertEquals(KycStatus.VERIFIED, deserialized.getKycStatus());
        assertNotNull(deserialized.getAddress());
        assertEquals("Boston", deserialized.getAddress().getCity());
    }

    @Test
    @DisplayName("Test ApiResponse generic wrapper serialization")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Model Serialization")
    @Description("Tests ApiResponse<T> generic wrapper serializes correctly")
    void testApiResponseSerialization() throws Exception {
        Biller biller = Biller.builder()
            .id("biller-123")
            .name("Test Biller")
            .category(BillerCategory.ELECTRICITY)
            .build();

        PaginationMeta meta = PaginationMeta.builder()
            .page(1)
            .limit(10)
            .total(100)
            .totalPages(10)
            .build();

        ApiResponse<Biller> response = ApiResponse.<Biller>builder()
            .success(true)
            .data(biller)
            .meta(meta)
            .build();

        String json = objectMapper.writeValueAsString(response);
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("\"success\":true"), "JSON should contain success flag");
        assertTrue(json.contains("Test Biller"), "JSON should contain data");
    }

    @Test
    @DisplayName("Test ApiResponse with List deserialization")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Model Serialization")
    @Description("Tests ApiResponse<List<T>> can be deserialized correctly using TypeReference")
    void testApiResponseListDeserialization() throws Exception {
        // Simulate JSON response from API
        String jsonResponse = """
            {
                "success": true,
                "data": [
                    {"id": "1", "name": "Biller 1", "category": "electricity", "active": true},
                    {"id": "2", "name": "Biller 2", "category": "telecom", "active": true}
                ],
                "meta": {"page": 1, "limit": 10, "total": 2, "totalPages": 1}
            }
            """;

        // Deserialize using TypeReference
        ApiResponse<List<Biller>> response = objectMapper.readValue(
            jsonResponse, 
            new TypeReference<ApiResponse<List<Biller>>>() {}
        );

        assertTrue(response.isSuccessful(), "Response should be successful");
        assertNotNull(response.getData(), "Data should not be null");
        assertEquals(2, response.getData().size(), "Should have 2 billers");
        assertEquals("Biller 1", response.getData().get(0).getName());
    }

    @Test
    @DisplayName("Test all enum serialization with @JsonValue")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Enum Serialization")
    @Description("Tests all enums serialize to their string values correctly")
    void testEnumSerialization() throws Exception {
        // Test BillerCategory - enums use lowercase values
        assertEquals("\"electricity\"", objectMapper.writeValueAsString(BillerCategory.ELECTRICITY));
        assertEquals("\"telecom\"", objectMapper.writeValueAsString(BillerCategory.TELECOM));
        assertEquals("\"insurance\"", objectMapper.writeValueAsString(BillerCategory.INSURANCE));

        // Test BillStatus
        assertEquals("\"pending\"", objectMapper.writeValueAsString(BillStatus.PENDING));
        assertEquals("\"paid\"", objectMapper.writeValueAsString(BillStatus.PAID));
        assertEquals("\"overdue\"", objectMapper.writeValueAsString(BillStatus.OVERDUE));

        // Test PaymentStatus
        assertEquals("\"initiated\"", objectMapper.writeValueAsString(PaymentStatus.INITIATED));
        assertEquals("\"completed\"", objectMapper.writeValueAsString(PaymentStatus.COMPLETED));
        assertEquals("\"failed\"", objectMapper.writeValueAsString(PaymentStatus.FAILED));

        // Test PaymentMethodType
        assertEquals("\"credit_card\"", objectMapper.writeValueAsString(PaymentMethodType.CREDIT_CARD));
        assertEquals("\"debit_card\"", objectMapper.writeValueAsString(PaymentMethodType.DEBIT_CARD));
        assertEquals("\"upi\"", objectMapper.writeValueAsString(PaymentMethodType.UPI));

        // Test KycStatus
        assertEquals("\"pending\"", objectMapper.writeValueAsString(KycStatus.PENDING));
        assertEquals("\"verified\"", objectMapper.writeValueAsString(KycStatus.VERIFIED));
        assertEquals("\"rejected\"", objectMapper.writeValueAsString(KycStatus.REJECTED));
    }

    @Test
    @DisplayName("Test enum deserialization from strings")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Enum Serialization")
    @Description("Tests enums can be deserialized from JSON string values")
    void testEnumDeserialization() throws Exception {
        assertEquals(BillerCategory.ELECTRICITY, objectMapper.readValue("\"electricity\"", BillerCategory.class));
        assertEquals(BillStatus.PENDING, objectMapper.readValue("\"pending\"", BillStatus.class));
        assertEquals(PaymentStatus.COMPLETED, objectMapper.readValue("\"completed\"", PaymentStatus.class));
        assertEquals(PaymentMethodType.CREDIT_CARD, objectMapper.readValue("\"credit_card\"", PaymentMethodType.class));
        assertEquals(KycStatus.VERIFIED, objectMapper.readValue("\"verified\"", KycStatus.class));
    }

    @Test
    @DisplayName("Test ErrorResponse model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests ErrorResponse POJO for API error handling")
    void testErrorResponseSerialization() throws Exception {
        ErrorResponse.ErrorDetail errorDetail = ErrorResponse.ErrorDetail.builder()
            .code("NOT_FOUND")
            .message("Resource not found")
            .path("/v1/billers/999")
            .build();

        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error(errorDetail)
            .build();

        String json = objectMapper.writeValueAsString(error);
        assertNotNull(json, "JSON should not be null");

        ErrorResponse deserialized = objectMapper.readValue(json, ErrorResponse.class);
        assertEquals("NOT_FOUND", deserialized.getError().getCode());
        assertEquals("Resource not found", deserialized.getError().getMessage());
    }

    @Test
    @DisplayName("Test UploadedFile model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests UploadedFile POJO serializes correctly")
    void testUploadedFileSerialization() throws Exception {
        UploadedFile file = UploadedFile.builder()
            .id("file-123")
            .filename("document.pdf")
            .originalName("my-document.pdf")
            .mimeType("application/pdf")
            .size(1024L)
            .url("https://storage.example.com/files/document.pdf")
            .build();

        String json = objectMapper.writeValueAsString(file);
        assertNotNull(json, "JSON should not be null");

        UploadedFile deserialized = objectMapper.readValue(json, UploadedFile.class);
        assertEquals("document.pdf", deserialized.getFilename());
        assertEquals("application/pdf", deserialized.getMimeType());
        assertEquals(Long.valueOf(1024L), deserialized.getSize());
    }

    @Test
    @DisplayName("Test TokenResponse model serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests TokenResponse POJO for OAuth2 token handling")
    void testTokenResponseSerialization() throws Exception {
        TokenResponse token = TokenResponse.builder()
            .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .refreshToken("refresh-token-xyz")
            .scope("read write")
            .build();

        String json = objectMapper.writeValueAsString(token);
        assertNotNull(json, "JSON should not be null");

        TokenResponse deserialized = objectMapper.readValue(json, TokenResponse.class);
        assertEquals("Bearer", deserialized.getTokenType());
        assertEquals(Long.valueOf(3600L), deserialized.getExpiresIn());
    }

    @Test
    @DisplayName("Test null handling in serialization")
    @Severity(SeverityLevel.NORMAL)
    @Story("Model Serialization")
    @Description("Tests that null fields are handled correctly")
    void testNullHandling() throws Exception {
        // Create object with some null fields
        Biller biller = Biller.builder()
            .id("biller-123")
            .name("Test Biller")
            .build();

        String json = objectMapper.writeValueAsString(biller);
        assertNotNull(json, "JSON should not be null");

        Biller deserialized = objectMapper.readValue(json, Biller.class);
        assertEquals("biller-123", deserialized.getId());
        // Null fields should remain null after deserialization
    }

    @Test
    @DisplayName("Test JsonUtils helper methods")
    @Severity(SeverityLevel.NORMAL)
    @Story("Utility Methods")
    @Description("Tests JsonUtils utility methods for serialization")
    void testJsonUtilsMethods() {
        Money money = Money.builder()
            .value(BigDecimal.valueOf(99.99))
            .currency("EUR")
            .build();

        // Test objectToJson
        String json = JsonUtils.objectToJson(money);
        assertNotNull(json, "JsonUtils.objectToJson should return JSON string");
        assertTrue(json.contains("99.99"), "JSON should contain value");

        // Test jsonToObject
        Money deserialized = JsonUtils.jsonToObject(json, Money.class);
        assertNotNull(deserialized, "JsonUtils.jsonToObject should return object");
        assertEquals("EUR", deserialized.getCurrency());
    }
}
