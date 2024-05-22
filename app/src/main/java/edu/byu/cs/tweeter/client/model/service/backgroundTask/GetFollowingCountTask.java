package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowingCountTask";
    private static final String URL_PATH = "/getfollowingcount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void runCountTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, targetUserAlias);
            GetFollowingCountResponse response = getServerFacade().getFollowingCount(request, URL_PATH);

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
