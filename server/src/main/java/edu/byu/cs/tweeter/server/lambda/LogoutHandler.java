package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.dao.S3DAO;
import edu.byu.cs.tweeter.server.service.UserService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LogoutHandler extends lambdaHandler implements RequestHandler<LogoutRequest, LogoutResponse> {
    @Override
    public LogoutResponse handleRequest(LogoutRequest logoutRequest, Context context) {
        UserService userService = new UserService(new S3DAO(), new DDBUserDAO(getClient()), new DDBAuthDAO(getClient()));
        return userService.logout(logoutRequest);
    }
}
