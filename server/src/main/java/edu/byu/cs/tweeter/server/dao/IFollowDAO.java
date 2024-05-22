package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface IFollowDAO {
    void followUser(String followerId, String followeeId);
    void unfollowUser(String followerId, String followeeId);
    Pair<List<String>, Boolean> getFollowers(String userId, int limit, String lastFolloweeAlias);
    Pair<List<String>, Boolean> getFollowees(String userId, int limit, String lastFolloweeAlias);
    Integer getFolloweeCount(String followerAlias);
    Integer getFollowerCount(String followerAlias);
    Boolean isFollower(String followerAlias, String followeeAlias);
}
