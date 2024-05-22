package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;
import android.widget.EditText;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.TaskHandlers;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public interface UserObserver extends ServiceObserver {
        default void newIntent(User user) {}
        default void startActivity() {}
        default void logoutUser() {}
    }

    public void getUser(String userAlias, UserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserTaskHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
        observer.displayMessage("Getting user's profile...");
    }

    public void LoginRequest (String alias, String password, UserObserver observer) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginTaskHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void logout(UserObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutTaskHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    public void register(EditText firstName, EditText lastName, EditText alias, EditText password, String imageBytesBase64, UserObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName.getText().toString(), lastName.getText().toString(),
                alias.getText().toString(), password.getText().toString(), imageBytesBase64, new RegisterTaskHandler(observer));

        BackgroundTaskUtils.runTask(registerTask);
    }

}
