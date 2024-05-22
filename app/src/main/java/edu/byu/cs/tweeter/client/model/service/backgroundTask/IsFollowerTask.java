package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    private static final String LOG_TAG = "IsFollowerTask";
    private static final String URL_PATH = "/isfollower";

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        isFollower = new Random().nextInt() > 0;

        // Call sendSuccessMessage if successful
        //sendSuccessMessage();
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
        try {
            String followerAlias = follower == null ? null : follower.getAlias();
            String followeeAlias = followee == null ? null : followee.getAlias();

            IsFollowerRequest request = new IsFollowerRequest(authToken, followerAlias, followeeAlias);
            IsFollowerResponse response = getServerFacade().isFollower(request, URL_PATH);

            if (response.isSuccess()) {
                this.isFollower = response.getIsFollower();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
