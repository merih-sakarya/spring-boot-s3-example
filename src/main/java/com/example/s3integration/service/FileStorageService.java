package com.example.s3integration.service;

import com.example.s3integration.model.http.response.storage.FileMetadataResponse;
import com.example.s3integration.model.http.response.storage.FileUploadResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Retrieves a file from the storage system and returns an InputStreamResource for streaming.
     * This method avoids loading the entire file into memory, making it suitable for large files.
     *
     * @param key the key of the file to retrieve
     * @return an InputStreamResource for efficient file streaming
     */
    InputStreamResource getFile(String key);

    /**
     * Retrieves the metadata of a file stored in the system using the provided key.
     * This method does not download the file but only fetches metadata such as file path and key.
     *
     * @param key The unique key of the file.
     * @return The file metadata as a response object.
     */
    FileMetadataResponse getFileMetadata(String key);

    /**
     * Uploads a file to the storage and returns the generated key.
     *
     * @param file the file to be uploaded
     * @return the generated key for the uploaded file
     */
    FileUploadResponse uploadFile(MultipartFile file);

    /**
     * Uploads a file to the storage using a streaming approach.
     * This method allows large files to be uploaded efficiently by processing them as a stream
     * without loading the entire content into memory, reducing the risk of memory-related issues.
     *
     * @param fileStream the input stream resource representing the file to be uploaded
     * @param fileName the original name of the file (provided by the client)
     * @return the generated key for the uploaded file
     */
    FileUploadResponse uploadFileStream(InputStreamResource fileStream, String fileName);

    /**
     * Deletes a file from the storage using the provided key.
     *
     * @param key the key of the file to delete
     */
    void deleteFile(String key);

    /**
     * Copies a file within the storage from source key to destination key.
     *
     * @param sourceKey the key of the source file
     * @param targetDirectory the directory where the file will be copied to
     */
    void copyFile(String sourceKey, String targetDirectory);
}
