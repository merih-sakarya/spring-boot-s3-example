package com.example.s3integration.controller;

import com.example.s3integration.model.http.request.storage.StorageFileCopyRequest;
import com.example.s3integration.model.http.response.storage.StorageFileResponse;
import com.example.s3integration.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    private final StorageService storageService;

    @PostMapping("/files")
    public ResponseEntity<StorageFileResponse> uploadFile(@RequestParam MultipartFile file) {
        logger.info("Received request to upload file: {}", file.getOriginalFilename());
        StorageFileResponse response = storageService.uploadFile(file);
        logger.info("File '{}' uploaded successfully. Generated key: {}", file.getOriginalFilename(), response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
        logger.info("Received request to download file with key: {}", key);
        byte[] response = storageService.getFile(key);
        logger.info("File with key '{}' downloaded successfully.", key);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/files/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable String key) {
        logger.info("Received request to delete file with key: {}", key);
        storageService.deleteFile(key);
        logger.info("File with key '{}' deleted successfully.", key);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/files/{key}/copy")
    public ResponseEntity<String> copyFile(@PathVariable String key, @RequestBody StorageFileCopyRequest request) {
        logger.info("Received request to copy file from key '{}' to '{}'", key, request.getTargetDirectory());
        storageService.copyFile(key, request.getTargetDirectory());
        logger.info("File copied successfully from key '{}' to '{}'", key, request.getTargetDirectory());
        return ResponseEntity.noContent().build();
    }
}
