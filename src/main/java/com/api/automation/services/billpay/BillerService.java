package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.ApiResponse;
import com.api.automation.models.billpay.Biller;
import com.api.automation.models.billpay.BillerInput;
import com.api.automation.models.billpay.enums.BillerCategory;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Biller Service for Bill Payment API biller management endpoints
 */
public class BillerService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BillerService.class);
    
    private static final String BILLERS_ENDPOINT = "/v1/billers";
    private static final String BILLER_BY_ID_ENDPOINT = "/v1/billers/{id}";
    private static final String BILLER_CATEGORIES_ENDPOINT = "/v1/billers/categories";

    /**
     * Get all billers
     * GET /v1/billers
     */
    public Response getAllBillers() {
        logger.info("Getting all billers");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Get billers with pagination
     */
    public Response getBillers(int page, int limit) {
        logger.info("Getting billers - page: {}, limit: {}", page, limit);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("page", page)
                .withQueryParam("limit", limit)
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Get billers by category
     */
    public Response getBillersByCategory(BillerCategory category) {
        logger.info("Getting billers by category: {}", category);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("category", category.getValue())
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Get billers with pagination (alias)
     */
    public Response getBillersWithPagination(int page, int limit) {
        return getBillers(page, limit);
    }

    /**
     * Get billers sorted
     */
    public Response getBillersSorted(String sortBy, String order) {
        logger.info("Getting billers sorted by: {} {}", sortBy, order);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("sortBy", sortBy)
                .withQueryParam("order", order)
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Get active billers only
     */
    public Response getActiveBillers() {
        logger.info("Getting active billers");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("active", true)
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Search billers by name
     */
    public Response searchBillers(String searchTerm) {
        logger.info("Searching billers: {}", searchTerm);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("search", searchTerm)
                .get(BILLERS_ENDPOINT);
    }

    /**
     * Get biller categories
     * GET /v1/billers/categories
     */
    public Response getBillerCategories() {
        logger.info("Getting biller categories");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(BILLER_CATEGORIES_ENDPOINT);
    }

    /**
     * Get biller by ID
     * GET /v1/billers/{id}
     */
    public Response getBillerById(String billerId) {
        logger.info("Getting biller by ID: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billerId)
                .get(BILLER_BY_ID_ENDPOINT);
    }

    /**
     * Create new biller
     * POST /v1/billers
     */
    public Response createBiller(BillerInput billerInput) {
        logger.info("Creating new biller: {}", billerInput.getName());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withBody(billerInput)
                .post(BILLERS_ENDPOINT);
    }

    /**
     * Update biller (full replace)
     * PUT /v1/billers/{id}
     */
    public Response updateBiller(String billerId, BillerInput billerInput) {
        logger.info("Updating biller ID: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billerId)
                .withBody(billerInput)
                .put(BILLER_BY_ID_ENDPOINT);
    }

    /**
     * Partial update biller
     * PATCH /v1/billers/{id}
     */
    public Response patchBiller(String billerId, Object patchData) {
        logger.info("Partially updating biller ID: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billerId)
                .withBody(patchData)
                .patch(BILLER_BY_ID_ENDPOINT);
    }

    /**
     * Delete biller
     * DELETE /v1/billers/{id}
     */
    public Response deleteBiller(String billerId) {
        logger.info("Deleting biller ID: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billerId)
                .delete(BILLER_BY_ID_ENDPOINT);
    }

    /**
     * Check if biller exists
     * HEAD /v1/billers/{id}
     */
    public Response checkBillerExists(String billerId) {
        logger.info("Checking if biller exists: {}", billerId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", billerId)
                .head(BILLER_BY_ID_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all billers as list
     */
    public List<Biller> getAllBillersAsList() {
        Response response = getAllBillers();
        response.then().statusCode(200);
        ApiResponse<List<Biller>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), Biller.class);
        return apiResponse.getData();
    }

    /**
     * Get biller by ID as object
     */
    public Biller getBillerByIdAsObject(String billerId) {
        Response response = getBillerById(billerId);
        response.then().statusCode(200);
        ApiResponse<Biller> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Biller.class);
        return apiResponse.getData();
    }

    /**
     * Create biller and return created object
     */
    public Biller createBillerAndReturn(BillerInput billerInput) {
        Response response = createBiller(billerInput);
        response.then().statusCode(201);
        ApiResponse<Biller> apiResponse = ApiResponse.fromJson(response.getBody().asString(), Biller.class);
        return apiResponse.getData();
    }

    /**
     * Check if biller exists (returns boolean)
     */
    public boolean billerExists(String billerId) {
        try {
            Response response = checkBillerExists(billerId);
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
