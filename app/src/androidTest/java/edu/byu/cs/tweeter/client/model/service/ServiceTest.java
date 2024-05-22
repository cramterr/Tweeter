package edu.byu.cs.tweeter.client.model.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class ServiceTest {

    private StatusService statusServiceSpy;
    private StatusServiceObserver observer;
    private User currentUser;
    private AuthToken currentAuthToken;

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setUp() {
        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();

        statusServiceSpy = Mockito.spy(new StatusService());
        observer = new StatusServiceObserver();

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testRegister_Success() throws InterruptedException {
        statusServiceSpy.loadMoreItems("Story", currentAuthToken, currentUser, 10, null, observer, "Error", "Exception");
        awaitCountDownLatch();

        Pair<List<Status>, Boolean> expectedStory = FakeData.getInstance().getPageOfStatus(null, 10);
        Assertions.assertTrue(observer.isSuccess());
        Assertions.assertNull(observer.getMessage());
        Assertions.assertEquals(expectedStory.getFirst(), observer.getStory());
        Assertions.assertTrue(observer.getHasMorePages());
        Assertions.assertNull(observer.getException());
    }

    private class StatusServiceObserver implements StatusService.StatusObserver {

        private boolean success;
        public String message;
        public List<Status> story;
        public boolean hasMorePages;
        public String exception;

        @Override
        public void addMoreItems(List items, boolean morePages) {
            this.success = true;
            this.message = null;
            this.story = items;
            this.hasMorePages = morePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void displayError(String message) {
            this.success = false;
            this.message = message;
            this.story = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void displayException(String exception) {
            this.success = false;
            this.message = null;
            this.story = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStory() {
            return story;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public String getException() {
            return exception;
        }
    }

}

