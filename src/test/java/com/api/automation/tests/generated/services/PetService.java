package com.api.automation.tests.generated.services;

import com.api.automation.client.BaseApiClient;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated service class for Pet
 * Generated from Swagger/OpenAPI specification
 */
public class PetService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    /**
     * uploads an image
     */
    public Response uploadFile() {
        logger.info("Calling POST /pet/{petId}/uploadImage");
        return getRequestSpec()
                .when()
                .post("/pet/{petId}/uploadImage");
    }

    /**
     * Add a new pet to the store
     */
    public Response addPet() {
        logger.info("Calling POST /pet");
        return getRequestSpec()
                .when()
                .post("/pet");
    }

    /**
     * Update an existing pet
     */
    public Response updatePet() {
        logger.info("Calling PUT /pet");
        return getRequestSpec()
                .when()
                .put("/pet");
    }

    /**
     * Finds Pets by status
     */
    public Response findPetsByStatus() {
        logger.info("Calling GET /pet/findByStatus");
        return getRequestSpec()
                .when()
                .get("/pet/findByStatus");
    }

    /**
     * Finds Pets by tags
     */
    public Response findPetsByTags() {
        logger.info("Calling GET /pet/findByTags");
        return getRequestSpec()
                .when()
                .get("/pet/findByTags");
    }

    /**
     * Find pet by ID
     */
    public Response getPetById() {
        logger.info("Calling GET /pet/{petId}");
        return getRequestSpec()
                .when()
                .get("/pet/{petId}");
    }

    /**
     * Updates a pet in the store with form data
     */
    public Response updatePetWithForm() {
        logger.info("Calling POST /pet/{petId}");
        return getRequestSpec()
                .when()
                .post("/pet/{petId}");
    }

    /**
     * Deletes a pet
     */
    public Response deletePet() {
        logger.info("Calling DELETE /pet/{petId}");
        return getRequestSpec()
                .when()
                .delete("/pet/{petId}");
    }

}
