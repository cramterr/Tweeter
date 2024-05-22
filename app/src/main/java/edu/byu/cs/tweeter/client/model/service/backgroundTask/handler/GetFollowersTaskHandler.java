package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersTaskHandler extends TaskHandlers {

    private final FollowService.FollowObserver observer;

    public GetFollowersTaskHandler(FollowService.FollowObserver observer, String errorType, String exceptionType) {
        super(observer, errorType, exceptionType);
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        List<User> follows = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.addMoreItems(follows, hasMorePages);
    }

}
