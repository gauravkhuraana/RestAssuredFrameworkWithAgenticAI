package com.api.automation.services.billpay;

import com.api.automation.auth.AuthHandler;
import com.api.automation.client.BaseApiClient;
import com.api.automation.models.billpay.ApiResponse;
import com.api.automation.models.billpay.UploadedFile;
import com.api.automation.utils.JsonUtils;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * File Service for Bill Payment API file upload/download endpoints
 */
public class FileService extends BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    
    private static final String FILES_ENDPOINT = "/v1/files";
    private static final String FILE_UPLOAD_ENDPOINT = "/v1/files/upload";
    private static final String FILE_UPLOAD_MULTIPLE_ENDPOINT = "/v1/files/upload-multiple";
    private static final String FILE_BY_ID_ENDPOINT = "/v1/files/{id}";

    /**
     * List all uploaded files
     * GET /v1/files
     */
    public Response getAllFiles() {
        logger.info("Getting all uploaded files");
        return withAuth(AuthHandler.AuthType.API_KEY)
                .get(FILES_ENDPOINT);
    }

    /**
     * List files - alias for getAllFiles
     */
    public Response listFiles() {
        return getAllFiles();
    }

    /**
     * Download file content
     * GET /v1/files/{id}/download
     */
    public Response downloadFile(String fileId) {
        logger.info("Downloading file ID: {}", fileId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", fileId)
                .get("/v1/files/{id}/download");
    }

    /**
     * Upload file with metadata
     */
    public Response uploadFileWithMetadata(File file, String purpose, String description, String category) {
        logger.info("Uploading file with metadata: {}", file.getName());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", file)
                .withFormParam("purpose", purpose)
                .withFormParam("description", description)
                .withFormParam("category", category)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Upload file with custom mime type
     */
    public Response uploadFileWithMimeType(File file, String mimeType) {
        logger.info("Uploading file with mime type: {} - {}", file.getName(), mimeType);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", file, mimeType)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Upload file with custom filename
     */
    public Response uploadFileWithCustomName(File file, String mimeType, String filename) {
        logger.info("Uploading file with custom name: {}", filename);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", file, mimeType)
                .withFormParam("filename", filename)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * List files with pagination
     */
    public Response getFiles(int page, int limit) {
        logger.info("Getting files - page: {}, limit: {}", page, limit);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withQueryParam("page", page)
                .withQueryParam("limit", limit)
                .get(FILES_ENDPOINT);
    }

    /**
     * Get file metadata by ID
     * GET /v1/files/{id}
     */
    public Response getFileById(String fileId) {
        logger.info("Getting file metadata by ID: {}", fileId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", fileId)
                .get(FILE_BY_ID_ENDPOINT);
    }

    /**
     * Upload a single file
     * POST /v1/files/upload
     */
    public Response uploadFile(File file) {
        logger.info("Uploading file: {}", file.getName());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", file)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Upload a single file with custom control name
     */
    public Response uploadFile(String controlName, File file) {
        logger.info("Uploading file with control name '{}': {}", controlName, file.getName());
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart(controlName, file)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Upload a single file with purpose
     */
    public Response uploadFile(File file, String purpose) {
        logger.info("Uploading file: {} for purpose: {}", file.getName(), purpose);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", file)
                .withFormParam("purpose", purpose)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Upload multiple files
     * POST /v1/files/upload-multiple
     */
    public Response uploadMultipleFiles(File... files) {
        logger.info("Uploading {} files", files.length);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiParts("files", files)
                .post(FILE_UPLOAD_MULTIPLE_ENDPOINT);
    }

    /**
     * Upload multiple files with purpose
     */
    public Response uploadMultipleFiles(String purpose, File... files) {
        logger.info("Uploading {} files for purpose: {}", files.length, purpose);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiParts("files", files)
                .withFormParam("purpose", purpose)
                .post(FILE_UPLOAD_MULTIPLE_ENDPOINT);
    }

    /**
     * Upload file from byte array
     */
    public Response uploadFile(String fileName, byte[] content, String mimeType) {
        logger.info("Uploading file from bytes: {}", fileName);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withMultiPart("file", fileName, content, mimeType)
                .post(FILE_UPLOAD_ENDPOINT);
    }

    /**
     * Delete a file
     * DELETE /v1/files/{id}
     */
    public Response deleteFile(String fileId) {
        logger.info("Deleting file ID: {}", fileId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", fileId)
                .delete(FILE_BY_ID_ENDPOINT);
    }

    /**
     * Check if file exists
     * HEAD /v1/files/{id}
     */
    public Response checkFileExists(String fileId) {
        logger.info("Checking if file exists: {}", fileId);
        return withAuth(AuthHandler.AuthType.API_KEY)
                .withPathParam("id", fileId)
                .head(FILE_BY_ID_ENDPOINT);
    }

    // ============ Convenience methods with object parsing ============

    /**
     * Get all files as list
     */
    public List<UploadedFile> getAllFilesAsList() {
        Response response = getAllFiles();
        response.then().statusCode(200);
        ApiResponse<List<UploadedFile>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), UploadedFile.class);
        return apiResponse.getData();
    }

    /**
     * Get file by ID as object
     */
    public UploadedFile getFileByIdAsObject(String fileId) {
        Response response = getFileById(fileId);
        response.then().statusCode(200);
        ApiResponse<UploadedFile> apiResponse = ApiResponse.fromJson(response.getBody().asString(), UploadedFile.class);
        return apiResponse.getData();
    }

    /**
     * Upload file and return uploaded file object
     */
    public UploadedFile uploadFileAndReturn(File file) {
        Response response = uploadFile(file);
        response.then().statusCode(201);
        ApiResponse<UploadedFile> apiResponse = ApiResponse.fromJson(response.getBody().asString(), UploadedFile.class);
        return apiResponse.getData();
    }

    /**
     * Upload multiple files and return list of uploaded files
     */
    public List<UploadedFile> uploadMultipleFilesAndReturn(File... files) {
        Response response = uploadMultipleFiles(files);
        response.then().statusCode(201);
        ApiResponse<List<UploadedFile>> apiResponse = ApiResponse.fromJsonList(response.getBody().asString(), UploadedFile.class);
        return apiResponse.getData();
    }

    /**
     * Check if file exists (returns boolean)
     */
    public boolean fileExists(String fileId) {
        try {
            Response response = checkFileExists(fileId);
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
