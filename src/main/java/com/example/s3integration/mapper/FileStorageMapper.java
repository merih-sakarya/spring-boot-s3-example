package com.example.s3integration.mapper;

import com.example.s3integration.config.MapperConfig;
import com.example.s3integration.model.entity.FileStorage;
import com.example.s3integration.model.http.request.storage.FileCreateRequest;
import com.example.s3integration.model.http.response.storage.FileMetadataResponse;
import com.example.s3integration.model.http.response.storage.FileUploadResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface FileStorageMapper {

    // Request mapper methods
    FileStorage toEntity(FileCreateRequest fileCreateRequest);

    // Response mapper methods
    FileUploadResponse toResponse(FileStorage fileStorage);
    FileMetadataResponse toMetadataResponse(FileStorage fileStorage);
}
