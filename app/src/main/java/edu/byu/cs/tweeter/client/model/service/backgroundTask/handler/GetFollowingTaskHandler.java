package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Handles messages from the background task indicating that the task is done, by invoking
 * methods on the observer.
 */
public class GetFollowingTaskHandler extends TaskHandlers {

    private final FollowService.FollowObserver observer;

    public GetFollowingTaskHandler(FollowService.FollowObserver observer, String errorType, String exceptionType) {
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
