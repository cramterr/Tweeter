package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {

    public FeedPresenter(FeedPresenter.View view) {
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
        errorType = "Failed to get feed: ";
        exceptionType = "Failed to get feed because of exception: ";
    }

    @Override
    public void getItems(User user) {
        statusService.loadMoreItems("Feed", Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new FeedPresenter.StatusServiceObserver(), errorType, exceptionType);
    }

}
