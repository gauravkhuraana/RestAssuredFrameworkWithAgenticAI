package com.api.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager to handle environment-specific properties
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;
    private String environment;

    private ConfigManager() {
        this.environment = System.getProperty("env", "dev");
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        String configFile = String.format("config/%s.properties", environment);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                logger.error("Configuration file not found: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
            properties.load(inputStream);
            logger.info("Loaded configuration for environment: {}", environment);
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", configFile, e);
            throw new RuntimeException("Error loading configuration file: " + configFile, e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer property: {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public String getEnvironment() {
        return environment;
    }

    // Convenience methods for common properties
    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public int getApiTimeout() {
        return getIntProperty("api.timeout", 30000);
    }

    public int getRetryAttempts() {
        return getIntProperty("retry.attempts", 3);
    }

    public int getRetryDelay() {
        return getIntProperty("retry.delay", 1000);
    }

    public String getAuthType() {
        return getProperty("auth.type", "bearer");
    }

    public String getAuthToken() {
        return getProperty("auth.token");
    }

    public String getAuthUsername() {
        return getProperty("auth.username");
    }

    public String getAuthPassword() {
        return getProperty("auth.password");
    }

    public String getApiKey() {
        return getProperty("auth.api.key");
    }
}
