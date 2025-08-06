package com.api.automation.tests.connectivity;

import com.api.automation.config.ConfigManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Basic connectivity tests to validate framework setup
 */
public class ConnectivityTest {

    @BeforeAll
    public static void setup() {
        ConfigManager configManager = ConfigManager.getInstance();
        baseURI = configManager.getProperty("base.url");
        requestSpecification = given()
                .contentType("application/json")
                .header("Accept", "application/json");
    }

    @Test
    @DisplayName("Test API Connectivity - Get All Users")
    public void testApiConnectivity() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Test Get Single User")
    public void testGetSingleUser() {
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("id", equalTo(1))
                .body("name", notNullValue())
                .body("email", notNullValue());
    }

    @Test
    @DisplayName("Test Get All Posts")
    public void testGetAllPosts() {
        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .contentType(containsString("application/json"))
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Test Invalid Endpoint")
    public void testInvalidEndpoint() {
        given()
                .when()
                .get("/invalid-endpoint")
                .then()
                .statusCode(404);
    }
}
