package com.example.s3integration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class S3ConfigProperties {

    private String region;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
