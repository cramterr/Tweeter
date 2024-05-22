package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBStoryDAO;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class StoryService {

    public StoryService (IAuthDAO authdao, IStoryDAO storydao) {
        this.authDAO = authdao;
        this.storyDAO = storydao;
    }

    private IAuthDAO authDAO;
    private IStoryDAO storyDAO;

    public GetStoryResponse getStory(GetStoryRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        try {
            Pair<List<Status>, Boolean> pair = storyDAO.getStory(request.getTargetAlias(), request.getLimit(), request.getLastStatus());
            return new GetStoryResponse(pair.getFirst(), pair.getSecond());
        } catch (DynamoDbException e) {
            return new GetStoryResponse(e.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/767030191187/postStatusQ";

        SqsClient sqsClient = SqsClient.builder().build();
        // Publish a message to the SQS queue
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(request.getStatus().toJson())
                .build());

        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        try {
            Pair<Boolean, String> user = authDAO.verifyAuth(request.getAuthToken());
            if (user.getFirst()) {
                Boolean success = storyDAO.postStatus(user.getSecond(), request.getStatus());
                return new PostStatusResponse(success);
            } else {
                return new PostStatusResponse("Invalid authtoken");
            }
        } catch (DynamoDbException e) {
            return new PostStatusResponse(e.getMessage());
        }
    }
}
