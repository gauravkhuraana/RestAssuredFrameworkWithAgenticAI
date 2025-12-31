package com.api.automation.tests.generated.services;

import com.api.automation.client.BaseApiClient;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated service class for User
 * Generated from Swagger/OpenAPI specification
 */
public class UserService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Creates list of users with given input array
     */
    public Response createUsersWithListInput() {
        logger.info("Calling POST /user/createWithList");
        return getRequestSpec()
                .when()
                .post("/user/createWithList");
    }

    /**
     * Get user by user name
     */
    public Response getUserByName() {
        logger.info("Calling GET /user/{username}");
        return getRequestSpec()
                .when()
                .get("/user/{username}");
    }

    /**
     * Updated user
     */
    public Response updateUser() {
        logger.info("Calling PUT /user/{username}");
        return getRequestSpec()
                .when()
                .put("/user/{username}");
    }

    /**
     * Delete user
     */
    public Response deleteUser() {
        logger.info("Calling DELETE /user/{username}");
        return getRequestSpec()
                .when()
                .delete("/user/{username}");
    }

    /**
     * Logs user into the system
     */
    public Response loginUser() {
        logger.info("Calling GET /user/login");
        return getRequestSpec()
                .when()
                .get("/user/login");
    }

    /**
     * Logs out current logged in user session
     */
    public Response logoutUser() {
        logger.info("Calling GET /user/logout");
        return getRequestSpec()
                .when()
                .get("/user/logout");
    }

    /**
     * Creates list of users with given input array
     */
    public Response createUsersWithArrayInput() {
        logger.info("Calling POST /user/createWithArray");
        return getRequestSpec()
                .when()
                .post("/user/createWithArray");
    }

    /**
     * Create user
     */
    public Response createUser() {
        logger.info("Calling POST /user");
        return getRequestSpec()
                .when()
                .post("/user");
    }

}
