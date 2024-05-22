package edu.byu.cs.tweeter.server.lambda;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class lambdaHandler {
    private DynamoDbClient dynamoDB;
    protected DynamoDbClient getClient()
    {
        if (dynamoDB == null)
        {
            dynamoDB = DynamoDbClient.builder()
                    .region(Region.US_WEST_2) // Specify your AWS region
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
        return dynamoDB;
    }
}
