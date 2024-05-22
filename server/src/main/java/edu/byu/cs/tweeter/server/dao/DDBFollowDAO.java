package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.Select;

public class DDBFollowDAO implements IFollowDAO {

    private String tableName = "follows";
    private DynamoDbClient dynamoDB;

    public DDBFollowDAO(DynamoDbClient dynamoDB) {
        this.dynamoDB = dynamoDB;
    }


    @Override
    public Integer getFolloweeCount(String followerAlias) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("follower_handle = :followerAlias")
                .expressionAttributeValues(Map.of(":followerAlias", AttributeValue.builder().s(followerAlias).build()))
                .select(Select.COUNT)
                .build();

        QueryResponse queryResponse = dynamoDB.query(queryRequest);
        return queryResponse.count();
    }

    @Override
    public Integer getFollowerCount(String followeeAlias) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("follows_index")
                .keyConditionExpression("followee_handle = :targetAlias")
                .expressionAttributeValues(Map.of(":targetAlias", AttributeValue.builder().s(followeeAlias).build()))
                .select(Select.COUNT)
                .build();

        QueryResponse queryResponse = dynamoDB.query(queryRequest);
        return queryResponse.count();
    }

    @Override
    public Pair<List<String>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {
        QueryRequest.Builder queryRequestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("follower_handle = :followerAlias")
                .expressionAttributeValues(Map.of(":followerAlias", AttributeValue.builder().s(followerAlias).build()))
                .limit(limit);

        if (lastFolloweeAlias != null) {
            queryRequestBuilder.exclusiveStartKey(Map.of(
                    "follower_handle", AttributeValue.builder().s(followerAlias).build(),
                    "followee_handle", AttributeValue.builder().s(lastFolloweeAlias).build()
            ));
        }

        QueryResponse queryResponse = dynamoDB.query(queryRequestBuilder.build());

        List<String> followees = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            followees.add(item.get("followee_handle").s());
        }

        boolean hasMoreResults;
        if (followees.size() == limit && queryResponse.lastEvaluatedKey() != null) {
            hasMoreResults = true;
        } else {
            hasMoreResults = false;
        }
        return new Pair(followees, hasMoreResults);
    }

    @Override
    public Pair<List<String>, Boolean> getFollowers(String followeeAlias, int limit, String lastFollowerAlias) throws DynamoDbException {
        QueryRequest.Builder queryRequestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .indexName("follows_index")
                .keyConditionExpression("followee_handle = :targetAlias")
                .expressionAttributeValues(Map.of(":targetAlias", AttributeValue.builder().s(followeeAlias).build()))
                .limit(limit);

        if (lastFollowerAlias != null) {
            queryRequestBuilder.exclusiveStartKey(Map.of(
                    "followee_handle", AttributeValue.builder().s(followeeAlias).build(),
                    "follower_handle", AttributeValue.builder().s(lastFollowerAlias).build()
            ));
        }

        QueryResponse queryResponse = dynamoDB.query(queryRequestBuilder.build());

        List<String> followers = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            followers.add(item.get("follower_handle").s());
        }

        boolean hasMoreResults;
        if (followers.size() == limit && queryResponse.lastEvaluatedKey() != null) {
            hasMoreResults = true;
        } else {
            hasMoreResults = false;
        }
        return new Pair(followers, hasMoreResults);
    }

    @Override
    public void followUser(String followerId, String followeeId) throws DynamoDbException {
        try {
            PutItemRequest putRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(Map.of("follower_handle", AttributeValue.builder().s(followerId).build(),
                            "followee_handle", AttributeValue.builder().s(followeeId).build()))
                    .build();

            dynamoDB.putItem(putRequest);
        } catch (DynamoDbException e) {
            throw e;
        }
    }

    @Override
    public void unfollowUser(String followerId, String followeeId) throws DynamoDbException {
        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("follower_handle", AttributeValue.builder().s(followerId).build(),
                        "followee_handle", AttributeValue.builder().s(followeeId).build()))
                .build();

        dynamoDB.deleteItem(deleteRequest);
    }

    @Override
    public Boolean isFollower(String followerAlias, String followeeAlias) throws DynamoDbException {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("follower_handle = :followerAlias AND followee_handle = :followeeAlias")
                .expressionAttributeValues(Map.of(
                        ":followerAlias", AttributeValue.builder().s(followerAlias).build(),
                        ":followeeAlias", AttributeValue.builder().s(followeeAlias).build()))
                .limit(1) // Limit to 1 result as we only need to check existence
                .build();
        QueryResponse queryResponse = dynamoDB.query(queryRequest);

        return queryResponse.count() > 0;
    }
}
