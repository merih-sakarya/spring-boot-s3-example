package com.example.s3integration.model.http.request.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileCopyRequest {

    @NotNull
    private String targetDirectory;
}
