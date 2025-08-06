package com.api.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON Path utility class for JSON response validation and extraction
 */
public class JsonPathUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonPathUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract value from JSON response using JSONPath
     */
    public static <T> T extractValue(String jsonResponse, String jsonPath) {
        try {
            JsonPath path = JsonPath.from(jsonResponse);
            return path.get(jsonPath);
        } catch (Exception e) {
            logger.error("Error extracting value from JSON path '{}': {}", jsonPath, e.getMessage(), e);
            throw new RuntimeException("Error extracting value from JSON path: " + jsonPath, e);
        }
    }

    /**
     * Extract string value from JSON response
     */
    public static String extractString(String jsonResponse, String jsonPath) {
        return extractValue(jsonResponse, jsonPath);
    }

    /**
     * Extract integer value from JSON response
     */
    public static Integer extractInt(String jsonResponse, String jsonPath) {
        return extractValue(jsonResponse, jsonPath);
    }

    /**
     * Extract boolean value from JSON response
     */
    public static Boolean extractBoolean(String jsonResponse, String jsonPath) {
        return extractValue(jsonResponse, jsonPath);
    }

    /**
     * Extract list from JSON response
     */
    public static <T> List<T> extractList(String jsonResponse, String jsonPath) {
        return extractValue(jsonResponse, jsonPath);
    }

    /**
     * Extract map from JSON response
     */
    public static Map<String, Object> extractMap(String jsonResponse, String jsonPath) {
        return extractValue(jsonResponse, jsonPath);
    }

    /**
     * Check if JSON path exists in response
     */
    public static boolean pathExists(String jsonResponse, String jsonPath) {
        try {
            JsonPath path = JsonPath.from(jsonResponse);
            Object value = path.get(jsonPath);
            return value != null;
        } catch (Exception e) {
            logger.debug("JSON path '{}' does not exist or is invalid", jsonPath);
            return false;
        }
    }

    /**
     * Get all values for a given path (useful for arrays)
     */
    public static <T> List<T> getAllValues(String jsonResponse, String jsonPath) {
        try {
            JsonPath path = JsonPath.from(jsonResponse);
            return path.getList(jsonPath);
        } catch (Exception e) {
            logger.error("Error getting all values from JSON path '{}': {}", jsonPath, e.getMessage(), e);
            throw new RuntimeException("Error getting all values from JSON path: " + jsonPath, e);
        }
    }

    /**
     * Count elements at given JSON path
     */
    public static int countElements(String jsonResponse, String jsonPath) {
        try {
            JsonPath path = JsonPath.from(jsonResponse);
            List<Object> elements = path.getList(jsonPath);
            return elements != null ? elements.size() : 0;
        } catch (Exception e) {
            logger.error("Error counting elements at JSON path '{}': {}", jsonPath, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Validate JSON structure using JsonNode
     */
    public static boolean validateJsonStructure(String jsonResponse, String... requiredFields) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            
            for (String field : requiredFields) {
                if (!hasField(rootNode, field)) {
                    logger.warn("Required field '{}' not found in JSON response", field);
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            logger.error("Error validating JSON structure: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if JsonNode has a specific field (supports nested paths with dot notation)
     */
    private static boolean hasField(JsonNode node, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        JsonNode currentNode = node;
        
        for (String part : parts) {
            if (currentNode == null || !currentNode.has(part)) {
                return false;
            }
            currentNode = currentNode.get(part);
        }
        return true;
    }

    /**
     * Find all objects in array where field matches value
     */
    public static <T> List<T> findInArray(String jsonResponse, String arrayPath, String fieldName, Object fieldValue) {
        try {
            String searchPath = String.format("%s.findAll { it.%s == '%s' }", arrayPath, fieldName, fieldValue);
            return extractList(jsonResponse, searchPath);
        } catch (Exception e) {
            logger.error("Error finding objects in array: {}", e.getMessage(), e);
            throw new RuntimeException("Error finding objects in array", e);
        }
    }

    /**
     * Extract field values from all objects in an array
     */
    public static <T> List<T> extractFieldFromArray(String jsonResponse, String arrayPath, String fieldName) {
        try {
            String extractPath = String.format("%s.%s", arrayPath, fieldName);
            return extractList(jsonResponse, extractPath);
        } catch (Exception e) {
            logger.error("Error extracting field '{}' from array: {}", fieldName, e.getMessage(), e);
            throw new RuntimeException("Error extracting field from array: " + fieldName, e);
        }
    }

    /**
     * Check if array contains object with specific field value
     */
    public static boolean arrayContains(String jsonResponse, String arrayPath, String fieldName, Object fieldValue) {
        try {
            List<Object> results = findInArray(jsonResponse, arrayPath, fieldName, fieldValue);
            return !results.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if array contains value: {}", e.getMessage(), e);
            return false;
        }
    }
}
