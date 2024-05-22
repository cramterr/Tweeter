package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class DDBStoryDAO implements IStoryDAO {
    private String tableName = "story";
    private DynamoDbClient dynamoDB;

    public DDBStoryDAO(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(String targetAlias, int limit, Status lastStatus) throws DynamoDbException {
        QueryRequest.Builder queryRequestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("user_handle = :targetAlias")
                .expressionAttributeValues(Map.of(":targetAlias", AttributeValue.builder().s(targetAlias).build()))
                .limit(limit)
                .scanIndexForward(false);

        if (lastStatus != null) {
            Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("user_handle", AttributeValue.builder().s(targetAlias).build());
            exclusiveStartKey.put("timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            queryRequestBuilder = queryRequestBuilder.exclusiveStartKey(exclusiveStartKey);
        }

        QueryResponse queryResponse = dynamoDB.query(queryRequestBuilder.build());

        List<Status> statuses = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            String statusString = item.get("status").s();
            Status status = Status.fromString(statusString);
            statuses.add(status);
        }


        boolean hasMoreResults;
        if (statuses.size() == limit && queryResponse.lastEvaluatedKey() != null) {
            hasMoreResults = true;
        } else {
            hasMoreResults = false;
        }
        return new Pair<>(statuses, hasMoreResults);
    }

    @Override
    public Boolean postStatus(String targetAlias, Status status) throws DynamoDbException {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "user_handle", AttributeValue.builder().s(targetAlias).build(),
                        "timestamp", AttributeValue.builder().n(String.valueOf(status.getTimestamp())).build(),
                        "status", AttributeValue.builder().s(status.toJson()).build()
                ))
                .build();

        dynamoDB.putItem(putItemRequest);

        return true;
    }
}
