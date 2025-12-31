package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Error response model for API errors in the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonProperty("success")
    @Builder.Default
    private Boolean success = false;

    @JsonProperty("error")
    private ErrorDetail error;

    /**
     * Nested error detail object
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {

        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("details")
        private List<ValidationError> details;

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("path")
        private String path;
    }

    /**
     * Validation error detail for field-level errors
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationError {

        @JsonProperty("field")
        private String field;

        @JsonProperty("message")
        private String message;

        @JsonProperty("rejectedValue")
        private Object rejectedValue;
    }

    /**
     * Quick check if this is an error response
     */
    public boolean isError() {
        return success != null && !success;
    }

    /**
     * Get error message if available
     */
    public String getErrorMessage() {
        return error != null ? error.getMessage() : null;
    }

    /**
     * Get error code if available
     */
    public String getErrorCode() {
        return error != null ? error.getCode() : null;
    }
}
