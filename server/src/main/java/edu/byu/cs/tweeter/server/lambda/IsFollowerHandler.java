package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class IsFollowerHandler extends lambdaHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {
    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest isFollowerRequest, Context context) {
        FollowService followService = new FollowService(new DDBAuthDAO(getClient()), new DDBUserDAO(getClient()), new DDBFollowDAO(getClient()));
        return followService.isFollower(isFollowerRequest);
    }
}
