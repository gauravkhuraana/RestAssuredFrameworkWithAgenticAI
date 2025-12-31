package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.PaymentMethodType;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PaymentMethod Service for Bill Payment API payment method management endpoints
 */
public class PaymentMethodService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodService.class);
    
    private static final String PAYMENT_METHODS_ENDPOINT = "/v1/payment-methods";
    private static final String PAYMENT_METHOD_BY_ID_ENDPOINT = "/v1/payment-methods/{id}";
    private static final String PAYMENT_METHOD_TYPES_ENDPOINT = "/v1/payment-methods/types";
    private static final String PAYMENT_METHOD_SET_DEFAULT_ENDPOINT = "/v1/payment-methods/{id}/set-default";

    /**
     * Get all payment methods
     * GET /v1/payment-methods
     */
    public Response getAllPaymentMethods() {
        logger.info("Getting all payment methods");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(PAYMENT_METHODS_ENDPOINT);
    }

    /**
     * Get payment methods by user ID
     */
    public Response getPaymentMethodsByUserId(String userId) {
        logger.info("Getting payment methods for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("userId", userId)
                .get(PAYMENT_METHODS_ENDPOINT);
    }

    /**
     * Get payment methods by type
     */
    public Response getPaymentMethodsByType(PaymentMethodType type) {
        logger.info("Getting payment methods by type: {}", type);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("type", type.getValue())
                .get(PAYMENT_METHODS_ENDPOINT);
    }

    /**
     * Get supported payment method types
     * GET /v1/payment-methods/types
     */
    public Response getPaymentMethodTypes() {
        logger.info("Getting supported payment method types");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(PAYMENT_METHOD_TYPES_ENDPOINT);
    }

    /**
     * Get payment method by ID
     * GET /v1/payment-methods/{id}
     */
    public Response getPaymentMethodById(String paymentMethodId) {
        logger.info("Getting payment method by ID: {}", paymentMethodId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentMethodId)
                .get(PAYMENT_METHOD_BY_ID_ENDPOINT);
    }

    /**
     * Create new payment method
     * POST /v1/payment-methods
     */
    public Response createPaymentMethod(PaymentMethodInput input) {
        logger.info("Creating new payment method for user: {}", input.getUserId());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withBody(input)
                .post(PAYMENT_METHODS_ENDPOINT);
    }

    /**
     * Update payment method (full replace)
     * PUT /v1/payment-methods/{id}
     */
    public Response updatePaymentMethod(String paymentMethodId, PaymentMethodInput input) {
        logger.info("Updating payment method ID: {}", paymentMethodId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentMethodId)
                .withBody(input)
                .put(PAYMENT_METHOD_BY_ID_ENDPOINT);
    }

    /**
     * Partial update payment method
     * PATCH /v1/payment-methods/{id}
     */
    public Response patchPaymentMethod(String paymentMethodId, Object patchData) {
        logger.info("Partially updating payment method ID: {}", paymentMethodId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentMethodId)
                .withBody(patchData)
                .patch(PAYMENT_METHOD_BY_ID_ENDPOINT);
    }

    /**
     * Delete payment method
     * DELETE /v1/payment-methods/{id}
     */
    public Response deletePaymentMethod(String paymentMethodId) {
        logger.info("Deleting payment method ID: {}", paymentMethodId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentMethodId)
                .delete(PAYMENT_METHOD_BY_ID_ENDPOINT);
    }

    /**
     * Set payment method as default
     * POST /v1/payment-methods/{id}/set-default
     */
    public Response setDefaultPaymentMethod(String paymentMethodId) {
        logger.info("Setting payment method as default: {}", paymentMethodId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", paymentMethodId)
                .post(PAYMENT_METHOD_SET_DEFAULT_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all payment methods as list
     */
    public List<PaymentMethod> getAllPaymentMethodsAsList() {
        Response response = getAllPaymentMethods();
        response.then().statusCode(200);
        ApiResponse<List<PaymentMethod>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), PaymentMethod.class);
        return apiResponse.getData();
    }

    /**
     * Get payment method by ID as object
     */
    public PaymentMethod getPaymentMethodByIdAsObject(String paymentMethodId) {
        Response response = getPaymentMethodById(paymentMethodId);
        response.then().statusCode(200);
        ApiResponse<PaymentMethod> apiResponse = ApiResponse.fromJson(response.getBody().asString(), PaymentMethod.class);
        return apiResponse.getData();
    }

    /**
     * Create payment method and return created object
     */
    public PaymentMethod createPaymentMethodAndReturn(PaymentMethodInput input) {
        Response response = createPaymentMethod(input);
        response.then().statusCode(201);
        ApiResponse<PaymentMethod> apiResponse = ApiResponse.fromJson(response.getBody().asString(), PaymentMethod.class);
        return apiResponse.getData();
    }

    /**
     * Create UPI payment method
     */
    public PaymentMethod createUpiPaymentMethod(String userId, String upiId, String name) {
        PaymentMethodInput input = PaymentMethodInput.upi(userId, upiId, name);
        return createPaymentMethodAndReturn(input);
    }

    /**
     * Get user's default payment method
     */
    public PaymentMethod getDefaultPaymentMethod(String userId) {
        List<PaymentMethod> methods = getAllPaymentMethodsAsList();
        return methods.stream()
                .filter(m -> userId.equals(m.getUserId()) && m.isDefaultMethod())
                .findFirst()
                .orElse(null);
    }
}
