package com.example.s3integration.service;

import com.example.s3integration.config.properties.S3ConfigProperties;
import com.example.s3integration.exception.custom.s3.S3ServiceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

@Service
@RequiredArgsConstructor
public class S3ClientServiceImpl implements S3ClientService {

    private static final Logger logger = LoggerFactory.getLogger(S3ClientServiceImpl.class);

    private final S3Client s3Client;
    private final S3ConfigProperties s3ConfigProperties;

    // Downloads a file from S3
    public ResponseInputStream<GetObjectResponse> getFile(String filePath) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(s3ConfigProperties.getBucketName())
                    .key(filePath)
                    .build());
        } catch (Exception e) {
            logger.error("Download Error for file {} in bucket {}: {}", filePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Download Error for file: " + filePath, e);
        }
    }

    // Uploads a file to S3
    public void uploadFile(String filePath, File file, ObjectCannedACL cannedAccessControlList) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(s3ConfigProperties.getBucketName())
                            .key(filePath)
                            .acl(cannedAccessControlList)
                            .build(),
                    RequestBody.fromFile(file.toPath()));
        } catch (Exception e) {
            logger.error("Upload Error for file {} to bucket {}: {}", filePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Upload Error for file: " + filePath, e);
        }
    }

    // Copies a file within S3
    public void copyFile(String sourceFilePath, String destinationFilePath, ObjectCannedACL cannedAccessControlList) {
        try {
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(s3ConfigProperties.getBucketName())
                    .sourceKey(sourceFilePath)
                    .destinationBucket(s3ConfigProperties.getBucketName())
                    .destinationKey(destinationFilePath)
                    .acl(cannedAccessControlList)
                    .build());
        } catch (Exception e) {
            logger.error("Copy Error from {} to {} in bucket {}: {}", sourceFilePath, destinationFilePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Copy Error from " + sourceFilePath + " to " + destinationFilePath, e);
        }
    }

    // Deletes a file from S3
    public void deleteFile(String filePath) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(s3ConfigProperties.getBucketName())
                    .key(filePath)
                    .build());
        } catch (Exception e) {
            logger.error("Delete Error for file {} in bucket {}: {}", filePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Delete Error for file: " + filePath, e);
        }
    }
}
