package com.api.automation.retry;

import com.api.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Retry mechanism implementation
 */
public class RetryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Execute operation with retry logic
     */
    public static <T> T executeWithRetry(Supplier<T> operation, int maxAttempts, long delayMs) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Executing operation, attempt {}/{}", attempt, maxAttempts);
                T result = operation.get();
                if (attempt > 1) {
                    logger.info("Operation succeeded on attempt {}/{}", attempt, maxAttempts);
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                
                // Enhanced null safety for exception handling
                String exceptionType = e != null ? e.getClass().getSimpleName() : "Unknown";
                String exceptionMessage = e != null ? e.getMessage() : "No message available";
                
                // Check if this is a type of exception we should not retry on
                if (shouldNotRetry(e)) {
                    logger.info("Exception type {} should not be retried, failing immediately", exceptionType);
                    throw e;
                }
                
                logger.warn("Operation failed on attempt {}/{}: {} - {}", attempt, maxAttempts, 
                    exceptionType, exceptionMessage);
                
                if (attempt < maxAttempts) {
                    try {
                        long waitTime = delayMs * attempt; // Exponential backoff
                        logger.debug("Waiting {}ms before retry", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry delay", ie);
                    }
                }
            }
        }
        
        // Enhanced error reporting with null safety
        String lastExceptionInfo = "Unknown error";
        if (lastException != null) {
            String exceptionType = lastException.getClass().getSimpleName();
            String message = lastException.getMessage() != null ? lastException.getMessage() : "No message";
            lastExceptionInfo = exceptionType + ": " + message;
        }
        
        logger.error("Operation failed after {} attempts. Last exception: {}", maxAttempts, lastExceptionInfo);
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }

    /**
     * Check if an exception should not be retried
     */
    private static boolean shouldNotRetry(Exception e) {
        // Don't retry on assertion errors, illegal arguments, etc.
        if (e instanceof RuntimeException && 
            (e.getClass().getSimpleName().contains("AssertionError") ||
             e instanceof IllegalArgumentException ||
             e instanceof IllegalStateException ||
             e instanceof UnsupportedOperationException)) {
            return true;
        }
        
        // Don't retry on valid HTTP response codes (4xx client errors)
        // These are expected responses and should not be retried
        String message = e.getMessage();
        if (message != null) {
            // Check for common HTTP status codes that shouldn't be retried
            return message.contains("404") || 
                   message.contains("400") || 
                   message.contains("401") || 
                   message.contains("403") || 
                   message.contains("405") ||
                   message.contains("409") ||
                   message.contains("422") ||
                   message.contains("status code") ||
                   message.contains("Expected status code");
        }
        
        return false;
    }

    /**
     * Execute operation with default retry configuration
     */
    public static <T> T executeWithRetry(Supplier<T> operation) {
        int maxAttempts = config.getRetryAttempts();
        long delayMs = config.getRetryDelay();
        return executeWithRetry(operation, maxAttempts, delayMs);
    }

    /**
     * Execute operation with retry for specific exceptions
     */
    public static <T> T executeWithRetry(Supplier<T> operation, int maxAttempts, long delayMs, 
                                       Class<? extends Throwable>[] retryOn, 
                                       Class<? extends Throwable>[] ignoreOn) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                logger.debug("Executing operation, attempt {}/{}", attempt, maxAttempts);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                
                // Check if exception should be ignored (no retry)
                if (shouldIgnoreException(e, ignoreOn)) {
                    logger.info("Exception is in ignore list, not retrying: {}", e.getClass().getSimpleName());
                    throw e;
                }
                
                // Check if exception should trigger retry
                if (!shouldRetryException(e, retryOn)) {
                    logger.info("Exception is not in retry list, not retrying: {}", e.getClass().getSimpleName());
                    throw e;
                }
                
                logger.warn("Operation failed on attempt {}/{}: {}", attempt, maxAttempts, e.getMessage());
                
                if (attempt < maxAttempts) {
                    try {
                        logger.debug("Waiting {}ms before retry", delayMs);
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry delay", ie);
                    }
                }
            }
        }
        
        logger.error("Operation failed after {} attempts", maxAttempts);
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }

    /**
     * Execute void operation with retry
     */
    public static void executeWithRetry(Runnable operation, int maxAttempts, long delayMs) {
        executeWithRetry(() -> {
            operation.run();
            return null;
        }, maxAttempts, delayMs);
    }

    /**
     * Execute void operation with default retry configuration
     */
    public static void executeWithRetry(Runnable operation) {
        int maxAttempts = config.getRetryAttempts();
        long delayMs = config.getRetryDelay();
        executeWithRetry(operation, maxAttempts, delayMs);
    }

    /**
     * Check if exception should trigger retry
     */
    private static boolean shouldRetryException(Exception exception, Class<? extends Throwable>[] retryOn) {
        if (retryOn == null || retryOn.length == 0) {
            return true; // Retry on all exceptions if none specified
        }
        
        return Arrays.stream(retryOn)
                .anyMatch(clazz -> clazz.isAssignableFrom(exception.getClass()));
    }

    /**
     * Check if exception should be ignored (no retry)
     */
    private static boolean shouldIgnoreException(Exception exception, Class<? extends Throwable>[] ignoreOn) {
        if (ignoreOn == null || ignoreOn.length == 0) {
            return false; // Don't ignore any exceptions if none specified
        }
        
        return Arrays.stream(ignoreOn)
                .anyMatch(clazz -> clazz.isAssignableFrom(exception.getClass()));
    }

    /**
     * Create a retry builder for fluent configuration
     */
    public static RetryBuilder builder() {
        return new RetryBuilder();
    }

    /**
     * Builder class for fluent retry configuration
     */
    public static class RetryBuilder {
        private int maxAttempts = 3;
        private long delayMs = 1000;
        @SuppressWarnings("unchecked")
        private Class<? extends Throwable>[] retryOn = new Class[]{Exception.class};
        @SuppressWarnings("unchecked")
        private Class<? extends Throwable>[] ignoreOn = new Class[0];

        public RetryBuilder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public RetryBuilder delay(long delayMs) {
            this.delayMs = delayMs;
            return this;
        }

        @SafeVarargs
        public final RetryBuilder retryOn(Class<? extends Throwable>... exceptions) {
            this.retryOn = exceptions;
            return this;
        }

        @SafeVarargs
        public final RetryBuilder ignoreOn(Class<? extends Throwable>... exceptions) {
            this.ignoreOn = exceptions;
            return this;
        }

        public <T> T execute(Supplier<T> operation) {
            return executeWithRetry(operation, maxAttempts, delayMs, retryOn, ignoreOn);
        }

        public void execute(Runnable operation) {
            executeWithRetry(() -> {
                operation.run();
                return null;
            }, maxAttempts, delayMs, retryOn, ignoreOn);
        }
    }
}
