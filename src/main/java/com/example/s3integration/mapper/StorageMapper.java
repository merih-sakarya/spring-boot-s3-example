package com.example.s3integration.mapper;

import com.example.s3integration.config.MapperConfig;
import com.example.s3integration.model.entity.Storage;
import com.example.s3integration.model.http.request.storage.StorageFileCreateRequest;
import com.example.s3integration.model.http.response.storage.StorageFileResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface StorageMapper {

    // Request mapper methods
    Storage toEntity(StorageFileCreateRequest deviceCertificateCreateRequest);

    // Response mapper methods
    StorageFileResponse toResponse(Storage iosDeviceCertificate);
}
