package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter{
    private String alias;
    private String password;

    public LoginPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    public void loginRequest(String alias, String password) {
        this.alias = alias;
        this.password = password;
        try {
            validateLogin();
            view.displayError(null);
            view.startToast("Logging In...");
            // Send the login request.
            userService.LoginRequest(alias, password, new UserServiceObserver());
        } catch (Exception e) {
            view.displayError(e.getMessage());
        }
    }

    public void validateLogin() {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

}
