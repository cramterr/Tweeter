package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class FollowHandler extends lambdaHandler implements RequestHandler<FollowRequest, FollowResponse> {
    @Override
    public FollowResponse handleRequest(FollowRequest followRequest, Context context) {
        FollowService followService = new FollowService(new DDBAuthDAO(getClient()), new DDBUserDAO(getClient()), new DDBFollowDAO(getClient()));
        return followService.follow(followRequest);
    }
}
