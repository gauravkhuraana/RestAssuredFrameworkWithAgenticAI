package com.api.automation.models.billpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UploadedFile entity model representing files uploaded via the Bill Payment API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadedFile {

    @JsonProperty("id")
    private String id;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("originalName")
    private String originalName;

    @JsonProperty("mimeType")
    private String mimeType;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("url")
    private String url;

    @JsonProperty("thumbnailUrl")
    private String thumbnailUrl;

    @JsonProperty("uploadedBy")
    private String uploadedBy;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("metadata")
    private Object metadata;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String createdAt;

    /**
     * Get file size in human readable format
     */
    public String getHumanReadableSize() {
        if (size == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }

    /**
     * Check if file is an image
     */
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * Check if file is a PDF
     */
    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }
}
