package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.FeedUpdatePage;
import edu.byu.cs.tweeter.server.dao.DDBFeedDAO;
import edu.byu.cs.tweeter.server.service.FeedService;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;


public class UpdateFeeds extends lambdaHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        for (SQSMessage sqsMessage : input.getRecords()) {
            String messageBody = sqsMessage.getBody();
            FeedUpdatePage page = FeedUpdatePage.fromString(messageBody);
            FeedService feedService = new FeedService(new DDBFeedDAO(getClient()));
            for (String follower : page.followers) {
                feedService.postStatus(follower, page.status);
            }
        }
        return null;
    }
}
