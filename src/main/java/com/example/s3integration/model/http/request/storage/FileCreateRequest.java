package com.example.s3integration.model.http.request.storage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileCreateRequest {

    @NotNull
    private String fileKey;

    @NotNull
    private String filePath;
}
