package com.example.s3integration.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${spring-boot-s3-example.storage.main-folder}")
    private String MAIN_FOLDER;

    /**
     * Converts a MultipartFile to a temporary File.
     *
     * @param multipartFile the MultipartFile to be converted
     * @return the converted File
     * @throws IOException if any I/O error occurs during file creation or writing
     */
    public File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        // Extract the file extension from the original filename
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        // Create a temporary file with a unique prefix and the correct file extension
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), "." + extension);
        // Write directly to disk without loading the entire file into RAM
        multipartFile.transferTo(tempFile);
        // Return the temporary file
        return tempFile;
    }

    /**
     * Converts an InputStream to a temporary File.
     *
     * @param inputStream the InputStream to be converted
     * @param originalFilename the original filename to determine the file extension
     * @return the converted temporary File
     * @throws IOException if any I/O error occurs during file creation or writing
     */
    public File convertInputStreamToFile(InputStream inputStream, String originalFilename) throws IOException {
        // Extract the file extension from the original filename
        String extension = FilenameUtils.getExtension(originalFilename);
        // Create a temporary file with a unique prefix and the correct file extension
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), "." + extension);
        // Write InputStream directly to file, avoiding large byte arrays in memory
        try (FileOutputStream fos = new FileOutputStream(tempFile); BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            byte[] buffer = new byte[8192]; // 8KB buffer size
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        // Return the temporary file
        return tempFile;
    }

    /**
     * Generates a random, unique file key using UUID.
     *
     * @return a unique random file key in UUID string format.
     */
    public String generateFileKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a structured file path based on the current date and a unique file name.
     *
     * @param originalFilename the original filename to extract the file extension.
     * @return the generated file path in the format: "MAIN_FOLDER/yyyyMMdd/uniqueFileName.ext".
     */
    public String generateFilePath(String originalFilename) {
        // Use java.time for date formatting
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Construct the full path
        return MAIN_FOLDER + "/" + formattedDate + "/" + generateRandomFileName(originalFilename);
    }

    /**
     * Generates a random, unique file name using UUID and the original file's extension.
     *
     * @param originalFilename the original filename from which to extract the extension.
     * @return a unique random file name with the original extension.
     */
    public String generateRandomFileName(String originalFilename) {
        // Extract the file extension from the original filename
        String extension = FilenameUtils.getExtension(originalFilename);
        // Generate a random UUID for the file name, and return it with the original extension
        return UUID.randomUUID() + "." + extension;
    }

    /**
     * Extracts the file name from the given file path.
     *
     * @param filePath the full path of the file (e.g., "storage/20250312/sample-file.ipa").
     * @return the extracted file name (e.g., "sample-file.ipa").
     */
    public String extractFileNameFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        // Extracts the file name from the full path
        return FilenameUtils.getName(filePath);
    }
}
