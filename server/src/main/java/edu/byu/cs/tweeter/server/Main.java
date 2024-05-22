package edu.byu.cs.tweeter.server;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DDBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DDBUserDAO;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Main {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 300;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@MrPopular";

    public static void main(String[] args) {
        DynamoDbClient dynamoDB = DynamoDbClient.builder()
                .region(Region.US_WEST_2) // Specify your AWS region
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();



        // Get instance of DAOs by way of the Abstract Factory Pattern
        DDBUserDAO userDAO = new DDBUserDAO(dynamoDB);
        DDBFollowDAO followDAO = new DDBFollowDAO(dynamoDB);

        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String alias = "@girl" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User("girl", " "+i, alias, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpYFswXN0uVE2c0u_08qQzTe104ib2e6HlsTWn3xTPIjXiGCiqpPVVQ1K4NabosCIBRfA&usqp=CAU");
            userDAO.register(alias, user);
            followDAO.followUser(alias, FOLLOW_TARGET);
        }
    }
}
