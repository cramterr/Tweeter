package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class GetFollowersHandler extends lambdaHandler implements RequestHandler<GetFollowersRequest, GetFollowersResponse> {
    @Override
    public GetFollowersResponse handleRequest(GetFollowersRequest getFollowersRequest, Context context) {
        FollowService followService = new FollowService(new DDBAuthDAO(getClient()), new DDBUserDAO(getClient()), new DDBFollowDAO(getClient()));
        return followService.getFollowers(getFollowersRequest);
    }
}
