package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.DDBFeedDAO;
import edu.byu.cs.tweeter.server.service.FeedService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class GetFeedHandler extends lambdaHandler implements RequestHandler<GetFeedRequest, GetFeedResponse> {
    @Override
    public GetFeedResponse handleRequest(GetFeedRequest getFeedRequest, Context context) {
        FeedService feedService = new FeedService(new DDBFeedDAO(getClient()));
        return feedService.getFeed(getFeedRequest);
    }
}
