package com.example.s3integration.model.http.response.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileMetadataResponse {

    private String fileKey;
    private String filePath;
}
