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
    public Response getAllUsers() {
        logger.info("Calling GET /users");
        return getRequestSpec()
                .when()
                .get("/users");
    }

    /**
     * Create a new user
     */
    public Response createUser() {
        logger.info("Calling POST /users");
        return getRequestSpec()
                .when()
                .post("/users");
    }

    /**
     * Get user by ID
     */
    public Response getUserById() {
        logger.info("Calling GET /users/{id}");
        return getRequestSpec()
                .when()
                .get("/users/{id}");
    }

    /**
     * Update user
     */
    public Response updateUser() {
        logger.info("Calling PUT /users/{id}");
        return getRequestSpec()
                .when()
                .put("/users/{id}");
    }

    /**
     * Delete user
     */
    public Response deleteUser() {
        logger.info("Calling DELETE /users/{id}");
        return getRequestSpec()
                .when()
                .delete("/users/{id}");
    }

}
