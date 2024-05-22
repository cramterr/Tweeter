package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowTaskHandler extends TaskHandlers {
    private FollowService.FollowObserver observer;
    private User selectedUser;

    public UnfollowTaskHandler(FollowService.FollowObserver observer, User selectedUser) {
        super(observer, "Failed to unfollow: ", "Failed to unfollow because of exception: ");
        this.observer = observer;
        this.selectedUser = selectedUser;
    }

    @Override
    protected void onSuccess(Message msg) {
        observer.updateSelectedUserFollowingAndFollowers(selectedUser);
        observer.updateFollowButton(true);
    }

    @Override
    protected void onEnd() {
        observer.enableFollowButton();
    }
}
