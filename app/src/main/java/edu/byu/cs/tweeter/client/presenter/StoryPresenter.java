package edu.byu.cs.tweeter.client.presenter;

import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    public StoryPresenter(StoryPresenter.View view) {
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
        errorType = "Failed to get story: ";
        exceptionType = "Failed to get story because of exception: ";
    }

    @Override
    public void getItems(User user) {
        statusService.loadMoreItems("Story", Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new StatusServiceObserver(), errorType, exceptionType);
    }
}
