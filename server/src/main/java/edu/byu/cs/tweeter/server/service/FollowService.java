package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.IAuthDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


public class FollowService {

    public FollowService (IAuthDAO authdao, IUserDAO userdao, IFollowDAO followdao) {
        this.authDAO = authdao;
        this.userDAO = userdao;
        this.followDAO = followdao;
    }

    private IAuthDAO authDAO;
    private IUserDAO userDAO;
    private IFollowDAO followDAO;

    //implemented to preserve tests.
    public IFollowDAO getFollowingDAO() {
        return followDAO;
    }

    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        try {
            Pair<List<String>, Boolean> pair = getFollowingDAO().getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
            List<User> followees = new ArrayList<>();
            for (String userAlias : pair.getFirst()) {
                User user = userDAO.getUser(userAlias);
                followees.add(user);
            }
            return new GetFollowingResponse(followees, pair.getSecond());
        } catch (DynamoDbException e) {
            return new GetFollowingResponse(e.getMessage());
        }
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        try {
            Pair<List<String>, Boolean> pair = getFollowingDAO().getFollowers(request.getFollowerAlias(), request.getLimit(), request.getLastFollowerAlias());
            List<User> followers = new ArrayList<>();
            for (String userAlias : pair.getFirst()) {
                User user = userDAO.getUser(userAlias);
                followers.add(user);
            }
            return new GetFollowersResponse(followers, pair.getSecond());
        } catch (DynamoDbException e) {
            return new GetFollowersResponse(e.getMessage());
        }
    }

    public Pair<List<String>, Boolean> getBatchFollowers(String targetAlias, String lastAlias) {
        return getFollowingDAO().getFollowers(targetAlias, 25, lastAlias);
    }


    public FollowResponse follow(FollowRequest request) {
        if (request.getFollowee() == null || request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias and an Auth Token");
        }

        try {
            // Verify the authenticity of the follower using the Auth Token
            Pair<Boolean, String> followerAlias = authDAO.verifyAuth(request.getAuthToken());

            if (followerAlias.getFirst()) {
                // If the follower is authenticated, proceed to follow the user
                try {
                    getFollowingDAO().followUser(followerAlias.getSecond(), request.getFollowee().getAlias());
                    return new FollowResponse(true);
                } catch (DynamoDbException e) {
                    return new FollowResponse(e.getMessage());
                }
            } else {
                return new FollowResponse("Couldn't find user");
            }
        } catch (DynamoDbException e) {
            return new FollowResponse(e.getMessage());
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an Auth Token");
        }
        Pair<Boolean, String> followerAlias;
        try {
            followerAlias = authDAO.verifyAuth(request.getAuthToken());
        } catch (DynamoDbException e) {
            return new UnfollowResponse(e.getMessage());
        }
        if (followerAlias.getFirst()) {
            try {
                getFollowingDAO().unfollowUser(followerAlias.getSecond(), request.getFollowee().getAlias());
            } catch (DynamoDbException e) {
                return new UnfollowResponse(e.getMessage());
            }
        } else {
            return new UnfollowResponse("Couldn't find user");
        }
        return new UnfollowResponse(true);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        Boolean isFollower;
        try {
            isFollower = getFollowingDAO().isFollower(request.getFollowerAlias(), request.getFolloweeAlias());
        } catch (DynamoDbException e) {
            return new IsFollowerResponse(e.getMessage());
        }

        return new IsFollowerResponse(isFollower);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        Integer count = getFollowingDAO().getFollowerCount(request.getTargetAlias());
        return new GetFollowersCountResponse(count);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        Integer count = getFollowingDAO().getFolloweeCount(request.getTargetAlias());
        return new GetFollowingCountResponse(count);
    }
}
