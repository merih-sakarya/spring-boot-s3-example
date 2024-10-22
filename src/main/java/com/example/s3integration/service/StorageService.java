package com.example.s3integration.service;

import com.example.s3integration.model.http.response.storage.StorageFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Downloads a file from the storage using the provided key.
     *
     * @param key the key of the file to download
     * @return the file content as a byte array
     */
    byte[] getFile(String key);

    /**
     * Uploads a file to the storage and returns the generated key.
     *
     * @param file the file to be uploaded
     * @return the generated key for the uploaded file
     */
    StorageFileResponse uploadFile(MultipartFile file);

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
