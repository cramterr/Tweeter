package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFollowersCountRequest {
    private AuthToken authToken;
    private String targetAlias;


    private GetFollowersCountRequest() {}

    public GetFollowersCountRequest(AuthToken authToken, String targetAlias) {
        this.authToken = authToken;
        this.targetAlias = targetAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getTargetAlias() {
        return targetAlias;
    }

    public void setTargetAlias(String targetAlias) {
        this.targetAlias = targetAlias;
    }

}
