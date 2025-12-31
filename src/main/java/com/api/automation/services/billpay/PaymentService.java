package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.PaymentStatus;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Payment Service for Bill Payment API payment transaction endpoints
 */
public class PaymentService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private static final String PAYMENTS_ENDPOINT = "/v1/payments";
    private static final String PAYMENT_BY_ID_ENDPOINT = "/v1/payments/{id}";
    private static final String PAYMENTS_STATS_ENDPOINT = "/v1/payments/stats";
    private static final String PAYMENT_REFUND_ENDPOINT = "/v1/payments/{id}/refund";
    private static final String PAYMENT_CANCEL_ENDPOINT = "/v1/payments/{id}/cancel";

    /**
     * Get all payments
     * GET /v1/payments
     */
    public Response getAllPayments() {
        logger.info("Getting all payments");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(PAYMENTS_ENDPOINT);
    }

    /**
     * Get payments with pagination
     */
    public Response getPayments(int page, int limit) {
        logger.info("Getting payments - page: {}, limit: {}", page, limit);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("page", page)
                .withQueryParam("limit", limit)
                .get(PAYMENTS_ENDPOINT);
    }

    /**
     * Get payments by user ID
     */
    public Response getPaymentsByUserId(String userId) {
        logger.info("Getting payments for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("userId", userId)
                .get(PAYMENTS_ENDPOINT);
    }

    /**
     * Get payments by status
     */
    public Response getPaymentsByStatus(PaymentStatus status) {
        logger.info("Getting payments by status: {}", status);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("status", status.getValue())
                .get(PAYMENTS_ENDPOINT);
    }

    /**
     * Get payments by bill ID
     */
    public Response getPaymentsByBillId(String billId) {
        logger.info("Getting payments for bill: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("billId", billId)
                .get(PAYMENTS_ENDPOINT);
    }

    /**
     * Get payment statistics
     * GET /v1/payments/stats
     */
    public Response getPaymentStats() {
        logger.info("Getting payment statistics");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(PAYMENTS_STATS_ENDPOINT);
    }

    /**
     * Get payment by ID
     * GET /v1/payments/{id}
     */
    public Response getPaymentById(String paymentId) {
        logger.info("Getting payment by ID: {}", paymentId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .get(PAYMENT_BY_ID_ENDPOINT);
    }

    /**
     * Create/process new payment
     * POST /v1/payments
     */
    public Response createPayment(PaymentInput paymentInput) {
        logger.info("Creating new payment for bill: {}", paymentInput.getBillId());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withBody(paymentInput)
                .post(PAYMENTS_ENDPOINT);
    }

    /**
     * Update payment
     * PUT /v1/payments/{id}
     */
    public Response updatePayment(String paymentId, PaymentInput paymentInput) {
        logger.info("Updating payment ID: {}", paymentId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .withBody(paymentInput)
                .put(PAYMENT_BY_ID_ENDPOINT);
    }

    /**
     * Delete payment
     * DELETE /v1/payments/{id}
     */
    public Response deletePayment(String paymentId) {
        logger.info("Deleting payment ID: {}", paymentId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .delete(PAYMENT_BY_ID_ENDPOINT);
    }

    /**
     * Refund payment
     * POST /v1/payments/{id}/refund
     */
    public Response refundPayment(String paymentId, String reason) {
        logger.info("Refunding payment ID: {}", paymentId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .withBody(Map.of("reason", reason))
                .post(PAYMENT_REFUND_ENDPOINT);
    }

    /**
     * Refund payment with amount
     */
    public Response refundPayment(String paymentId, Money amount, String reason) {
        logger.info("Refunding payment ID: {} amount: {}", paymentId, amount);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .withBody(Map.of("amount", amount, "reason", reason))
                .post(PAYMENT_REFUND_ENDPOINT);
    }

    /**
     * Cancel payment
     * POST /v1/payments/{id}/cancel
     */
    public Response cancelPayment(String paymentId) {
        logger.info("Cancelling payment ID: {}", paymentId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .post(PAYMENT_CANCEL_ENDPOINT);
    }

    /**
     * Cancel payment with reason
     */
    public Response cancelPayment(String paymentId, String reason) {
        logger.info("Cancelling payment ID: {} - reason: {}", paymentId, reason);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentId)
                .withBody(Map.of("reason", reason))
                .post(PAYMENT_CANCEL_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all payments as list
     */
    public List<Payment> getAllPaymentsAsList() {
        Response response = getAllPayments();
        response.then().statusCode(200);
        ApiResponse<List<Payment>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Payment.class);
        return apiResponse.getData();
    }

    /**
     * Get payment by ID as object
     */
    public Payment getPaymentByIdAsObject(String paymentId) {
        Response response = getPaymentById(paymentId);
        response.then().statusCode(200);
        ApiResponse<Payment> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Payment.class);
        return apiResponse.getData();
    }

    /**
     * Create payment and return created object
     */
    public Payment createPaymentAndReturn(PaymentInput paymentInput) {
        Response response = createPayment(paymentInput);
        response.then().statusCode(201);
        ApiResponse<Payment> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Payment.class);
        return apiResponse.getData();
    }

    /**
     * Get payment stats as object
     */
    public PaymentStats getPaymentStatsAsObject() {
        Response response = getPaymentStats();
        response.then().statusCode(200);
        ApiResponse<PaymentStats> apiResponse = ApiResponse.fromJson(response.getBody().asString(), PaymentStats.class);
        return apiResponse.getData();
    }

    /**
     * Process payment end-to-end
     * Creates payment and waits for completion
     */
    public Payment processPayment(String billId, String userId, String paymentMethodId, Money amount) {
        PaymentInput input = PaymentInput.simple(billId, userId, paymentMethodId, amount);
        return createPaymentAndReturn(input);
    }
}
