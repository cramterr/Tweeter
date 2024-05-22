package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public interface IAuthDAO {
    Pair<Boolean, String> verifyAuth(AuthToken authToken) throws DynamoDbException;

    Boolean verifyLogin(String userAlias, String password) throws DynamoDbException;

    Boolean verifyNotDuplicate(String userAlias) throws DynamoDbException;

    Boolean logout(String userAlias) throws DynamoDbException;

    Boolean login(String userAlias, AuthToken authToken) throws DynamoDbException;

    Boolean register(String userAlias, String password, AuthToken authToken) throws DynamoDbException;
}
