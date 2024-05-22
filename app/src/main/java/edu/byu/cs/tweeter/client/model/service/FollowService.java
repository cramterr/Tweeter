package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.TaskHandlers;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface FollowObserver extends ServiceObserver {
        default void updateSelectedUserFollowingAndFollowers(User selectedUser) {}
        default void updateFollowButton(boolean removed) {}
        default void enableFollowButton() {}
        default void isFollowerButton(boolean isFollower) {}
        default void setFollowCount(int count, String option) {}
    }

    public void updateFollowingandFollowers(User selectedUser, FollowObserver observer) {

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountTaskHandler(observer));
        BackgroundTaskUtils.runTask(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountTaskHandler(observer));
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    public void isFollower(User selectedUser, FollowObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerTaskHandler(observer));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void unfollow(User selectedUser, FollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowTaskHandler(observer, selectedUser));
        BackgroundTaskUtils.runTask(unfollowTask);
        observer.displayMessage("Removing " + selectedUser.getName() + "...");
    }

    public void follow(User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowTaskHandler(observer, selectedUser));
        BackgroundTaskUtils.runTask(followTask);
        observer.displayMessage("Adding " + selectedUser.getName() + "...");
    }

    public void loadMoreItems(String type, AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, FollowObserver observer, String errorType, String exceptionType) {
        if (type == "Followers") {
            GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                    user, pageSize, lastFollowee, new GetFollowersTaskHandler(observer, errorType, exceptionType));
            BackgroundTaskUtils.runTask(getFollowersTask);
        } else if (type == "Following") {
            GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                    user, pageSize, lastFollowee, new GetFollowingTaskHandler(observer, errorType, exceptionType));
            BackgroundTaskUtils.runTask(getFollowingTask);
        }
    }
}
