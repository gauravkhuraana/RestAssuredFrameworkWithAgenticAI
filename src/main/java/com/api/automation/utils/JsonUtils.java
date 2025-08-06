package com.api.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JSON utility class for serialization and deserialization
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    /**
     * Convert object to JSON string using Jackson
     */
    public static String objectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error converting object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Convert JSON string to object using Jackson
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Error converting JSON to object: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }

    /**
     * Convert JSON string to List of objects using Jackson
     */
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            logger.error("Error converting JSON to List: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting JSON to List", e);
        }
    }

    /**
     * Convert object to JSON string using Gson
     */
    public static String objectToJsonGson(Object object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            logger.error("Error converting object to JSON using Gson: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting object to JSON using Gson", e);
        }
    }

    /**
     * Convert JSON string to object using Gson
     */
    public static <T> T jsonToObjectGson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            logger.error("Error converting JSON to object using Gson: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting JSON to object using Gson", e);
        }
    }

    /**
     * Pretty print JSON string
     */
    public static String prettyPrintJson(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            logger.error("Error pretty printing JSON: {}", e.getMessage(), e);
            return json; // Return original if formatting fails
        }
    }

    /**
     * Validate if string is valid JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get ObjectMapper instance for custom configurations
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Get Gson instance for custom configurations
     */
    public static Gson getGson() {
        return gson;
    }
}
