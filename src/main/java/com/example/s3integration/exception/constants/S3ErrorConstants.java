package com.example.s3integration.exception.constants;

public final class S3ErrorConstants {

    public static final Code S3_ERR001 = new Code("S3_ERR001", "S3 Service operation failed");

    public record Code(String code, String message) {
    }

    private S3ErrorConstants() {
    }
}
