package com.api.automation.tests.generated.smoke;

import com.api.automation.tests.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

/**
 * Generated test class for PetSmoke
 * Generated from Swagger/OpenAPI specification
 */
@Tag("smoke")
@Tag("generated")
public class PetSmokeTest extends BaseTest {

    @Test
    @DisplayName("Find pet by ID")
    void testGetpetpetId() {
        logStep("Send GET request to /pet/{petId}");
        
        Response response = RestAssured.given()
                .when()
                .get("/pet/{petId}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Find pet by ID test passed");
    }

    @Test
    @DisplayName("Updates a pet in the store with form data")
    void testPostpetpetId() {
        logStep("Send POST request to /pet/{petId}");
        
        Response response = RestAssured.given()
                .when()
                .post("/pet/{petId}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Updates a pet in the store with form data test passed");
    }

    @Test
    @DisplayName("Deletes a pet")
    void testDeletepetpetId() {
        logStep("Send DELETE request to /pet/{petId}");
        
        Response response = RestAssured.given()
                .when()
                .delete("/pet/{petId}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200); // TODO: Add proper validations
        
        logVerification("Deletes a pet test passed");
    }

}
