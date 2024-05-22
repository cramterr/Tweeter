package edu.byu.cs.tweeter.client.presenter;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public class PostTest {

    private MainPresenter.View view;
    private StatusService service;

    private MainPresenter presenterSpy;

    @BeforeEach
    public void setUp() {
        view = Mockito.mock(MainPresenter.View.class);
        service = Mockito.mock(StatusService.class);

        presenterSpy = Mockito.spy(new MainPresenter(view));
        Mockito.when(presenterSpy.getStatusService()).thenReturn(service);
    }

    @Test
    public void testPostStatus_Success() {
        Answer<Void> answer = createAnswer("Successfully Posted!");

        postNewStatus(answer);
        verify(view).displayMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_Failure() {
        Answer<Void> answer = createAnswer("Failed to post status: Error Message");

        postNewStatus(answer);
        verify(view).displayMessage("Failed to post status: Error Message");
    }

    @Test
    public void testPostStatus_Exception() {
        Answer<Void> answer = createAnswer("Failed to post status because of exception: Exception Message");

        postNewStatus(answer);
        verify(view).displayMessage("Failed to post status because of exception: Exception Message");
    }

    private Answer<Void> createAnswer(String message) {
        return new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.StatusServiceObserver observer = invocation.getArgument(1, MainPresenter.StatusServiceObserver.class);
                observer.showToast();
                observer.cancelToast();
                observer.displayMessage(message);
                return null;
            }
        };
    }

    private void postNewStatus(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(service).newStatus(eq("Some post"), Mockito.any());
        presenterSpy.newStatus("Some post");

        verifyCommonVerifications();
    }

    private void verifyCommonVerifications() {
        verify(view).showPostingToast();
        verify(view).cancelPostingToast();
    }
}
