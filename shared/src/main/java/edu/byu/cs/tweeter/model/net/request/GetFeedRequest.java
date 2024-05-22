package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedRequest {

    private AuthToken authToken;
    private String targetAlias;
    private int limit;
    private Status lastStatus;


    private GetFeedRequest() {}

    public GetFeedRequest(AuthToken authToken, String targetAlias, int limit, Status lastStatus) {
        this.authToken = authToken;
        this.targetAlias = targetAlias;
        this.limit = limit;
        this.lastStatus = lastStatus;
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


    public int getLimit() {
        return limit;
    }


    public void setLimit(int limit) {
        this.limit = limit;
    }


    public Status getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }
}
