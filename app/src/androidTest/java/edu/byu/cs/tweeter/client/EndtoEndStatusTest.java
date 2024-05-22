package edu.byu.cs.tweeter.client;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;

import edu.byu.cs.tweeter.client.model.net.ClientCommunicator;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class EndtoEndStatusTest {

    private ServerFacade serverFacadeSpy;
    private ClientCommunicator clientCommunicatorSpy;
    private MainPresenter mainPresenter;
    private StatusService statusService;
    private UserService userService;
    private UserService.UserObserver observer;
    private LoginRequest loginRequestSpy;
    private LoginResponse loginResponseMock;
    private GetStoryRequest GetStoryRequestSpy;
    private GetStoryResponse GetStoryResponseMock;
    private PostStatusResponse PostStatusResponseMock;
    MainPresenter.View view;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        view = Mockito.mock(MainPresenter.View.class);
        mainPresenter = new MainPresenter(view);
        statusService = Mockito.mock(StatusService.class);
        userService = Mockito.spy(new UserService());
        observer = Mockito.mock(UserService.UserObserver.class);

        serverFacadeSpy = Mockito.spy(new ServerFacade());
        clientCommunicatorSpy = Mockito.spy(new ClientCommunicator("https://z7ivyx91od.execute-api.us-west-2.amazonaws.com/Dev"));
        Mockito.when(serverFacadeSpy.getClientCommunicator()).thenReturn(clientCommunicatorSpy);
    }

    @Test
    public void testPostStatusAndVerifyUserStory() {

        LoginRequest request = new LoginRequest("@1", "1");
        //loginRequestSpy = Mockito.spy(new LoginRequest("@asdf", "asdf"));
        loginResponseMock = Mockito.mock(LoginResponse.class);
        try {
            loginResponseMock = serverFacadeSpy.login(request, "/login");
            verify(clientCommunicatorSpy).doPost("/login", request, null, LoginResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to Login " + ex);
        }

        Assertions.assertTrue(loginResponseMock.isSuccess());
        Assertions.assertNotNull(loginResponseMock.getAuthToken());
        Assertions.assertEquals("new", loginResponseMock.getUser().getFirstName());



        Status status = new Status("Test Post", loginResponseMock.getUser(), Instant.now().toEpochMilli(), null, null);

        mainPresenter.newStatus("Test Post");
        verify(view).showPostingToast();

        PostStatusRequest request1 = new PostStatusRequest(loginResponseMock.getAuthToken(), status);
        PostStatusResponseMock = Mockito.mock(PostStatusResponse.class);
        try {
            PostStatusResponseMock = serverFacadeSpy.postStatus(request1, "/poststatus");
            verify(clientCommunicatorSpy).doPost("/poststatus", request1, null, PostStatusResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to Login " + ex);
        }

        GetStoryRequest request2 = new GetStoryRequest(loginResponseMock.getAuthToken(), "@1", 1, null);
        //GetStoryRequestSpy = Mockito.spy(new GetStoryRequest(loginResponseMock.getAuthToken(), "@asdf", 1, null));
        GetStoryResponseMock = Mockito.mock(GetStoryResponse.class);
        try {
            GetStoryResponseMock = serverFacadeSpy.getStory(request2, "/getstory");
            verify(clientCommunicatorSpy).doPost("/getstory", request2, null, GetStoryResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to Login " + ex);
        }

        Assertions.assertTrue(GetStoryResponseMock.isSuccess());
        Assertions.assertEquals(status.getPost(), GetStoryResponseMock.getStory().get(0).getPost());
        Assertions.assertEquals(status.getUser(), GetStoryResponseMock.getStory().get(0).getUser());

    }
}
