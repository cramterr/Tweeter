package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IFeedDAO {
    Pair<List<Status>, Boolean> getFeed(String targetAlias, int limit, Status lastStatus);

    void postStatus(String targetAlias, Status status);
}
