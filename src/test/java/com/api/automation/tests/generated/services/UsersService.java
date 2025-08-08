package com.api.automation.tests.generated.services;

import com.api.automation.client.BaseApiClient;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated service class for Users
 * Generated from Swagger/OpenAPI specification
 */
public class UsersService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    /**
     * Get all users
     */
    public Response getusers() {
        logger.info("Calling GET /users");
        return getRequestSpec()
                .when()
                .get("/users");
    }

    /**
     * Create a new user
     */
    public Response postusers() {
        logger.info("Calling POST /users");
        return getRequestSpec()
                .when()
                .post("/users");
    }

    /**
     * Get user by ID
     */
    public Response getusersid() {
        logger.info("Calling GET /users/{id}");
        return getRequestSpec()
                .when()
                .get("/users/{id}");
    }

    /**
     * Update user
     */
    public Response putusersid() {
        logger.info("Calling PUT /users/{id}");
        return getRequestSpec()
                .when()
                .put("/users/{id}");
    }

    /**
     * Delete user
     */
    public Response deleteusersid() {
        logger.info("Calling DELETE /users/{id}");
        return getRequestSpec()
                .when()
                .delete("/users/{id}");
    }

    /**
     * Search users by criteria
     */
    public Response getuserssearch() {
        logger.info("Calling GET /users/search");
        return getRequestSpec()
                .when()
                .get("/users/search");
    }

    /**
     * Bulk create users
     */
    public Response postusersbulk() {
        logger.info("Calling POST /users/bulk");
        return getRequestSpec()
                .when()
                .post("/users/bulk");
    }

    /**
     * Upload user avatar
     */
    public Response postusersidavatar() {
        logger.info("Calling POST /users/{id}/avatar");
        return getRequestSpec()
                .when()
                .post("/users/{id}/avatar");
    }

    /**
     * Get user profile
     */
    public Response getusersidprofile() {
        logger.info("Calling GET /users/{id}/profile");
        return getRequestSpec()
                .when()
                .get("/users/{id}/profile");
    }

}
