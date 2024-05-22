package edu.byu.cs.tweeter.client.presenter;

import android.view.View;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Presenter<T> {
    public static final int PAGE_SIZE = 10;
    public interface View extends baseView { }

    public View view;
    public FollowService followService;
    public StatusService statusService;
    public UserService userService;

    protected class UserServiceObserver implements UserService.UserObserver {

        @Override
        public void newIntent(User user) {
            view.newIntent(user);
        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void startActivity() {
            view.startActivity();
        }

        @Override
        public void cancelToast() {
            view.stopToast();
        }

        @Override
        public void logoutUser() {view.logoutUser();}
    }
}
