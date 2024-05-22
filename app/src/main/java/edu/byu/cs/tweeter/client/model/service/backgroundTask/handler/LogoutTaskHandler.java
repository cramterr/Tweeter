package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LogoutTaskHandler extends TaskHandlers {
    private UserService.UserObserver observer;

    public LogoutTaskHandler(UserService.UserObserver observer) {
        super(observer, "Failed to logout: ", "Failed to logout because of exception: ");
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        observer.cancelToast();
        observer.logoutUser();
    }
}
