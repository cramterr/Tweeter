package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowTaskHandler extends TaskHandlers {
    private FollowService.FollowObserver observer;
    private User selectedUser;

    public FollowTaskHandler(FollowService.FollowObserver observer, User selectedUser) {
        super(observer, "Failed to follow: ", "Failed to follow because of exception: ");
        this.observer = observer;
        this.selectedUser = selectedUser;
    }

    @Override
    protected void onSuccess(Message msg) {
        observer.updateSelectedUserFollowingAndFollowers(selectedUser);
        observer.updateFollowButton(false);
    }
    @Override
    protected void onEnd() {
        observer.enableFollowButton();
    }
}
