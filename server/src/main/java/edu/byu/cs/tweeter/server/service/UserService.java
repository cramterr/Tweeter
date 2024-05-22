package edu.byu.cs.tweeter.server.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IS3DAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.S3DAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class UserService {

    public UserService(IS3DAO s3dao, IUserDAO userdao, IAuthDAO authdao) {
        this.s3DAO = s3dao;
        this.userDAO = userdao;
        this.authDAO = authdao;
    }

    private IS3DAO s3DAO;
    private IUserDAO userDAO;
    private IAuthDAO authDAO;

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        try {
            if(authDAO.verifyLogin(request.getUsername(), request.getPassword())) {
                AuthToken authToken = generateAuthToken();
                Boolean success = authDAO.login(request.getUsername(), authToken);
                if(success) {
                    User user = userDAO.getUser(request.getUsername());
                    return new LoginResponse(user, authToken);
                } else {
                    return new LoginResponse("Error adding Authtoken");
                }
            } else {
                return new LoginResponse("Username or Password incorrect");
            }
        } catch (DynamoDbException e) {
            return new LoginResponse(e.getMessage());
        }
    }

    private AuthToken generateAuthToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String token = bytes.toString();
        return new AuthToken(token, Instant.now().getEpochSecond());
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        }else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        try {
            if (authDAO.verifyNotDuplicate(request.getUsername())) {
                String imageURL = s3DAO.uploadImage(request.getImage());
                User newUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
                userDAO.register(request.getUsername(), newUser);
                AuthToken authToken = generateAuthToken();
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String hash = encoder.encode(request.getPassword());
                authDAO.register(request.getUsername(), hash, authToken);
                return new RegisterResponse(newUser, authToken);
            } else {
                return new RegisterResponse("Username already exists");
            }
        } catch (DynamoDbException e) {
            return new RegisterResponse(e.getMessage());
        } catch (S3Exception e) {
            return new RegisterResponse(e.getMessage());
        }
    }

    public LogoutResponse logout(LogoutRequest request) {

        try {
            Pair<Boolean, String> pair = authDAO.verifyAuth(request.getAuthToken());
            if (pair.getFirst()) {
                Boolean success = authDAO.logout(pair.getSecond());
                return new LogoutResponse(success);
            }
        } catch (DynamoDbException e) {
            return new LogoutResponse(e.getMessage());
        }

        return new LogoutResponse("Couldn't logout for some reason");
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getTargetAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a target user alias");
        }

        try {
            //Pair<Boolean, String> verified = authDAO.verifyAuth(request.getAuthToken());
            if(true) {
                User user = userDAO.getUser(request.getTargetAlias());
                return new GetUserResponse(user);
            } else {
                return new GetUserResponse("Failed to verify");
            }
        } catch (DynamoDbException e) {
            return new GetUserResponse(e.getMessage());
        }

    }
}
