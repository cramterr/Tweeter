package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedService {

    public FeedService (IFeedDAO feeddao) {
        this.feedDAO = feeddao;
    }

    private IFeedDAO feedDAO;


    public GetFeedResponse getFeed(GetFeedRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        try {
            Pair<List<Status>, Boolean> pair = feedDAO.getFeed(request.getTargetAlias(), request.getLimit(), request.getLastStatus());
            return new GetFeedResponse(pair.getFirst(), pair.getSecond());
        }  catch (DynamoDbException e) {
            return new GetFeedResponse(e.getMessage());
        }
    }

    public void postStatus(String targetAlias, Status status) {
        if(status == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        try {
            feedDAO.postStatus(targetAlias, status);
        } catch (DynamoDbException e) {
            throw e;
        }
    }
}
