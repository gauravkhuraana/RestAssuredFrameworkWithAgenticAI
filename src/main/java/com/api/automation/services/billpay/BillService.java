package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.*;
import com.api.automation.models.billpay.enums.BillStatus;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Bill Service for Bill Payment API bill management endpoints
 */
public class BillService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BillService.class);
    
    private static final String BILLS_ENDPOINT = "/v1/bills";
    private static final String BILL_BY_ID_ENDPOINT = "/v1/bills/{id}";
    private static final String BILLS_SUMMARY_ENDPOINT = "/v1/bills/summary";
    private static final String BILLS_OVERDUE_ENDPOINT = "/v1/bills/overdue";
    private static final String BILL_FETCH_ENDPOINT = "/v1/bills/{id}/fetch";

    /**
     * Get all bills
     * GET /v1/bills
     */
    public Response getAllBills() {
        logger.info("Getting all bills");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(BILLS_ENDPOINT);
    }

    /**
     * Get bills with pagination
     */
    public Response getBills(int page, int limit) {
        logger.info("Getting bills - page: {}, limit: {}", page, limit);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("page", page)
                .withQueryParam("limit", limit)
                .get(BILLS_ENDPOINT);
    }

    /**
     * Get bills with pagination - alias
     */
    public Response getBillsWithPagination(int page, int limit) {
        return getBills(page, limit);
    }

    /**
     * Get bills by date range
     */
    public Response getBillsByDateRange(String startDate, String endDate) {
        logger.info("Getting bills from {} to {}", startDate, endDate);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("startDate", startDate)
                .withQueryParam("endDate", endDate)
                .get(BILLS_ENDPOINT);
    }

    /**
     * Fetch bill for biller and account number
     */
    public Response fetchBill(String billerId, String accountNumber) {
        logger.info("Fetching bill for biller: {} account: {}", billerId, accountNumber);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("billerId", billerId)
                .withQueryParam("accountNumber", accountNumber)
                .post(BILLS_ENDPOINT + "/fetch");
    }

    /**
     * Get bills by user ID
     */
    public Response getBillsByUserId(String userId) {
        logger.info("Getting bills for user: {}", userId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("userId", userId)
                .get(BILLS_ENDPOINT);
    }

    /**
     * Get bills by status
     */
    public Response getBillsByStatus(BillStatus status) {
        logger.info("Getting bills by status: {}", status);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("status", status.getValue())
                .get(BILLS_ENDPOINT);
    }

    /**
     * Get bills by biller ID
     */
    public Response getBillsByBillerId(String billerId) {
        logger.info("Getting bills for biller: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("billerId", billerId)
                .get(BILLS_ENDPOINT);
    }

    /**
     * Get bills summary
     * GET /v1/bills/summary
     */
    public Response getBillsSummary() {
        logger.info("Getting bills summary");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(BILLS_SUMMARY_ENDPOINT);
    }

    /**
     * Get overdue bills
     * GET /v1/bills/overdue
     */
    public Response getOverdueBills() {
        logger.info("Getting overdue bills");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(BILLS_OVERDUE_ENDPOINT);
    }

    /**
     * Get bill by ID
     * GET /v1/bills/{id}
     */
    public Response getBillById(String billId) {
        logger.info("Getting bill by ID: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billId)
                .get(BILL_BY_ID_ENDPOINT);
    }

    /**
     * Create new bill
     * POST /v1/bills
     */
    public Response createBill(BillInput billInput) {
        logger.info("Creating new bill for user: {}", billInput.getUserId());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withBody(billInput)
                .post(BILLS_ENDPOINT);
    }

    /**
     * Update bill (full replace)
     * PUT /v1/bills/{id}
     */
    public Response updateBill(String billId, BillInput billInput) {
        logger.info("Updating bill ID: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billId)
                .withBody(billInput)
                .put(BILL_BY_ID_ENDPOINT);
    }

    /**
     * Partial update bill
     * PATCH /v1/bills/{id}
     */
    public Response patchBill(String billId, Object patchData) {
        logger.info("Partially updating bill ID: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billId)
                .withBody(patchData)
                .patch(BILL_BY_ID_ENDPOINT);
    }

    /**
     * Delete bill
     * DELETE /v1/bills/{id}
     */
    public Response deleteBill(String billId) {
        logger.info("Deleting bill ID: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billId)
                .delete(BILL_BY_ID_ENDPOINT);
    }

    /**
     * Fetch latest bill from biller
     * POST /v1/bills/{id}/fetch
     */
    public Response fetchLatestBill(String billId) {
        logger.info("Fetching latest bill for ID: {}", billId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billId)
                .post(BILL_FETCH_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all bills as list
     */
    public List<Bill> getAllBillsAsList() {
        Response response = getAllBills();
        response.then().statusCode(200);
        ApiResponse<List<Bill>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Bill.class);
        return apiResponse.getData();
    }

    /**
     * Get bill by ID as object
     */
    public Bill getBillByIdAsObject(String billId) {
        Response response = getBillById(billId);
        response.then().statusCode(200);
        ApiResponse<Bill> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Bill.class);
        return apiResponse.getData();
    }

    /**
     * Create bill and return created object
     */
    public Bill createBillAndReturn(BillInput billInput) {
        Response response = createBill(billInput);
        response.then().statusCode(201);
        ApiResponse<Bill> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Bill.class);
        return apiResponse.getData();
    }

    /**
     * Get bills summary as object
     */
    public BillsSummary getBillsSummaryAsObject() {
        Response response = getBillsSummary();
        response.then().statusCode(200);
        ApiResponse<BillsSummary> apiResponse = ApiResponse.fromJson(response.getBody().asString(), BillsSummary.class);
        return apiResponse.getData();
    }

    /**
     * Get overdue bills as list
     */
    public List<Bill> getOverdueBillsAsList() {
        Response response = getOverdueBills();
        response.then().statusCode(200);
        ApiResponse<List<Bill>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Bill.class);
        return apiResponse.getData();
    }
}
