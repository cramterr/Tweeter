package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserTaskHandler extends TaskHandlers {
    private UserService.UserObserver observer;

    public GetUserTaskHandler(UserService.UserObserver observer) {
        super(observer, "Failed to get user's profile: ", "Failed to get user's profile because of exception: ");
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
        observer.newIntent(user);
    }
}
