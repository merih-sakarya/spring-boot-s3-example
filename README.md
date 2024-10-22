# AWS S3 Integration in Spring Boot: Using S3Client and S3Template

## Overview

**Amazon S3** (Simple Storage Service) is one of the most scalable and durable object storage services offered by AWS, commonly used for file storage and backup solutions. In this article, we will explore how to integrate AWS S3 into a Spring Boot project, demonstrating the use of **S3Client** for low-level control and **S3Template** for a more simplified, Spring-friendly approach.

This project primarily demonstrates **S3Client** to give fine-grained control over file operations such as upload, download, and delete. Additionally, we will cover how **S3Template**, provided by **Spring Cloud AWS**, simplifies AWS S3 interactions by abstracting the complexities of the lower-level API.

## Introduction to S3Client and S3Template

### S3Client
**S3Client** is part of the AWS SDK and provides a low-level API for interacting with AWS S3 services. It allows us to directly perform operations like creating buckets, uploading and downloading files, and deleting objects. While **S3Client** offers comprehensive control over S3, it requires more manual handling of request and response objects, making it more complex but highly customizable.

### S3Template
**S3Template** is a higher-level abstraction provided by **Spring Cloud AWS**. It simplifies AWS S3 operations with a Spring-style interface, allowing for easier integration with Spring Boot projects. **S3Template** builds on top of **S3Client**, making common tasks like file upload, download, and deletion much more straightforward. It abstracts away much of the complexity, allowing developers to work more efficiently in Spring ecosystems.

**Note:** When you include the `spring-cloud-aws-starter-s3` dependency, it also provides **S3Client** through auto-configuration. This means that, while **S3Template** simplifies the integration, you still have access to **S3Client** for low-level control if needed. This flexibility allows you to choose between the two based on your project's requirements.

### Why This Project Uses S3Client
In this project, I primarily implemented **S3Client** to demonstrate the control and flexibility it offers over AWS S3 operations. However, S3Template can be used as an alternative for developers who are looking for a simpler and more Spring-friendly approach to AWS S3 integration.

## Setting Up the Project

### Dependency Configuration for AWS S3 using S3Client
To integrate **AWS S3** using **S3Client**, we need to include the relevant dependencies in our pom.xml.

Maven Dependencies for S3Client:
```xml
<dependencies>
   <!-- AWS SDK for S3Client -->
    <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>s3</artifactId>
      <version>${version}</version>
    </dependency>
</dependencies>
```

### Dependency Configuration for AWS S3 using S3Template
For **S3Template**, we need to include the **Spring Cloud AWS** dependencies in pom.xml. This will enable higher-level integration and **auto-configuration** for AWS S3, including **S3Client**.

**Maven Dependencies for S3Template:**
```xml
<dependencyManagement>
   <dependencies>
      <dependency>
         <groupId>io.awspring.cloud</groupId>
         <artifactId>spring-cloud-aws-dependencies</artifactId>
         <version>3.2.0</version>
         <type>pom</type>
         <scope>import</scope>
      </dependency>
   </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Spring Cloud AWS for S3Template -->
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-s3</artifactId>
    </dependency>
</dependencies>
```

### Auto-Configuration for AWS S3 using S3Template

**AWS Credentials Configuration for S3Template:**
When using **S3Template**, **Spring Cloud AWS** takes care of the **auto-configuration** for both **S3Template** and **S3Client**. This means we do not need to manually configure **S3Template** or **S3Client** as Spring beans. We only need to configure AWS credentials and region settings in our application.yml file, and the framework automatically handles the rest.

```yml
spring:
  cloud:
    aws:
      credentials:
        access-key: ${AWS_S3_ACCESS_KEY:your-aws-s3-access-key}
        secret-key: ${AWS_S3_SECRET_KEY:your-aws-s3-secret-key}
      region:
        static: ${AWS_S3_REGION:your-aws-s3-region}
      s3:
        bucket-name: ${AWS_S3_BUCKET_NAME:your-aws-s3-bucket-name}
```

With **Spring Cloud AWS**, auto-configuration detects and configures the necessary beans for S3 operations, including both **S3Template** and **S3Client**, along with the AWS credentials and region settings. This allows us to switch between **S3Template** and **S3Client** seamlessly without additional configuration.

## Using S3Client
S3Client provides granular control over AWS S3 operations, but it requires more manual configuration and handling. Below is an example of how to configure and use S3Client in your project.

### S3Client Configuration

We can configure S3Client by creating a @Configuration class to initialize the client using credentials provided in application.yml:

```yml
## AWS Specific Storage Configuration
aws:
  # S3 Configuration for AWS
  s3:
    access-key: ${AWS_S3_ACCESS_KEY:your-aws-s3-access-key}
    secret-key: ${AWS_S3_SECRET_KEY:your-aws-s3-secret-key}
    region: ${AWS_S3_REGION:your-aws-s3-region}
    bucket-name: ${AWS_S3_BUCKET_NAME:your-aws-s3-bucket-name}
```

**Configuration class that initializes an S3Client instance:**

```java
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3ConfigProperties s3ConfigProperties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3ConfigProperties.getAccessKey(), s3ConfigProperties.getSecretKey())))
                .region(Region.of(s3ConfigProperties.getRegion()))
                .build();
    }
}
```

**S3Client Usage in a Service**
We can use S3Client to perform operations such as file upload, download, and delete. Here is an example service class:

```java
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

    // ...
}
```

## Using S3Template

With S3Template, Spring Cloud AWS automatically configures the template and the required beans, allowing us to use it right away in our services. There is no need for manual bean configuration unless custom behavior is required.

**S3Template Usage in a Service**
Here’s how we can use S3Template for common file operations such as upload, download, and delete:

```java
@Service
@RequiredArgsConstructor
public class S3TemplateServiceImpl implements S3TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(S3TemplateServiceImpl.class);

    private final S3Template s3Template;
    private final S3ConfigProperties s3ConfigProperties;

    // Downloads a file from S3
    public S3Resource getFile(String filePath) {
        try {
            return s3Template.download(s3ConfigProperties.getBucketName(), filePath);
        } catch (Exception e) {
            logger.error("Download Error for file {} in bucket {}: {}", filePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Download Error for file: " + filePath, e);
        }
    }

    // Uploads a file to S3
    public void uploadFile(String filePath, File file, ObjectCannedACL cannedAccessControlList) {
        try (InputStream inputStream = new FileInputStream(file)) {
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .acl(cannedAccessControlList)
                    .build();

            s3Template.upload(s3ConfigProperties.getBucketName(), filePath, inputStream, metadata);
            logger.info("File uploaded successfully with filePath: {}", filePath);
        } catch (Exception e) {
            logger.error("Upload Error for file {} to bucket {}: {}", filePath, s3ConfigProperties.getBucketName(), e.getMessage(), e);
            throw new S3ServiceException("Upload Error for file: " + filePath, e);
        }
    }

    // ...
}
```

## Conclusion
In this project, we primarily showcased the use of **S3Client** to interact with AWS S3, offering low-level control over operations. However, for those seeking a more Spring-integrated solution, **S3Template** is a great alternative that simplifies S3 interactions and requires less configuration.

Both **S3Client** and S3Template have their respective use cases, and the choice between them depends on the level of control you need and how deeply you want to integrate with the Spring ecosystem.