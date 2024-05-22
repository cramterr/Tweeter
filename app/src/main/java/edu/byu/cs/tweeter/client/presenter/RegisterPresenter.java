package edu.byu.cs.tweeter.client.presenter;

import android.graphics.drawable.Drawable;
import android.widget.EditText;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter {

    public RegisterPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    public void registerRequest(String imageBytesBase64, EditText firstName, EditText lastName, EditText alias, EditText password) {
        try {
            //validateRegistration(imageToUpload.getDrawable(), firstName, lastName, alias, password);
            view.displayError(null);
            view.startToast("Registering...");

            // Send register request.
            userService.register(firstName, lastName, alias, password, imageBytesBase64, new UserServiceObserver());
        } catch (Exception e) {
            view.displayError(e.getMessage());
        }
    }

    public void validateRegistration(Drawable image, EditText firstName, EditText lastName, EditText alias, EditText password) {
        if (firstName.getText().length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.getText().length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.getText().length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (image == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

}
