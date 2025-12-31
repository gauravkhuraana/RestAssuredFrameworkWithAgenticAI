package com.api.automation.tests.generated.smoke;

import com.api.automation.tests.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

/**
 * Generated test class for UserSmoke
 * Generated from Swagger/OpenAPI specification
 */
@Tag("smoke")
@Tag("generated")
public class UserSmokeTest extends BaseTest {

    @Test
    @DisplayName("Create user")
    void testPostuser() {
        logStep("Send POST request to /user");
        
        Response response = RestAssured.given()
                .when()
                .post("/user");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Create user test passed");
    }

}
