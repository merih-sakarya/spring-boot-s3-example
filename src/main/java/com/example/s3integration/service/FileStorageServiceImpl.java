package com.example.s3integration.service;

import com.example.s3integration.exception.custom.storage.StorageServiceException;
import com.example.s3integration.mapper.FileStorageMapper;
import com.example.s3integration.model.entity.FileStorage;
import com.example.s3integration.model.http.request.storage.FileCreateRequest;
import com.example.s3integration.model.http.response.storage.FileMetadataResponse;
import com.example.s3integration.model.http.response.storage.FileUploadResponse;
import com.example.s3integration.repository.FileStorageRepository;
import com.example.s3integration.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.File;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private final FileUtils fileUtils;
    private final S3ClientService s3ClientService;
    private final FileStorageMapper fileStorageMapper;
    private final FileStorageRepository fileStorageRepository;

    // Downloads a file as InputStream from the storage
    @Override
    public InputStreamResource getFile(String key) {
        try {
            // Retrieve file metadata (file path) from the database using the fileKey
            FileStorage fileStorage = fileStorageRepository.findByFileKey(key).orElseThrow(() -> new StorageServiceException("File not found for key: " + key));
            // Download the file from S3 using the file path stored in the metadata
            InputStream fileInputStream = s3ClientService.getFile(fileStorage.getFilePath());
            logger.debug("File downloaded successfully for key: {}", key);
            return new InputStreamResource(fileInputStream);
        } catch (Exception e) {
            logger.error("Failed to download file with key: {}", key, e);
            throw new StorageServiceException("Failed to download file with key: " + key, e);
        }
    }

    // Uploads a file to the storage
    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        File tempFile = null;
        try {
            // Generate file key and path, and create request model
            FileCreateRequest request = generatFileCreateRequest(file.getOriginalFilename());
            // Convert MultipartFile to a temporary File
            tempFile = fileUtils.convertMultiPartToFile(file);
            logger.info("Uploading file '{}' ({} bytes) to path: '{}'", file.getOriginalFilename(), file.getSize(), request.getFilePath());
            // Upload the stored file to the final storage location
            s3ClientService.uploadFile(request.getFilePath(), tempFile, ObjectCannedACL.PRIVATE);
            // Save Storage entity to database
            FileStorage fileStorage = saveStorageFileMetadata(request);
            logger.info("File uploaded successfully with key: {}", request.getFileKey());
            return fileStorageMapper.toResponse(fileStorage);
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new StorageServiceException("Failed to upload file", e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.warn("Temporary file '{}' could not be deleted.", tempFile.getAbsolutePath());
            }
        }
    }

    // Uploads a file stream to the storage
    @Override
    public FileUploadResponse uploadFileStream(InputStreamResource fileStream, String fileName) {
        File tempFile = null;
        try {
            // Generate file key and path, and create request model
            FileCreateRequest request = generatFileCreateRequest(fileName);
            // Convert InputStreamResource to InputStream
            InputStream inputStream = fileStream.getInputStream();
            // Convert InputStream to a temporary file
            tempFile = fileUtils.convertInputStreamToFile(inputStream, fileName);
            logger.info("Uploading streamed file to path: '{}'", request.getFilePath());
            // Upload the stored file to the final storage location
            s3ClientService.uploadFile(request.getFilePath(), tempFile, ObjectCannedACL.PRIVATE);
            // Save Storage entity to database
            FileStorage fileStorage = saveStorageFileMetadata(request);
            logger.info("Streamed file uploaded successfully with key: '{}'", request.getFileKey());
            return fileStorageMapper.toResponse(fileStorage);
        } catch (Exception e) {
            logger.error("Failed to upload file via stream", e);
            throw new StorageServiceException("Failed to upload file via stream", e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.warn("Temporary file '{}' could not be deleted.", tempFile.getAbsolutePath());
            }
        }
    }

    // Retrieves the metadata of a file stored in the system using the provided key.
    // This method does not download the file but only fetches metadata such as file path and key.
    @Override
    public FileMetadataResponse getFileMetadata(String key) {
        try {
            // Retrieve file metadata (file path, file key) from the database
            FileStorage fileStorage = fileStorageRepository.findByFileKey(key).orElseThrow(() -> new StorageServiceException("File metadata not found for key: " + key));
            // Map entity to response model
            return fileStorageMapper.toMetadataResponse(fileStorage);
        } catch (Exception e) {
            logger.error("Failed to retrieve file metadata for key: {}", key, e);
            throw new StorageServiceException("Failed to retrieve file metadata for key: " + key, e);
        }
    }

    // Deletes a file from the storage
    @Override
    public void deleteFile(String key) {
        try {
            // Retrieve file metadata (file path) from the database using the fileKey
            FileStorage fileStorage = fileStorageRepository.findByFileKey(key).orElseThrow(() -> new StorageServiceException("File not found for key: " + key));
            // Delete the file from S3 using the file path from the metadata
            s3ClientService.deleteFile(fileStorage.getFilePath());
            // Remove the file metadata from the database
            fileStorageRepository.delete(fileStorage);
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
            FileStorage fileStorage = fileStorageRepository.findByFileKey(sourceKey).orElseThrow(() -> new StorageServiceException("Source file not found for key: " + sourceKey));
            String destinationFilePath = targetDirectory + "/" + fileUtils.generateRandomFileName(fileStorage.getFilePath());
            // Copy the file within S3 using the file path from the metadata
            s3ClientService.copyFile(fileStorage.getFilePath(), destinationFilePath, null);
            // Update the storage entity with the new path
            fileStorage.setFilePath(destinationFilePath);
            fileStorageRepository.save(fileStorage);
            logger.info("File copied successfully from {} to {}", fileStorage.getFilePath(), destinationFilePath);
        } catch (Exception e) {
            logger.error("Failed to copy file from key: {} to destination: {}", sourceKey, targetDirectory, e);
            throw new StorageServiceException("Failed to copy file from key: " + sourceKey + " to destination: " + targetDirectory, e);
        }
    }

    // Helper method to create StorageFileCreateRequest model.
    private FileCreateRequest generatFileCreateRequest(String originalFilename) {
        // Generate a unique key for the file
        String fileKey = fileUtils.generateFileKey();
        // Generate a unique path for the file
        String filePath = fileUtils.generateFilePath(originalFilename);
        return new FileCreateRequest(fileKey, filePath);
    }

    // Saves file metadata as a Storage entity
    private FileStorage saveStorageFileMetadata(FileCreateRequest request) {
        FileStorage fileStorage = fileStorageMapper.toEntity(request);
        return fileStorageRepository.save(fileStorage);
    }
}
