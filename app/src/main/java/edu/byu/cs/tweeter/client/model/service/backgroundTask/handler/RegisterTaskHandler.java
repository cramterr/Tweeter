package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterTaskHandler extends TaskHandlers {
    private UserService.UserObserver observer;

    public RegisterTaskHandler(UserService.UserObserver observer) {
        super(observer, "Failed to register: ", "Failed to register because of exception: ");
        this.observer = observer;
    }

    @Override
    protected void onSuccess(Message msg) {
        User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.newIntent(registeredUser);
        observer.cancelToast();
        observer.displayMessage("Hello " + Cache.getInstance().getCurrUser().getName());
        try {
            observer.startActivity();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
