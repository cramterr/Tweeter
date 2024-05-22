package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryTaskHandler extends TaskHandlers {
    private StatusService.StatusObserver observer;

    public GetStoryTaskHandler(StatusService.StatusObserver observer, String errorMessage, String exceptionMessage) {
        super(observer, errorMessage, exceptionMessage);
        this.observer = observer;
    }
    @Override
    protected void onSuccess(Message msg) {
        List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.addMoreItems(statuses, hasMorePages);
    }
}
