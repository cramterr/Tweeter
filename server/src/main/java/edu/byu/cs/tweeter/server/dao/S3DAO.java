package edu.byu.cs.tweeter.server.dao;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.UUID;

public class S3DAO implements IS3DAO {

    S3Client s3Client = S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    private String bucketName = "cramterr-newbucket";

    @Override
    public String uploadImage(String imageString) throws S3Exception {
        try {
            String objectKey = UUID.randomUUID().toString();

            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageString);

            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType("image/jpeg")
                    .build(), RequestBody.fromBytes(imageBytes));

            // Generate and return the URL for the uploaded image
            return generateImageUrl(objectKey);
        } catch (S3Exception e) {
            throw e;
        }
    }

    private String generateImageUrl(String objectKey) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + objectKey;
    }
}
