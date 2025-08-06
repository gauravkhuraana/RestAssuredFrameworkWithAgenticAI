package com.api.automation.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for retry on failure
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    
    /**
     * Number of retry attempts
     */
    int attempts() default 3;
    
    /**
     * Delay between retries in milliseconds
     */
    long delay() default 1000;
    
    /**
     * Exceptions that should trigger a retry
     */
    Class<? extends Throwable>[] retryOn() default {Exception.class};
    
    /**
     * Exceptions that should NOT trigger a retry
     */
    Class<? extends Throwable>[] ignoreOn() default {};
}
