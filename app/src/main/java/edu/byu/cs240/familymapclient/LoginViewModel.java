package edu.byu.cs240.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import request.LoginRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private static final String SERVER_HOST = "10.0.2.2";
    private static final String SERVER_PORT = "8080";
    private static final String MESSAGE_KEY = "message";
    private static final String SECOND_MESSAGE_KEY = "secondMessage";
    private static final String SUCCESS_KEY = "success";
    private static final String SECOND_SUCCESS_KEY = "secondSuccess";

    private static ServerProxy serverProxy = new ServerProxy(SERVER_HOST, SERVER_PORT);

    private boolean loginSuccess;

    private void getData(String authtoken){

        Handler getDataThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean firstIsSuccess = bundle.getBoolean(SUCCESS_KEY);
                boolean secondIsSuccess = bundle.getBoolean(SECOND_SUCCESS_KEY);
                String firstMessage = bundle.getString(MESSAGE_KEY);
                String secondMessage = bundle.getString(SECOND_MESSAGE_KEY);

                if(firstIsSuccess && !secondIsSuccess){
                    loginSuccess = false;
                    Log.d(TAG, "Error getting events. Message: " + secondMessage);
                } else if (!firstIsSuccess && secondIsSuccess) {
                    loginSuccess = false;
                    Log.d(TAG, "Error getting people. Message: " + firstMessage);
                } else if (!firstIsSuccess && !secondIsSuccess) {
                    loginSuccess = false;
                    Log.d(TAG, "Error getting both. Messages: \n " + firstMessage + "\n" + secondMessage);
                } else {
                    loginSuccess = true;
                    Log.d(TAG, "Success Loading Data!");
                }
            }
        };

        GetDataTask task = new GetDataTask(getDataThreadMessageHandler, authtoken);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    public void Login() {
        Handler loginThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean isSuccess = bundle.getBoolean(SUCCESS_KEY);
                String success_message = bundle.getString(MESSAGE_KEY);
                Log.d(TAG, "message: " + success_message);

                if(isSuccess) {
                    String authtoken = DataCache.getInstance().getAuthtoken();
                    Log.d(TAG, "authtoken:" + authtoken);
                    Log.d(TAG, "username: " + DataCache.getInstance().getUsername());
                    Log.d(TAG, "PersonID: " + DataCache.getInstance().getPersonID());
                    getData(authtoken);
                } else {
                    loginSuccess = false;
                    Log.d(TAG, "Login failed");
                }
            }
        };

        //TODO: I NEED TO PASS IN THE ACTUAL REQUEST HERE AS WELL
        LoginRequest request = new LoginRequest();
        request.setUsername("jb");
        request.setPassword("hello");

        LoginTask task = new LoginTask(loginThreadMessageHandler, request);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);

    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        LoginRequest request;

        public LoginTask(Handler messageHandler, LoginRequest request){
            this.request = request;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            LoginResult result = serverProxy.Login(request);
            DataCache.getInstance().setAuthtoken(result.getAuthToken());
            DataCache.getInstance().setPersonID(result.getPersonID());
            DataCache.getInstance().setUsername(result.getUsername());
            sendMessage(result);
        }

        private void sendMessage(LoginResult result) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE_KEY, result.getMessage());
            messageBundle.putBoolean(SUCCESS_KEY, result.isSuccess());
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private static class GetDataTask implements Runnable {
        private final Handler messageHandler;
        private String authtoken;

        public  GetDataTask(Handler messageHandler, String authtoken) {
            this.messageHandler = messageHandler;
            this.authtoken = authtoken;
        }

        @Override
        public void run() {
            AllPersonResult personResult = serverProxy.getPeople(authtoken);
            AllEventResult eventResult = serverProxy.getEvents(authtoken);
            if(personResult.isSuccess() && eventResult.isSuccess()){
                DataCache.getInstance().setPersonList(personResult.getData());
                DataCache.getInstance().setEventList(eventResult.getData());
                serverProxy.OrganizeData();
            }
            sendMessage(personResult, eventResult);
        }

        private void sendMessage(AllPersonResult personResult, AllEventResult eventResult) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE_KEY, personResult.getMessage());
            messageBundle.putBoolean(SUCCESS_KEY, personResult.isSuccess());
            messageBundle.putString(SECOND_MESSAGE_KEY, eventResult.getMessage());
            messageBundle.putBoolean(SECOND_SUCCESS_KEY, eventResult.isSuccess());
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }

    }
}
