package com.example.s3integration.exception.constants;

/**
 * Contains constants for all error codes used across the application.
 */
public final class GlobalErrorConstants {

    // Validation errors
    public static final Code ERR001 = new Code("ERR001", "Validation errors");
    // Entity not found error
    public static final Code ERR002 = new Code("ERR002", "Entity not found");
    // Access denied error
    public static final Code ERR003 = new Code("ERR003", "Access denied");
    // Method not allowed error
    public static final Code ERR004 = new Code("ERR004", "Method not allowed");
    // Unsupported media type error
    public static final Code ERR005 = new Code("ERR005", "Unsupported media type");
    // Unexpected error
    public static final Code ERR999 = new Code("ERR999", "An unexpected error occurred. Please contact support if the issue persists.");

    /**
     * Represents an error code and its associated message.
     */
    public record Code(String code, String message) {
    }

    // Private constructor to prevent instantiation
    private GlobalErrorConstants() {
    }
}
