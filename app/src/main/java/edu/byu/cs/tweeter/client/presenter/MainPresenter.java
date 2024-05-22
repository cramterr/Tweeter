package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {
    private static final String LOG_TAG = "MainActivity";

    public interface View extends baseView {
        void updateFollowButton(boolean removed);
        void logoutUser();
        void cancelLogoutToast();
        void cancelPostingToast();
        void showLogoutToast();
        void showPostingToast();
        void setFollowerCount(int count);
        void setFolloweeCount(int count);
        void enableFollowButton();
        void isFollowerButton(boolean isFollower);
    }

    private View view;

    public MainPresenter(View view) {
        this.view = view;
        userService = new UserService();
        followService = new FollowService();
    }

    protected StatusService getStatusService() {
        if (statusService == null) {
            return new StatusService();
        } else {
            return statusService;
        }
    }

    public void presenterUpdateSelectedUserFollowingAndFollowers(User selectedUser) {
        followService.updateFollowingandFollowers(selectedUser, new FollowServiceObserver());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(selectedUser, new FollowServiceObserver());
    }

    public void unfollow(User selectedUser) {
        followService.unfollow(selectedUser, new FollowServiceObserver());
    }

    public void follow(User selectedUser) {
        followService.follow(selectedUser, new FollowServiceObserver());
    }

    public void logout() {
        view.showLogoutToast();
        userService.logout(new MainPresenter.UserServiceObserver());
    }

    public void newStatus(String post) {
        getStatusService().newStatus(post, new MainPresenter.StatusServiceObserver());
    }

    private class UserServiceObserver implements UserService.UserObserver {

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void logoutUser() {
            view.logoutUser();
        }

        @Override
        public void cancelToast() {
            view.cancelLogoutToast();
        }

    }

    public class StatusServiceObserver implements StatusService.StatusObserver {

        @Override
        public void displayMessage(String msg) {
            view.displayMessage(msg);
        }

        @Override
        public void logException(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
        }

        @Override
        public void showToast() {
            view.showPostingToast();
        }

        @Override
        public void cancelToast() {
            view.cancelPostingToast();
        }

    }

    private class FollowServiceObserver implements FollowService.FollowObserver {

        @Override
        public void displayMessage(String msg) {
            view.displayMessage(msg);
        }

        @Override
        public void setFollowCount(int count, String option) {
            if ("followee".equals(option)) {
                view.setFolloweeCount(count);
            } else if ("follower".equals(option)) {
                view.setFollowerCount(count);
            } else {
                // Do Nothing
            }
        }

        @Override
        public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
            presenterUpdateSelectedUserFollowingAndFollowers(selectedUser);
        }

        @Override
        public void updateFollowButton(boolean removed) {
            view.updateFollowButton(removed);
        }

        @Override
        public void enableFollowButton() {
            view.enableFollowButton();
        }

        @Override
        public void isFollowerButton(boolean isFollower) {
            view.isFollowerButton(isFollower);
        }
    }

}
