package edu.byu.cs240.familymapclient;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;

import static org.junit.Assert.*;

public class ServerProxyTest {

    private static final String TAG = "ServerProxyTest";
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PORT = "8080";

    private static ServerProxy serverProxy;

    @Before
    public void setup() {
        serverProxy = new ServerProxy(SERVER_HOST, SERVER_PORT);
        serverProxy.clearDatabase();
        serverProxy.loadDatabase();
    }

    @Test
    public void loginSuccess() {
        LoginRequest loginRequest;
        loginRequest = new LoginRequest();
        loginRequest.setPassword("parker");
        loginRequest.setUsername("sheila");

        LoginResult result = serverProxy.Login(loginRequest);
        assertNotNull(result.getPersonID());
        assertTrue(result.isSuccess());
    }

    @Test
    public void loginFailure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("blah");
        request.setPassword("blah");
        LoginResult result = serverProxy.Login(request);
        assertFalse(result.isSuccess());
        assertNull(result.getPersonID());
    }

    @Test
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("spencerbrosnahan@gmail.com");
        request.setPassword("cheese");
        request.setUsername("SpencerB");
        request.setFirstname("Spencer");
        request.setLastname("Brosnahan");
        request.setGender("m");

        RegisterResult result = serverProxy.Register(request);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getAuthtoken());
    }

    @Test
    public void registerFailure() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("spencerbrosnahan@gmail.com");
        request.setPassword("cheese");
        request.setUsername("sheila");
        request.setFirstname("Spencer");
        request.setLastname("Brosnahan");
        request.setGender("m");

        RegisterResult result = serverProxy.Register(request);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getAuthtoken());
    }

    @Test
    public void getPeopleSuccess() {
        LoginRequest loginRequest;
        loginRequest = new LoginRequest();
        loginRequest.setPassword("parker");
        loginRequest.setUsername("sheila");

        LoginResult result = serverProxy.Login(loginRequest);

        String authToken = result.getAuthToken();

        AllPersonResult allPersonResult = serverProxy.getPeople(authToken);

        assertNotNull(allPersonResult);
        assertTrue(allPersonResult.isSuccess());
        assertTrue(allPersonResult.getData().size() > 0);
    }

    @Test
    public void getPeopleFailure() {

        String authToken = "notAnAuthToken";

        AllPersonResult allPersonResult = serverProxy.getPeople(authToken);

        assertNotNull(allPersonResult);
        assertFalse(allPersonResult.isSuccess());
        assertNull(allPersonResult.getData());
    }

    @Test
    public void getEventsSuccess() {
        LoginRequest loginRequest;
        loginRequest = new LoginRequest();
        loginRequest.setPassword("parker");
        loginRequest.setUsername("sheila");

        LoginResult result = serverProxy.Login(loginRequest);

        String authToken = result.getAuthToken();

        AllEventResult allEventResult = serverProxy.getEvents(authToken);

        assertNotNull(allEventResult);
        assertTrue(allEventResult.isSuccess());
        assertTrue(allEventResult.getData().size() > 0);
    }

    @Test
    public void getEventsFailure() {
        String authToken = "notAnAuthToken";

        AllEventResult allEventResult = serverProxy.getEvents(authToken);

        assertNotNull(allEventResult);
        assertFalse(allEventResult.isSuccess());
        assertNull(allEventResult.getData());
    }
}
