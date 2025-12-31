package com.api.automation.tests.generated.services;

import com.api.automation.client.BaseApiClient;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated service class for Store
 * Generated from Swagger/OpenAPI specification
 */
public class StoreService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    /**
     * Returns pet inventories by status
     */
    public Response getInventory() {
        logger.info("Calling GET /store/inventory");
        return getRequestSpec()
                .when()
                .get("/store/inventory");
    }

    /**
     * Place an order for a pet
     */
    public Response placeOrder() {
        logger.info("Calling POST /store/order");
        return getRequestSpec()
                .when()
                .post("/store/order");
    }

    /**
     * Find purchase order by ID
     */
    public Response getOrderById() {
        logger.info("Calling GET /store/order/{orderId}");
        return getRequestSpec()
                .when()
                .get("/store/order/{orderId}");
    }

    /**
     * Delete purchase order by ID
     */
    public Response deleteOrder() {
        logger.info("Calling DELETE /store/order/{orderId}");
        return getRequestSpec()
                .when()
                .delete("/store/order/{orderId}");
    }

}
