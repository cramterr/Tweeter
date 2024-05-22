package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowersCountTask";
    private static final String URL_PATH = "/getfollowercount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void runCountTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();

            GetFollowersCountRequest request = new GetFollowersCountRequest(authToken, targetUserAlias);
            GetFollowersCountResponse response = getServerFacade().getFollowersCount(request, URL_PATH);

            if (response.isSuccess()) {
                this.count = response.getCount();
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
