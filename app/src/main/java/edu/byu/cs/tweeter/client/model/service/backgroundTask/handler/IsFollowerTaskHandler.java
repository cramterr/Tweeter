package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;

public class IsFollowerTaskHandler extends TaskHandlers {
    private FollowService.FollowObserver observer;

    public IsFollowerTaskHandler(FollowService.FollowObserver observer) {
        super(observer, "Failed to determine following relationship: ", "Failed to determine following relationship because of exception: ");
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.isFollowerButton(isFollower);
        //observer.updateFollowButton(!isFollower);
    }
}
