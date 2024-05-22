package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IStoryDAO {
    Pair<List<Status>, Boolean> getStory(String targetAlias, int limit, Status lastStatus);
    Boolean postStatus(String targetAlias, Status status);
}
