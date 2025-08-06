package com.api.automation.tests.smoke;

import com.api.automation.models.User;
import com.api.automation.services.UserService;
import com.api.automation.tests.base.BaseTest;
import com.api.automation.utils.JsonPathUtils;
import com.api.automation.utils.TestDataUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for User API endpoints
 */
@Tag("smoke")
@Tag("users")
@Epic("User Management")
@Feature("User API")
public class UserSmokeTest extends BaseTest {

    private final UserService userService = new UserService();

    @Test
    @DisplayName("Get All Users - Smoke Test")
    @Description("Verify that the get all users endpoint returns a successful response")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Get All Users")
    void testGetAllUsers() {
        logStep("Send GET request to retrieve all users");
        
        Response response = userService.getAllUsers();
        
        logStep("Verify response status code is 200");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0));
        
        logVerification("Successfully retrieved all users with status code 200");
        
        // Additional validations
        String responseBody = response.getBody().asString();
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].id"), "First user should have an ID");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].name"), "First user should have a name");
        assertTrue(JsonPathUtils.pathExists(responseBody, "[0].email"), "First user should have an email");
        
        logVerification("All required fields are present in the response");
    }

    @Test
    @DisplayName("Get User by ID - Smoke Test")
    @Description("Verify that getting a user by ID returns the correct user")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get User by ID")
    void testGetUserById() {
        Long userId = 1L;
        
        logStep("Send GET request to retrieve user with ID: " + userId);
        
        Response response = userService.getUserById(userId);
        
        logStep("Verify response status code is 200 and user data is correct");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(userId.intValue()))
                .body("name", notNullValue())
                .body("email", notNullValue());
        
        logVerification("Successfully retrieved user by ID with correct data");
        
        // Parse response to User object
        User user = userService.getUserByIdAsObject(userId);
        assertNotNull(user, "User object should not be null");
        assertEquals(userId, user.getId(), "User ID should match requested ID");
        assertNotNull(user.getName(), "User name should not be null");
        assertNotNull(user.getEmail(), "User email should not be null");
        
        logVerification("User object deserialization successful");
    }

    @Test
    @DisplayName("Create User - Smoke Test")
    @Description("Verify that a new user can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create User")
    void testCreateUser() {
        logStep("Prepare test data for new user");
        
        // Load test data from JSON file
        List<User> testUsers = TestDataUtils.readJsonTestDataAsList("users.json", User.class);
        User newUser = testUsers.get(0); // Use first test user
        
        logStep("Send POST request to create new user: " + newUser.getUsername());
        
        Response response = userService.createUser(newUser);
        
        logStep("Verify response status code is 201 and user is created");
        response.then()
                .statusCode(201)
                .contentType("application/json")
                .body("name", equalTo(newUser.getName()))
                .body("username", equalTo(newUser.getUsername()))
                .body("email", equalTo(newUser.getEmail()));
        
        logVerification("User created successfully with status code 201");
        
        // Verify the created user object
        User createdUser = userService.createUserAndReturn(newUser);
        assertNotNull(createdUser.getId(), "Created user should have an ID");
        assertEquals(newUser.getName(), createdUser.getName(), "Names should match");
        assertEquals(newUser.getEmail(), createdUser.getEmail(), "Emails should match");
        
        logVerification("Created user data matches the input data");
    }

    @Test
    @DisplayName("Update User - Smoke Test")
    @Description("Verify that an existing user can be updated successfully")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update User")
    void testUpdateUser() {
        Long userId = 1L;
        
        logStep("Prepare updated user data");
        
        User updatedUser = new User();
        updatedUser.setName("Updated Test User");
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        
        logStep("Send PUT request to update user with ID: " + userId);
        
        Response response = userService.updateUser(userId, updatedUser);
        
        logStep("Verify response status code is 200 and user is updated");
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(userId.intValue()))
                .body("name", equalTo(updatedUser.getName()))
                .body("username", equalTo(updatedUser.getUsername()))
                .body("email", equalTo(updatedUser.getEmail()));
        
        logVerification("User updated successfully with correct data");
    }

    @Test
    @DisplayName("Delete User - Smoke Test")
    @Description("Verify that an existing user can be deleted successfully")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete User")
    void testDeleteUser() {
        Long userId = 1L;
        
        logStep("Send DELETE request to delete user with ID: " + userId);
        
        Response response = userService.deleteUser(userId);
        
        logStep("Verify response status code is 200");
        response.then()
                .statusCode(200);
        
        logVerification("User deleted successfully");
    }

    @Test
    @DisplayName("Invalid User ID - Error Handling")
    @Description("Verify proper error handling for invalid user ID")
    @Severity(SeverityLevel.MINOR)
    @Story("Error Handling")
    void testInvalidUserId() {
        Long invalidUserId = 99999L;
        
        logStep("Send GET request with invalid user ID: " + invalidUserId);
        
        Response response = userService.getUserById(invalidUserId);
        
        logStep("Verify response status code is 404 for invalid user ID");
        response.then()
                .statusCode(404);
        
        logVerification("Proper error handling for invalid user ID");
    }
}
