package com.example.s3integration.service;

import com.example.s3integration.exception.custom.storage.StorageServiceException;
import com.example.s3integration.mapper.StorageMapper;
import com.example.s3integration.model.entity.Storage;
import com.example.s3integration.model.http.request.storage.StorageFileCreateRequest;
import com.example.s3integration.model.http.response.storage.StorageFileResponse;
import com.example.s3integration.repository.StorageRepository;
import com.example.s3integration.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final FileUtils fileUtils;
    private final StorageMapper storageMapper;
    private final S3ClientService s3ClientService;
    private final StorageRepository storageRepository;

    // Downloads a file from the storage
    @Override
    public byte[] getFile(String key) {
        try {
            // Retrieve file metadata (file path) from the database using the fileKey
            Storage storage = storageRepository.findByFileKey(key)
                    .orElseThrow(() -> new StorageServiceException("File not found for key: " + key));
            // Download the file from S3 using the file path stored in the metadata
            ResponseInputStream<GetObjectResponse> response = s3ClientService.getFile(storage.getFilePath());
            logger.info("File downloaded successfully for key: {}", key);
            return response.readAllBytes();
        } catch (Exception e) {
            logger.error("Failed to download file with key: {}", key, e);
            throw new StorageServiceException("Failed to download file with key: " + key, e);
        }
    }

    // Uploads a file to the storage
    @Override
    public StorageFileResponse uploadFile(MultipartFile file) {
        File convertedFile = null;
        try {
            // Convert MultipartFile to File
            convertedFile = fileUtils.convertMultiPartToFile(file);
            // Generate file key and path, and create request model
            StorageFileCreateRequest request = generateStorageFileCreateRequest(file);
            // Upload the file to the S3 storage
            s3ClientService.uploadFile(request.getFilePath(), convertedFile, null);
            // Save Storage entity to database
            Storage storage = saveStorageFileMetadata(request);
            logger.info("File uploaded successfully with key: {}", request.getFileKey());
            return storageMapper.toResponse(storage);
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new StorageServiceException("Failed to upload file", e);
        } finally {
            if (convertedFile != null && !convertedFile.delete()) {
                logger.warn("Temporary file {} could not be deleted.", convertedFile.getName());
            }
        }
    }

    // Helper method to create StorageFileCreateRequest model.
    private StorageFileCreateRequest generateStorageFileCreateRequest(MultipartFile file) {
        // Generate a unique key for the file
        String fileKey = fileUtils.generateRandomFileKey();
        // Generate a unique path for the file
        String filePath = fileUtils.generateRandomFileName(file.getOriginalFilename());
        return new StorageFileCreateRequest(fileKey, filePath);
    }

    // Saves file metadata as a Storage entity
    private Storage saveStorageFileMetadata(StorageFileCreateRequest request) {
        Storage storage = storageMapper.toEntity(request);
        return storageRepository.save(storage);
    }

    // Deletes a file from the storage
    @Override
    public void deleteFile(String key) {
        try {
            // Retrieve file metadata (file path) from the database using the fileKey
            Storage storage = storageRepository.findByFileKey(key)
                    .orElseThrow(() -> new StorageServiceException("File not found for key: " + key));
            // Delete the file from S3 using the file path from the metadata
            s3ClientService.deleteFile(storage.getFilePath());
            // Remove the file metadata from the database
            storageRepository.delete(storage);
            logger.info("File deleted successfully with key: {}", key);
        } catch (Exception e) {
            logger.error("Failed to delete file with key: {}", key, e);
            throw new StorageServiceException("Failed to delete file with key: " + key, e);
        }
    }

    // Copies a file within the storage
    @Override
    public void copyFile(String sourceKey, String targetDirectory) {
        try {
            // Retrieve file metadata (file path) from the database using the sourceKey
            Storage storage = storageRepository.findByFileKey(sourceKey)
                    .orElseThrow(() -> new StorageServiceException("Source file not found for key: " + sourceKey));
            String destinationFilePath = targetDirectory + "/" + fileUtils.generateRandomFileName(storage.getFilePath());
            // Copy the file within S3 using the file path from the metadata
            s3ClientService.copyFile(storage.getFilePath(), destinationFilePath , null);
            // Update the storage entity with the new path
            storage.setFilePath(destinationFilePath);
            storageRepository.save(storage);
            logger.info("File copied successfully from {} to {}", storage.getFilePath(), destinationFilePath);
        } catch (Exception e) {
            logger.error("Failed to copy file from key: {} to destination: {}", sourceKey, targetDirectory, e);
            throw new StorageServiceException("Failed to copy file from key: " + sourceKey + " to destination: " + targetDirectory, e);
        }
    }
}
