package com.example.s3integration.controller;

import com.example.s3integration.model.http.request.storage.FileCopyRequest;
import com.example.s3integration.model.http.response.storage.FileMetadataResponse;
import com.example.s3integration.model.http.response.storage.FileUploadResponse;
import com.example.s3integration.service.FileStorageService;
import com.example.s3integration.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class FileStorageController {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    private final FileUtils fileUtils;
    private final FileStorageService fileStorageService;

    /**
     * Uploads a file using a traditional form-based POST request with {@link MultipartFile}.
     *
     * This method is efficient for handling small to medium-sized file uploads.
     * However, for large file uploads, {@link #uploadFileStream(InputStreamResource, String)}
     * is recommended to reduce memory usage.
     *
     * @param file the file to upload
     * @return a response containing the uploaded file's metadata and generated key
     */
    @PostMapping("/files")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam MultipartFile file) {
        logger.info("Received request to upload file: {}", file.getOriginalFilename());
        FileUploadResponse response = fileStorageService.uploadFile(file);
        logger.info("File '{}' uploaded successfully. Generated key: {}", file.getOriginalFilename(), response.getFileKey());
        return ResponseEntity.ok(response);
    }

    /**
     * Uploads a file using an InputStream-based streaming approach.
     * This method prevents memory issues by processing the file as a stream.
     *
     * @param fileStream the file content stream
     * @param fileName the original file name, passed via the "filename" header
     * @return a response containing the uploaded file's metadata and generated key
     */
    @PostMapping(value = "/files/stream", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFileStream(@RequestBody InputStreamResource fileStream,
                                                               @RequestHeader(value = "filename") String fileName) {
        logger.info("Received a file upload request using streaming. File Name: {}", fileName);
        FileUploadResponse response = fileStorageService.uploadFileStream(fileStream, fileName);
        logger.info("File '{}' uploaded successfully using streaming. Generated key: {}", fileName, response.getFileKey());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a file from the storage system and streams it as a response.
     * This method is optimized for large files by avoiding memory overhead.
     *
     * @param key the unique key of the file to download
     * @return a ResponseEntity with InputStreamResource for efficient streaming
     */
    @GetMapping(value = "/files/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String key) {
        logger.debug("Received request to stream file with key: {}", key);

        // Retrieve file metadata
        FileMetadataResponse fileMetadata = fileStorageService.getFileMetadata(key);
        // Extract file name from storage path
        String fileName = fileUtils.extractFileNameFromPath(fileMetadata.getFilePath());

        InputStreamResource fileStream = fileStorageService.getFile(key);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileStream);
    }

    /**
     * Deletes a file from storage using its unique key.
     *
     * @param key the key of the file to delete
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/files/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable String key) {
        logger.info("Received request to delete file with key: {}", key);
        fileStorageService.deleteFile(key);
        logger.info("File with key '{}' deleted successfully.", key);
        return ResponseEntity.noContent().build();
    }

    /**
     * Copies an existing file to a new directory within the same storage system.
     *
     * @param key the key of the file to copy
     * @param request a request containing the target directory path
     * @return HTTP 204 No Content if the copy operation is successful
     */
    @PostMapping("/files/{key}/copy")
    public ResponseEntity<String> copyFile(@PathVariable String key, @RequestBody FileCopyRequest request) {
        logger.info("Received request to copy file from key '{}' to '{}'", key, request.getTargetDirectory());
        fileStorageService.copyFile(key, request.getTargetDirectory());
        logger.info("File copied successfully from key '{}' to '{}'", key, request.getTargetDirectory());
        return ResponseEntity.noContent().build();
    }
}
