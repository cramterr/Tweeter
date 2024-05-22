package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBStoryDAO;
import edu.byu.cs.tweeter.server.service.StoryService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class GetStoryHandler extends lambdaHandler implements RequestHandler<GetStoryRequest, GetStoryResponse> {
    @Override
    public GetStoryResponse handleRequest(GetStoryRequest getStoryRequest, Context context) {
        StoryService storyService = new StoryService(new DDBAuthDAO(getClient()), new DDBStoryDAO(getClient()));
        return storyService.getStory(getStoryRequest);
    }
}
