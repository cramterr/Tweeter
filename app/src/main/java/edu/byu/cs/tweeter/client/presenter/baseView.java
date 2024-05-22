package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface baseView {
    default void displayMessage(String message) {}
    default void displayError(String msg) {}
    default void setLoadingFooter(boolean toggle) {}
    default <T> void addMoreItems(List<T> items) {}
    default void newIntent(User user) {}
    default void startActivity() {}
    default void startToast(String msg) {}
    default void stopToast() {}
    default void logoutUser() {}
}
