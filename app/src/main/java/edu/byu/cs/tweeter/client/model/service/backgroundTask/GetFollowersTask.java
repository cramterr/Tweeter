package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private static final String LOG_TAG = "GetFollowersTask";
    private static final String URL_PATH = "/getfollowers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected void getItems() {
        //return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastFollowerAlias = getLastItem() == null ? null : getLastItem().getAlias();

            GetFollowersRequest request = new GetFollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
            GetFollowersResponse response = getServerFacade().getFollowers(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getFollowers();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers", ex);
            sendExceptionMessage(ex);
        }
    }
}
