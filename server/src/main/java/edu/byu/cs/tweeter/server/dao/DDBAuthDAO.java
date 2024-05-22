package edu.byu.cs.tweeter.server.dao;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

public class DDBAuthDAO implements IAuthDAO {
    private DynamoDbClient dynamoDB;

    public DDBAuthDAO(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    private String tableName = "auth";

    @Override
    public Pair<Boolean, String> verifyAuth(AuthToken authToken) throws DynamoDbException {
        try {
            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(tableName)
                    .indexName("authtoken-index") // Specify the secondary index name
                    .keyConditionExpression("authtoken = :token")
                    .expressionAttributeValues(Map.of(":token", AttributeValue.builder().s(authToken.getToken()).build()))
                    .projectionExpression("user_handle, #ts") // Use an alias for "timestamp"
                    .expressionAttributeNames(Map.of("#ts", "timestamp")) // Provide an alias for "timestamp"
                    .build();

            QueryResponse queryResponse = dynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResponse.items();

            if (items.isEmpty()) {
                return new Pair<>(false, "AuthToken not valid");
            } else {
                Map<String, AttributeValue> item = items.get(0);
                AttributeValue aliasAttributeValue = item.get("user_handle");
                String alias = aliasAttributeValue.s();

                // Extract timestamp value and check if it's more than 5 days old
                AttributeValue timestampAttributeValue = item.get("timestamp");
                long timestamp = Long.parseLong(timestampAttributeValue.n());
                Instant currentTime = Instant.now();
                Instant fiveDaysAgo = currentTime.minusSeconds(5 * 24 * 60 * 60); // 5 days in seconds

                boolean expired = Instant.ofEpochSecond(timestamp).isBefore(fiveDaysAgo);

                if (expired) {
                    return new Pair<>(false, "AuthToken is more than 5 days old");
                } else {
                    return new Pair<>(true, alias);
                }
            }
        } catch (DynamoDbException e) {
            throw e;
        }
    }

    @Override
    public Boolean verifyLogin(String userAlias, String password) throws DynamoDbException {
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("user_handle", AttributeValue.builder().s(userAlias).build()))
                .projectionExpression("password")
                .build();

        Map<String, AttributeValue> item = dynamoDB.getItem(getItemRequest).item();

        if (item == null || !item.containsKey("password")) {
            return false;
        }

        String hashedPassword = item.get("password").s();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Override
    public Boolean verifyNotDuplicate(String userAlias) throws DynamoDbException {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("user_handle = :userAlias")
                .expressionAttributeValues(Map.of(":userAlias", AttributeValue.builder().s(userAlias).build()))
                .limit(1)
                .build();

        try {
            QueryResponse queryResponse = dynamoDB.query(queryRequest);
            return queryResponse.count() == 0;
        } catch (DynamoDbException e) {
            throw e;
        }
    }

    @Override
    public Boolean logout(String userAlias) throws DynamoDbException {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("user_handle", AttributeValue.builder().s(userAlias).build()))
                .updateExpression("SET #token = :token, #timestamp = :timestamp")
                .expressionAttributeNames(Map.of("#token", "authtoken", "#timestamp", "timestamp"))
                .expressionAttributeValues(Map.of(
                        ":token", AttributeValue.builder().s("null").build(),
                        ":timestamp", AttributeValue.builder().n("0").build()
                ))
                .build();

        UpdateItemResponse updateItemResponse = dynamoDB.updateItem(updateItemRequest);

        return updateItemResponse.sdkHttpResponse().isSuccessful();
    }

    @Override
    public Boolean login(String userAlias, AuthToken authToken) throws DynamoDbException {
        try {
            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("user_handle", AttributeValue.builder().s(userAlias).build()))
                    .updateExpression("SET #token = :token, #timestamp = :timestamp")
                    .expressionAttributeNames(Map.of("#token", "authtoken", "#timestamp", "timestamp"))
                    .expressionAttributeValues(Map.of(
                            ":token", AttributeValue.builder().s(authToken.getToken()).build(),
                            ":timestamp", AttributeValue.builder().n(String.valueOf(authToken.getTimestamp())).build()
                    ))
                    .build();

            UpdateItemResponse updateItemResponse = dynamoDB.updateItem(updateItemRequest);

            return updateItemResponse.sdkHttpResponse().isSuccessful();
        } catch (DynamoDbException e) {
            throw e;
        }
    }

    @Override
    public Boolean register(String userAlias, String password, AuthToken authToken) throws DynamoDbException {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "user_handle", AttributeValue.builder().s(userAlias).build(),
                        "password", AttributeValue.builder().s(password).build(),
                        "timestamp", AttributeValue.builder().n(String.valueOf(authToken.getTimestamp())).build(),
                        "authtoken", AttributeValue.builder().s(authToken.getToken()).build()
                ))
                .build();

        dynamoDB.putItem(putItemRequest);

        return true;
    }
}
