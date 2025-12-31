package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.KycStatus;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * User Service for Bill Payment API user management endpoints
 */
public class BillPayUserService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BillPayUserService.class);
    
    private static final String USERS_ENDPOINT = "/v1/users";
    private static final String USER_BY_ID_ENDPOINT = "/v1/users/{id}";
    private static final String USER_BILLS_ENDPOINT = "/v1/users/{id}/bills";
    private static final String USER_PAYMENT_METHODS_ENDPOINT = "/v1/users/{id}/payment-methods";
    private static final String USER_TRANSACTIONS_ENDPOINT = "/v1/users/{id}/transactions";
    private static final String USER_VERIFY_KYC_ENDPOINT = "/v1/users/{id}/verify-kyc";

    /**
     * Get all users
     * GET /v1/users
     */
    public Response getAllUsers() {
        logger.info("Getting all users");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(USERS_ENDPOINT);
    }

    /**
     * Get users with pagination
     */
    public Response getUsers(int page, int limit) {
        logger.info("Getting users - page: {}, limit: {}", page, limit);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("page", page)
                .withQueryParam("limit", limit)
                .get(USERS_ENDPOINT);
    }

    /**
     * Get user by ID
     * GET /v1/users/{id}
     */
    public Response getUserById(String userId) {
        logger.info("Getting user by ID: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .get(USER_BY_ID_ENDPOINT);
    }

    /**
     * Create new user
     * POST /v1/users
     */
    public Response createUser(UserInput userInput) {
        logger.info("Creating new user: {}", userInput.getEmail());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withBody(userInput)
                .post(USERS_ENDPOINT);
    }

    /**
     * Update user (full replace)
     * PUT /v1/users/{id}
     */
    public Response updateUser(String userId, UserInput userInput) {
        logger.info("Updating user ID: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .withBody(userInput)
                .put(USER_BY_ID_ENDPOINT);
    }

    /**
     * Partial update user
     * PATCH /v1/users/{id}
     */
    public Response patchUser(String userId, Object patchData) {
        logger.info("Partially updating user ID: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .withBody(patchData)
                .patch(USER_BY_ID_ENDPOINT);
    }

    /**
     * Delete user
     * DELETE /v1/users/{id}
     */
    public Response deleteUser(String userId) {
        logger.info("Deleting user ID: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .delete(USER_BY_ID_ENDPOINT);
    }

    /**
     * Get user's bills
     * GET /v1/users/{id}/bills
     */
    public Response getUserBills(String userId) {
        logger.info("Getting bills for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .get(USER_BILLS_ENDPOINT);
    }

    /**
     * Get user's payment methods
     * GET /v1/users/{id}/payment-methods
     */
    public Response getUserPaymentMethods(String userId) {
        logger.info("Getting payment methods for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .get(USER_PAYMENT_METHODS_ENDPOINT);
    }

    /**
     * Get user's transactions
     * GET /v1/users/{id}/transactions
     */
    public Response getUserTransactions(String userId) {
        logger.info("Getting transactions for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .get(USER_TRANSACTIONS_ENDPOINT);
    }

    /**
     * Verify user KYC
     * POST /v1/users/{id}/verify-kyc
     */
    public Response verifyUserKyc(String userId, KycStatus status) {
        logger.info("Verifying KYC for user: {} with status: {}", userId, status);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .withBody(Map.of("status", status.getValue()))
                .post(USER_VERIFY_KYC_ENDPOINT);
    }

    /**
     * Verify user KYC with document details
     */
    public Response verifyUserKyc(String userId, KycStatus status, String documentType, String documentNumber) {
        logger.info("Verifying KYC for user: {} with document: {}", userId, documentType);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", userId)
                .withBody(Map.of(
                        "status", status.getValue(),
                        "documentType", documentType,
                        "documentNumber", documentNumber
                ))
                .post(USER_VERIFY_KYC_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all users as list
     */
    public List<BillPayUser> getAllUsersAsList() {
        Response response = getAllUsers();
        response.then().statusCode(200);
        ApiResponse<List<BillPayUser>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), BillPayUser.class);
        return apiResponse.getData();
    }

    /**
     * Get user by ID as object
     */
    public BillPayUser getUserByIdAsObject(String userId) {
        Response response = getUserById(userId);
        response.then().statusCode(200);
        ApiResponse<BillPayUser> apiResponse = ApiResponse.fromJson(response.getBody().asString(), BillPayUser.class);
        return apiResponse.getData();
    }

    /**
     * Create user and return created object
     */
    public BillPayUser createUserAndReturn(UserInput userInput) {
        Response response = createUser(userInput);
        response.then().statusCode(201);
        ApiResponse<BillPayUser> apiResponse = ApiResponse.fromJson(response.getBody().asString(), BillPayUser.class);
        return apiResponse.getData();
    }

    /**
     * Get user's bills as list
     */
    public List<Bill> getUserBillsAsList(String userId) {
        Response response = getUserBills(userId);
        response.then().statusCode(200);
        ApiResponse<List<Bill>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Bill.class);
        return apiResponse.getData();
    }

    /**
     * Get user's payment methods as list
     */
    public List<PaymentMethod> getUserPaymentMethodsAsList(String userId) {
        Response response = getUserPaymentMethods(userId);
        response.then().statusCode(200);
        ApiResponse<List<PaymentMethod>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), PaymentMethod.class);
        return apiResponse.getData();
    }

    /**
     * Get user's transactions as list (payments)
     */
    public List<Payment> getUserTransactionsAsList(String userId) {
        Response response = getUserTransactions(userId);
        response.then().statusCode(200);
        ApiResponse<List<Payment>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Payment.class);
        return apiResponse.getData();
    }

    /**
     * Create a simple user with basic info
     */
    public BillPayUser createSimpleUser(String email, String firstName, String lastName, String phone) {
        UserInput input = UserInput.simple(email, firstName, lastName, phone);
        return createUserAndReturn(input);
    }
}
