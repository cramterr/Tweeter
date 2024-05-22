package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.FeedUpdatePage;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.DDBAuthDAO;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;




public class PostUpdateFeedMessages extends lambdaHandler implements RequestHandler<SQSEvent, Void> {
    SqsClient sqsClient = SqsClient.builder().build();
    String queueUrl = "https://sqs.us-west-2.amazonaws.com/767030191187/updateFeedsQ";

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        for (SQSMessage sqsMessage : input.getRecords()) {
            String messageBody = sqsMessage.getBody();
            Status status = Status.fromString(messageBody);
            String alias = status.getUser().getAlias();
            FollowService service = new FollowService(new DDBAuthDAO(getClient()), new DDBUserDAO(getClient()), new DDBFollowDAO(getClient()));
            boolean hasMorePages = true;
            String lastFollower = null;
            while(hasMorePages) {
                Pair<List<String>, Boolean> pair = service.getBatchFollowers(alias, lastFollower);
                lastFollower = pair.getFirst().get(pair.getFirst().size()-1);
                hasMorePages = pair.getSecond();
                FeedUpdatePage newPage = new FeedUpdatePage(pair.getFirst(), status);
                sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(newPage.toJson())
                        .build());
            }
        }
        return null;
    }
}
