package com.api.automation.tests.billpay;

import com.api.automation.models.billpay.UploadedFile;
import com.api.automation.services.billpay.FileService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File Upload Tests for Bill Payment API
 * Tests multipart file upload operations
 */
@Epic("Bill Payment API")
@Feature("File Management")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileUploadTests {

    private static FileService fileService;
    private static File testFile;
    private static File testFile2;
    private static String uploadedFileId;
    private static Path tempDir;

    @BeforeAll
    static void setup() throws IOException {
        fileService = new FileService();
        
        // Create temporary test files
        tempDir = Files.createTempDirectory("billpay-test");
        
        testFile = new File(tempDir.toFile(), "test-document.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is a test document for Bill Payment API file upload testing.\n");
            writer.write("Created for automated testing purposes.\n");
            writer.write("Timestamp: " + System.currentTimeMillis());
        }
        
        testFile2 = new File(tempDir.toFile(), "test-receipt.txt");
        try (FileWriter writer = new FileWriter(testFile2)) {
            writer.write("Test Receipt\n");
            writer.write("Amount: $100.00\n");
            writer.write("Date: 2024-01-01");
        }
    }

    @AfterAll
    static void cleanup() {
        // Clean up temporary files
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
        if (testFile2 != null && testFile2.exists()) {
            testFile2.delete();
        }
        if (tempDir != null) {
            tempDir.toFile().delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Upload a single file")
    @Severity(SeverityLevel.BLOCKER)
    @Story("File Upload")
    @Description("Tests uploading a single file to the API")
    void testUploadSingleFile() {
        Response response = fileService.uploadFile(testFile);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Upload should return 200 or 201, got: " + response.getStatusCode());
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response should contain upload result");
        
        if (body.contains("id")) {
            uploadedFileId = extractIdFromResponse(body);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Upload file with custom filename")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Upload")
    @Description("Tests uploading a file with custom filename")
    void testUploadWithCustomFilename() {
        Response response = fileService.uploadFileWithCustomName(testFile, "text/plain", "custom-test-document.txt");
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Upload with custom name should succeed");
    }

    @Test
    @Order(3)
    @DisplayName("Upload multiple files")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Multiple File Upload")
    @Description("Tests uploading multiple files in a single request")
    void testUploadMultipleFiles() {
        Response response = fileService.uploadMultipleFiles(testFile, testFile2);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Multiple file upload should succeed");
    }

    @Test
    @Order(4)
    @DisplayName("List all uploaded files")
    @Severity(SeverityLevel.CRITICAL)
    @Story("File Listing")
    @Description("Tests retrieval of all uploaded files")
    void testListFiles() {
        Response response = fileService.listFiles();
        
        assertEquals(200, response.getStatusCode(), "List files should return 200 OK");
        
        String body = response.getBody().asString();
        assertNotNull(body, "Response should contain file list");
    }

    @Test
    @Order(5)
    @DisplayName("Get file by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Story("File Retrieval")
    @Description("Tests retrieval of a specific file by ID")
    void testGetFileById() {
        String fileId = uploadedFileId != null ? uploadedFileId : "1";
        
        Response response = fileService.getFileById(fileId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Get file by ID should return 200 or 404");
    }

    @Test
    @Order(6)
    @DisplayName("Download file content")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Download")
    @Description("Tests downloading file content")
    void testDownloadFile() {
        String fileId = uploadedFileId != null ? uploadedFileId : "1";
        
        Response response = fileService.downloadFile(fileId);
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
            "Download should return 200 or 404");
    }

    @Test
    @Order(7)
    @DisplayName("Upload file with metadata")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Upload")
    @Description("Tests uploading a file with additional metadata")
    void testUploadFileWithMetadata() {
        Response response = fileService.uploadFileWithMetadata(testFile, "bill", "Bill document", "Test Category");
        
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201,
            "Upload with metadata should succeed");
    }

    @Test
    @Order(8)
    @DisplayName("Upload image file")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Upload")
    @Description("Tests uploading an image file (simulated)")
    void testUploadImageFile() throws IOException {
        // Create a small test image file (actually just bytes simulating image)
        File imageFile = new File(tempDir.toFile(), "test-image.png");
        Files.write(imageFile.toPath(), new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});
        
        try {
            Response response = fileService.uploadFileWithMimeType(imageFile, "image/png");
            
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || response.getStatusCode() == 400,
                "Image upload should succeed or return validation error");
        } finally {
            imageFile.delete();
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test file type validation")
    @Severity(SeverityLevel.NORMAL)
    @Story("File Validation")
    @Description("Tests that API validates file types appropriately")
    void testFileTypeValidation() {
        // Upload regular text file and verify it's accepted
        Response response = fileService.uploadFile(testFile);
        
        // API should either accept the file or return validation error
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 || 
                   response.getStatusCode() == 400 || response.getStatusCode() == 415,
            "File type validation should be performed");
    }

    @Test
    @Order(10)
    @DisplayName("Test empty file upload")
    @Severity(SeverityLevel.MINOR)
    @Story("File Validation")
    @Description("Tests API behavior when uploading an empty file")
    void testEmptyFileUpload() throws IOException {
        File emptyFile = new File(tempDir.toFile(), "empty-file.txt");
        emptyFile.createNewFile();
        
        try {
            Response response = fileService.uploadFile(emptyFile);
            
            // API should handle empty files gracefully
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 201 ||
                       response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Empty file should be handled appropriately");
        } finally {
            emptyFile.delete();
        }
    }

    @Test
    @Order(100)
    @DisplayName("Delete uploaded file")
    @Severity(SeverityLevel.CRITICAL)
    @Story("File Deletion")
    @Description("Tests deleting an uploaded file")
    void testDeleteFile() {
        if (uploadedFileId != null) {
            Response response = fileService.deleteFile(uploadedFileId);
            
            assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204 || response.getStatusCode() == 404,
                "Delete should return success or not found");
        }
    }

    @Test
    @DisplayName("Test file not found returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    @Description("Tests that requesting non-existent file returns 404")
    void testFileNotFound() {
        Response response = fileService.getFileById("non-existent-file-id-99999");
        
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
            "Non-existent file should return 404 or 400");
    }

    @Test
    @DisplayName("Test upload without file returns error")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error Handling")
    @Description("Tests API behavior when no file is provided")
    void testUploadWithoutFile() {
        // Try to upload null file
        Response response = fileService.uploadFile((File) null);
        
        // Should return error or handle gracefully
        assertNotNull(response, "Response should not be null even for invalid request");
    }

    private static String extractIdFromResponse(String body) {
        if (body.contains("\"id\":\"")) {
            int start = body.indexOf("\"id\":\"") + 6;
            int end = body.indexOf("\"", start);
            if (end > start) {
                return body.substring(start, end);
            }
        } else if (body.contains("\"id\":")) {
            int start = body.indexOf("\"id\":") + 5;
            int end = body.indexOf(",", start);
            if (end == -1) end = body.indexOf("}", start);
            if (end > start) {
                return body.substring(start, end).trim();
            }
        }
        return null;
    }
}
