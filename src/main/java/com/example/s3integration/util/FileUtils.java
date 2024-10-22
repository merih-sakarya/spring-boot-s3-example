package com.example.s3integration.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class FileUtils {

    /**
     * Converts a MultipartFile to a temporary File.
     *
     * @param multipartFile the MultipartFile to be converted
     * @return the converted File
     * @throws IOException if any I/O error occurs during file creation or writing
     */
    public File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile(multipartFile.getOriginalFilename(), null);

        // Write the contents of the MultipartFile to the temporary file
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(multipartFile.getBytes());
        }

        // Return the temporary file
        return tempFile.toFile();
    }

    /**
     * Generates a random, unique file key using UUID.
     *
     * @return a unique random file key in UUID string format.
     */
    public String generateRandomFileKey() {
        return UUID.randomUUID().toString();
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
}
