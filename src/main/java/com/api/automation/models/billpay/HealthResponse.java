package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Health check response model
 * API returns: {"success":true, "data": {...health data...}, "meta": {...}}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private HealthData data;

    @JsonProperty("meta")
    private Object meta;

    /**
     * Health data details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthData {

        @JsonProperty("status")
        private String status;

        @JsonProperty("service")
        private String service;

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("version")
        private String version;

        @JsonProperty("uptime")
        private Long uptime;

        @JsonProperty("database")
        private DatabaseHealth database;
    }

    /**
     * Database health details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabaseHealth {

        @JsonProperty("status")
        private String status;

        @JsonProperty("responseTime")
        private Long responseTime;

        @JsonProperty("connectionPool")
        private ConnectionPool connectionPool;
    }

    /**
     * Connection pool details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectionPool {

        @JsonProperty("active")
        private Integer active;

        @JsonProperty("idle")
        private Integer idle;

        @JsonProperty("max")
        private Integer max;
    }

    /**
     * Check if service is healthy
     */
    public boolean isHealthy() {
        return data != null && ("healthy".equalsIgnoreCase(data.getStatus()) || "ok".equalsIgnoreCase(data.getStatus()));
    }

    /**
     * Get the status string
     */
    public String getStatus() {
        return data != null ? data.getStatus() : null;
    }

    /**
     * Get version
     */
    public String getVersion() {
        return data != null ? data.getVersion() : null;
    }
}
