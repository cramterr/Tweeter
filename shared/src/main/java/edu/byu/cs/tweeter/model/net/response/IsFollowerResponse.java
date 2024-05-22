package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerResponse extends Response {

    private boolean isFollower;
    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

}
