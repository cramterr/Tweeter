package edu.byu.cs.tweeter.client.presenter;

import android.widget.TextView;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {
    public T lastItem;

    public String errorType;
    public String exceptionType;
    public boolean isLoading;
    public boolean hasMorePages;

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(user);
        }
    }

    public void getUser(String clickable) {
        userService.getUser(clickable, new UserServiceObserver());
    }

    public void getItems(User user) {}

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    protected class StatusServiceObserver implements StatusService.StatusObserver {

        @Override
        public void addMoreItems(List items, boolean morePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            hasMorePages = morePages;
            lastItem = (items.size() > 0) ? (T) items.get(items.size() - 1) : null;
            view.addMoreItems(items);
        }

        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(errorType + message);
        }

        @Override
        public void displayException(String exception) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(exceptionType + exception);
        }
    }

    protected class FollowServiceObserver implements FollowService.FollowObserver {

        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(errorType + message);
        }

        @Override
        public void displayException(String exception) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage(exceptionType + exception);
        }

        @Override
        public void addMoreItems(List follows, boolean morePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            hasMorePages = morePages;
            lastItem = (follows.size() > 0) ? (T) follows.get(follows.size() - 1) : null;
            view.addMoreItems(follows);
        }
    }
}
