package com.api.automation.tests.generated.smoke;

import com.api.automation.tests.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

/**
 * Generated test class for UsersSmoke
 * Generated from Swagger/OpenAPI specification
 */
@Tag("smoke")
@Tag("generated")
public class UsersSmokeTest extends BaseTest {

    @Test
    @DisplayName("Get user profile")
    void testGetusersidprofile() {
        logStep("Send GET request to /users/{id}/profile");
        
        Response response = RestAssured.given()
                .when()
                .get("/users/{id}/profile");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Get user profile test passed");
    }

}
