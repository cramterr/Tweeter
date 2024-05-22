package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetFollowingCountRequest {
    private AuthToken authToken;
    private String targetAlias;


    private GetFollowingCountRequest() {}

    public GetFollowingCountRequest(AuthToken authToken, String targetAlias) {
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
