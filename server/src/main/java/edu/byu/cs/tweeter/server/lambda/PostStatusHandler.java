package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBStoryDAO;
import edu.byu.cs.tweeter.server.service.StoryService;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class PostStatusHandler extends lambdaHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest postStatusRequest, Context context) {
        StoryService storyService = new StoryService(new DDBAuthDAO(getClient()), new DDBStoryDAO(getClient()));
        return storyService.postStatus(postStatusRequest);
    }
}
