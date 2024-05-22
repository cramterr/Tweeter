package edu.byu.cs.tweeter.client.model.net;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import android.util.Log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {

    private ServerFacade serverFacadeSpy;
    private ClientCommunicator clientCommunicatorSpy;
    private RegisterRequest registerRequestSpy;
    private RegisterResponse registerResponseMock;
    private GetFollowersRequest getFollowersRequestSpy;
    private GetFollowersResponse getFollowersResponseMock;
    private GetFollowingCountRequest getFollowingCountRequestSpy;
    private GetFollowingCountResponse getFollowingCountResponseMock;


    @BeforeEach
    public void setUp() {
        serverFacadeSpy = Mockito.spy(new ServerFacade());
        clientCommunicatorSpy = Mockito.spy(new ClientCommunicator("https://z7ivyx91od.execute-api.us-west-2.amazonaws.com/Dev"));
        Mockito.when(serverFacadeSpy.getClientCommunicator()).thenReturn(clientCommunicatorSpy);
    }

    @Test
    public void testRegister_Success() {
        registerRequestSpy = Mockito.spy(new RegisterRequest("@Lord_Voldemort", "DarkLord", "Tom", "Riddle", "somestring"));
        registerResponseMock = Mockito.mock(RegisterResponse.class);
        try {
            registerResponseMock = serverFacadeSpy.register(registerRequestSpy, "/register");
            verify(clientCommunicatorSpy).doPost("/register", registerRequestSpy, null, RegisterResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to register "+ex);
        }

        Assertions.assertTrue(registerResponseMock.isSuccess());
        Assertions.assertNotNull(registerResponseMock.getAuthToken());
        Assertions.assertEquals(FakeData.getInstance().getFirstUser(), registerResponseMock.getUser());
    }

    @Test
    public void testGetFollowers_Success() {
        getFollowersRequestSpy = Mockito.spy(new GetFollowersRequest(new AuthToken(), "@John", 10, null));
        getFollowersResponseMock = Mockito.mock(GetFollowersResponse.class);
        try {
            getFollowersResponseMock = serverFacadeSpy.getFollowers(getFollowersRequestSpy, "/getfollowers");
            verify(clientCommunicatorSpy).doPost("/getfollowers", getFollowersRequestSpy, null, GetFollowersResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to get followers "+ex);
        }

        Assertions.assertTrue(getFollowersResponseMock.isSuccess());
        Assertions.assertTrue(getFollowersResponseMock.getHasMorePages());
        Assertions.assertNotNull(getFollowersResponseMock.getFollowers());
    }

    @Test
    public void testGetFollowingCount_Success() {
        getFollowingCountRequestSpy = Mockito.spy(new GetFollowingCountRequest(new AuthToken(), "@John"));
        getFollowingCountResponseMock = Mockito.mock(GetFollowingCountResponse.class);
        try {
            getFollowingCountResponseMock = serverFacadeSpy.getFollowingCount(getFollowingCountRequestSpy, "/getfollowingcount");
            verify(clientCommunicatorSpy).doPost("/getfollowingcount", getFollowingCountRequestSpy, null, GetFollowingCountResponse.class);
        } catch (IOException | TweeterRemoteException ex) {
            System.out.println("Failed to get following count "+ex);
        }

        Assertions.assertTrue(getFollowingCountResponseMock.isSuccess());
        Assertions.assertEquals(21, getFollowingCountResponseMock.getCount());
    }

}
