package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class DDBFeedDAO implements IFeedDAO {
    private DynamoDbClient dynamoDB;
    private String tableName = "feed";

    public DDBFeedDAO(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public Pair<List<Status>, Boolean> getFeed(String targetAlias, int limit, Status lastStatus) throws DynamoDbException {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("user_handle = :followerAlias")
                .expressionAttributeValues(Map.of(":followerAlias", AttributeValue.builder().s(targetAlias).build()))
                .limit(limit)
                .build();

        if (lastStatus != null) {
            queryRequest = queryRequest.toBuilder()
                    .exclusiveStartKey(Map.of(
                            "user_handle", AttributeValue.builder().s(targetAlias).build(),
                            "timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build()
                    ))
                    .build();
        }

        QueryResponse queryResponse = dynamoDB.query(queryRequest);

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
    public void postStatus(String targetAlias, Status status) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "user_handle", AttributeValue.builder().s(targetAlias).build(),
                        "timestamp", AttributeValue.builder().n(String.valueOf(status.getTimestamp())).build(),
                        "status", AttributeValue.builder().s(status.toJson()).build()
                ))
                .build();

        dynamoDB.putItem(putItemRequest);
    }
}
