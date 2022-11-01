package com.example.s3integration.service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.File;

public interface S3ClientService {

    /**
     * Downloads a file from the specified path in the S3 bucket.
     *
     * @param filePath the path of the file in the S3 bucket.
     * @return a stream containing the contents of the file.
     */
    ResponseInputStream<GetObjectResponse> getFile(String filePath);

    /**
     * Uploads a file to the specified path in the S3 bucket.
     *
     * @param filePath the path where the file will be uploaded in the S3 bucket.
     * @param file the file to upload.
     * @param cannedAccessControlList the access control settings for the uploaded file.
     */
    void uploadFile(String filePath, File file, ObjectCannedACL cannedAccessControlList);

    /**
     * Copies a file within the S3 bucket from one path to another.
     *
     * @param sourceFilePath the path of the source file in the S3 bucket.
     * @param destinationFilePath the path of the destination file in the S3 bucket.
     * @param cannedAccessControlList the access control settings for the copied file.
     */
    void copyFile(String sourceFilePath, String destinationFilePath, ObjectCannedACL cannedAccessControlList);

    /**
     * Deletes a file from the specified path in the S3 bucket.
     *
     * @param filePath the path of the file to be deleted in the S3 bucket.
     */
    void deleteFile(String filePath);
}
