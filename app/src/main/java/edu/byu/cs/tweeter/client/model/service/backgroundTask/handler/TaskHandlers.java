package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;

public abstract class TaskHandlers<T extends ServiceObserver> extends Handler {
    protected T observer;
    protected String errorMessage;
    protected String exceptionMessage;

    public TaskHandlers(T observer, String errorMessage, String exceptionMessage) {
        super(Looper.getMainLooper());
        this.observer = observer;
        this.errorMessage = errorMessage;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY)) {
            onSuccess(msg);
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            onMessage(msg);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            onException(msg);
        }
        onEnd();
    }

    protected abstract void onSuccess(Message msg);

    protected void onMessage(Message msg) {
        String message = msg.getData().getString(GetFeedTask.MESSAGE_KEY);
        observer.displayMessage(errorMessage + message);
    }

    protected void onException(Message msg) {
        Exception ex = (Exception) msg.getData().getSerializable(GetFeedTask.EXCEPTION_KEY);
        observer.displayMessage(exceptionMessage + ex.getMessage());
    }
    protected void onEnd() {}
}
