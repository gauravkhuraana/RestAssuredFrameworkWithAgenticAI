package com.api.automation.services;

import com.api.automation.client.BaseApiClient;
import com.api.automation.models.User;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User service for user-related API operations
 */
public class UserService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USERS_ENDPOINT = "/users";

    /**
     * Get all users
     */
    public Response getAllUsers() {
        logger.info("Getting all users");
        return get(USERS_ENDPOINT);
    }

    /**
     * Get user by ID
     */
    public Response getUserById(Long userId) {
        logger.info("Getting user by ID: {}", userId);
        return withPathParam("id", userId)
                .get(USERS_ENDPOINT + "/{id}");
    }

    /**
     * Create new user
     */
    public Response createUser(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        return withBody(user)
                .post(USERS_ENDPOINT);
    }

    /**
     * Update user
     */
    public Response updateUser(Long userId, User user) {
        logger.info("Updating user ID: {}", userId);
        return withPathParam("id", userId)
                .withBody(user)
                .put(USERS_ENDPOINT + "/{id}");
    }

    /**
     * Partially update user
     */
    public Response patchUser(Long userId, User user) {
        logger.info("Partially updating user ID: {}", userId);
        return withPathParam("id", userId)
                .withBody(user)
                .patch(USERS_ENDPOINT + "/{id}");
    }

    /**
     * Delete user
     */
    public Response deleteUser(Long userId) {
        logger.info("Deleting user ID: {}", userId);
        return withPathParam("id", userId)
                .delete(USERS_ENDPOINT + "/{id}");
    }

    /**
     * Get all users and parse to User objects
     */
    public List<User> getAllUsersAsList() {
        Response response = getAllUsers();
        response.then().statusCode(200);
        return JsonUtils.jsonToList(response.getBody().asString(), User.class);
    }

    /**
     * Get user by ID and parse to User object
     */
    public User getUserByIdAsObject(Long userId) {
        Response response = getUserById(userId);
        response.then().statusCode(200);
        return JsonUtils.jsonToObject(response.getBody().asString(), User.class);
    }

    /**
     * Create user and return created User object
     */
    public User createUserAndReturn(User user) {
        Response response = createUser(user);
        response.then().statusCode(201);
        return JsonUtils.jsonToObject(response.getBody().asString(), User.class);
    }

    /**
     * Search users by username
     */
    public Response searchUsersByUsername(String username) {
        logger.info("Searching users by username: {}", username);
        return withQueryParam("username", username)
                .get(USERS_ENDPOINT);
    }

    /**
     * Search users by email
     */
    public Response searchUsersByEmail(String email) {
        logger.info("Searching users by email: {}", email);
        return withQueryParam("email", email)
                .get(USERS_ENDPOINT);
    }
}
