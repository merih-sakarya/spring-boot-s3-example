package com.example.s3integration.repository;

import com.example.s3integration.model.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {

    Optional<FileStorage> findByFileKey(String fileKey);
    Optional<FileStorage> findByFilePath(String filePath);
}
