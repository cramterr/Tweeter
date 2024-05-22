package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetStoryTask";
    private static final String URL_PATH = "/getstory";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected void getItems() {
        //return getFakeData().getPageOfStatus(getLastItem(), getLimit());
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            Status lastStatus = getLastItem();

            GetStoryRequest request = new GetStoryRequest(authToken, targetUserAlias, limit, lastStatus);
            GetStoryResponse response = getServerFacade().getStory(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getStory();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }
}
