package edu.byu.cs.tweeter.client.model.service;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public interface ServiceObserver {
    default void displayMessage(String msg){}
    default <T> void addMoreItems(List<T> items, boolean hasMorePages){}
    default void displayError(String message) {
        //display error
        Log.e("ME", message);
    }
    default void displayException(String message) {
        //display exception
        Log.e("ME", message);
    }
    default void logException(Exception ex) {
        Log.e("ME", ex.getMessage());
    }
    default void cancelToast() {}
}
