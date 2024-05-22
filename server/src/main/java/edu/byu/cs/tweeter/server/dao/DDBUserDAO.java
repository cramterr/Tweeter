package edu.byu.cs.tweeter.server.dao;

import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class DDBUserDAO implements IUserDAO {
    private String tableName = "users";

    private DynamoDbClient dynamoDB;

    public DDBUserDAO(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public void register(String userAlias, User newUser) throws DynamoDbException {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "user_handle", AttributeValue.builder().s(userAlias).build(),
                        "user_json", AttributeValue.builder().s(newUser.toJson()).build()
                ))
                .build();

        dynamoDB.putItem(putItemRequest);
    }

    @Override
    public User getUser(String userAlias) throws DynamoDbException {
        try {
            GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("user_handle", AttributeValue.builder().s(userAlias).build()))
                    .projectionExpression("user_json")
                    .build();

            Map<String, AttributeValue> item = dynamoDB.getItem(getItemRequest).item();

            if (item == null || !item.containsKey("user_json")) {
                return null;
            } else {
                return User.fromString(item.get("user_json").s());
            }
        } catch (DynamoDbException e) {
            throw e;
        }
    }
}
