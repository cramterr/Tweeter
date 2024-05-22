package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class UnfollowHandler extends lambdaHandler implements RequestHandler<UnfollowRequest, UnfollowResponse> {
    @Override
    public UnfollowResponse handleRequest(UnfollowRequest unfollowRequest, Context context) {
        FollowService followService = new FollowService(new DDBAuthDAO(getClient()), new DDBUserDAO(getClient()), new DDBFollowDAO(getClient()));
        return followService.unfollow(unfollowRequest);
    }
}
