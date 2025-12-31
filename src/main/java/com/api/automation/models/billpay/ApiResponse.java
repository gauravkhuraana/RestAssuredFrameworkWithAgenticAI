package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic API response wrapper for the Bill Payment API
 * Handles the standard {success, data, meta} response structure
 * 
 * @param <T> The type of data contained in the response
 * 
 * Example usage:
 * <pre>
 * // For single object response
 * ApiResponse<Biller> response = ApiResponse.fromJson(json, Biller.class);
 * Biller biller = response.getData();
 * 
 * // For list response
 * ApiResponse<List<Biller>> listResponse = ApiResponse.fromJsonList(json, Biller.class);
 * List<Biller> billers = listResponse.getData();
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private T data;

    @JsonProperty("meta")
    private PaginationMeta meta;

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private ErrorResponse.ErrorDetail error;

    /**
     * Check if the response indicates success
     */
    public boolean isSuccessful() {
        return success != null && success;
    }

    /**
     * Check if the response has data
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Check if the response has pagination metadata
     */
    public boolean hasPagination() {
        return meta != null;
    }

    /**
     * Check if there are more pages
     */
    public boolean hasMorePages() {
        return meta != null && meta.hasNextPage();
    }

    /**
     * Get error message if present
     */
    public String getErrorMessage() {
        return error != null ? error.getMessage() : null;
    }

    /**
     * Parse JSON to ApiResponse with single object data
     * 
     * @param json The JSON string to parse
     * @param dataClass The class of the data object
     * @param <T> The type of data
     * @return Parsed ApiResponse
     */
    public static <T> ApiResponse<T> fromJson(String json, Class<T> dataClass) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructParametricType(ApiResponse.class, dataClass);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ApiResponse: " + e.getMessage(), e);
        }
    }

    /**
     * Parse JSON to ApiResponse with list data
     * 
     * @param json The JSON string to parse
     * @param elementClass The class of list elements
     * @param <T> The type of list elements
     * @return Parsed ApiResponse with List<T> data
     */
    public static <T> ApiResponse<List<T>> fromJsonList(String json, Class<T> elementClass) {
        try {
            JavaType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);
            JavaType responseType = objectMapper.getTypeFactory()
                    .constructParametricType(ApiResponse.class, listType);
            return objectMapper.readValue(json, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ApiResponse list: " + e.getMessage(), e);
        }
    }

    /**
     * Create a success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Create a success response with pagination
     */
    public static <T> ApiResponse<T> success(T data, PaginationMeta meta) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(meta)
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder().message(message).build())
                .build();
    }
}
