package com.example.s3integration.repository;

import com.example.s3integration.model.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByFileKey(String fileKey);
    Optional<Storage> findByFilePath(String filePath);
}
