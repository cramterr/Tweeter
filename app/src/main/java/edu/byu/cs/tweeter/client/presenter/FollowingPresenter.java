package edu.byu.cs.tweeter.client.presenter;

import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {

    public FollowingPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
        errorType = "Failed to get following: ";
        exceptionType = "Failed to get following because of exception: ";
    }

    @Override
    public void getItems(User user) {
        followService.loadMoreItems("Following", Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new FollowServiceObserver(), errorType, exceptionType);
    }
}
