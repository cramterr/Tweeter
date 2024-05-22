package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;

public interface IUserDAO {
    User getUser(String userAlias);
    void register(String userAlias, User newUser);
}
