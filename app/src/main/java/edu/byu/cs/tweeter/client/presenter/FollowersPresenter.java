package edu.byu.cs.tweeter.client.presenter;

import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    public FollowersPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
        errorType = "Failed to get followers: ";
        exceptionType = "Failed to get followers because of exception: ";
    }

    @Override
    public void getItems(User user) {
        followService.loadMoreItems("Followers", Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new FollowServiceObserver(), errorType, exceptionType);
    }


}