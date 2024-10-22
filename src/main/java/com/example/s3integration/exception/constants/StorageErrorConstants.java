package com.example.s3integration.exception.constants;

public final class StorageErrorConstants {

    public static final Code STORAGE_ERR001 = new Code("STORAGE_ERR001", "Storage Service operation failed");

    public record Code(String code, String message) {
    }

    private StorageErrorConstants() {
    }
}
