package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;

public class GetFollowingCountTaskHandler extends TaskHandlers {
    private FollowService.FollowObserver observer;

    public GetFollowingCountTaskHandler(FollowService.FollowObserver observer) {
        super(observer, "Failed to get following count: ", "Failed to get following count because of exception: ");
        this.observer =observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
        observer.setFollowCount(count, "followee");
    }
}
