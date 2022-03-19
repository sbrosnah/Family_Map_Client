package edu.byu.cs240.familymapclient;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;
import result.Result;

public class ServerProxyTest {
    private LoginViewModel getLoginViewModel() {
        LoginFragment fragment = new LoginFragment();
        return ViewModelProviders.of(fragment).get(LoginViewModel.class);
    }

    @Test
    public void LoginSuccess() {
        //getLoginViewModel().clearDatabase();
        getLoginViewModel().Login();
        assertNotNull(DataCache.getInstance().getAuthtoken());
    }
}
