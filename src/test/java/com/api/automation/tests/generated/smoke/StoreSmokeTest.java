package com.api.automation.tests.generated.smoke;

import com.api.automation.tests.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

/**
 * Generated test class for StoreSmoke
 * Generated from Swagger/OpenAPI specification
 */
@Tag("smoke")
@Tag("generated")
public class StoreSmokeTest extends BaseTest {

    @Test
    @DisplayName("Find purchase order by ID")
    void testGetstoreorderorderId() {
        logStep("Send GET request to /store/order/{orderId}");
        
        Response response = RestAssured.given()
                .when()
                .get("/store/order/{orderId}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Find purchase order by ID test passed");
    }

    @Test
    @DisplayName("Delete purchase order by ID")
    void testDeletestoreorderorderId() {
        logStep("Send DELETE request to /store/order/{orderId}");
        
        Response response = RestAssured.given()
                .when()
                .delete("/store/order/{orderId}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Delete purchase order by ID test passed");
    }

}
