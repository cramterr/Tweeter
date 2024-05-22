package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;

public class GetFollowersCountTaskHandler extends TaskHandlers {
    private FollowService.FollowObserver observer;

    public GetFollowersCountTaskHandler(FollowService.FollowObserver observer) {
        super(observer, "Failed to get followers count: ", "Failed to get followers count because of exception: ");
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
        observer.setFollowCount(count, "follower");
    }
}
