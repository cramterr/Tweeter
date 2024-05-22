package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public class PostStatusTaskHandler extends TaskHandlers {
    private StatusService.StatusObserver observer;

    public PostStatusTaskHandler(StatusService.StatusObserver observer) {
        super(observer, "Failed to post status: ", "Failed to post status because of exception: ");
        this.observer = observer;
    }
    @Override
    protected void onSuccess(Message msg) {
        observer.cancelToast();
        observer.displayMessage("Successfully Posted!");
    }
}
