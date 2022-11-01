package com.example.s3integration.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "file_storage")
public class FileStorage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "file_key", nullable = false, unique = true)
    private String fileKey;

    @Size(max = 255)
    @Column(name = "file_path", nullable = false, unique = true)
    private String filePath;
}
