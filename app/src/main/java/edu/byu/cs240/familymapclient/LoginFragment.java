package edu.byu.cs240.familymapclient;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.os.Message;
import android.support.v4.os.IResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;
import result.Result;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final String SERVER_HOST = "10.0.2.2";
    private static final String SERVER_PORT = "8080";
    private static final String MESSAGE_KEY = "message";
    private static final String SECOND_MESSAGE_KEY = "secondMessage";
    private static final String SUCCESS_KEY = "success";
    private static final String SECOND_SUCCESS_KEY = "secondSuccess";

    private static final String DEFAULT_SERVER_HOST = "10.0.2.2";
    private static final String DEFAULT_SERVER_PORT = "8080";

    private static final int ENABLED_BACKGROUND = Color.rgb(1, 148, 87);
    private static final int DISABLED_BACKGROUND = Color.GRAY;

    private static ServerProxy serverProxy;

    private Button loginButton;
    private Button registerButton;
    private Button clearButton;
    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText emailEditText;
    private RadioButton maleButton;
    private RadioButton femaleButton;


    private LoginViewModel getLoginViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    private String checkCharSeq(CharSequence charSequence){
        String string = charSequence.toString();
        if(string.equals("")){
            string = null;
        }
        return string;
    }

    private void makeToast(String string) {
        Toast.makeText(getContext().getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    private void switchViews(){
        Bundle bundle = new Bundle();
        bundle.putBoolean(DataCache.IS_EVENT_KEY, false);

        Fragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateButtons() {
        boolean containsLogin = getLoginViewModel().containsRequiredForLogin();
        boolean containsRegister = getLoginViewModel().containsRequiredForRegister();
        loginButton.setEnabled(containsLogin);
        registerButton.setEnabled(containsRegister);
        updateButtonColors(containsLogin, containsRegister);
    }

    private void updateButtonColors(boolean containsLogin, boolean containsRegister) {
        if(containsLogin) {
            loginButton.setBackgroundColor(ENABLED_BACKGROUND);
        } else {

            loginButton.setBackgroundColor(DISABLED_BACKGROUND);
        }

        if(containsRegister) {
            registerButton.setBackgroundColor(ENABLED_BACKGROUND);
        } else {

            registerButton.setBackgroundColor(DISABLED_BACKGROUND);
        }
    }

    private void setDefaultValues() {
        serverHostEditText.setText(DEFAULT_SERVER_HOST);
        serverPortEditText.setText(DEFAULT_SERVER_PORT);
        getLoginViewModel().setServerHost(DEFAULT_SERVER_HOST);
        getLoginViewModel().setServerPort(DEFAULT_SERVER_PORT);

        //TODO: Get rid of this later
        String username = "sheila";
        String password = "parker";
        usernameEditText.setText(username);
        getLoginViewModel().setUsername(username);
        passwordEditText.setText(password);
        getLoginViewModel().setPassword(password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        clearButton = view.findViewById(R.id.clear_button);
        usernameEditText = view.findViewById(R.id.username);
        serverHostEditText = view.findViewById(R.id.serverHost);
        serverPortEditText = view.findViewById(R.id.serverPort);
        passwordEditText = view.findViewById(R.id.password);
        firstnameEditText = view.findViewById(R.id.firstname);
        lastnameEditText = view.findViewById(R.id.lastname);
        emailEditText = view.findViewById(R.id.email);
        maleButton = view.findViewById(R.id.maleButton);
        femaleButton = view.findViewById(R.id.femaleButton);



        setDefaultValues();

        updateButtons();
        clearButton.setBackgroundColor(ENABLED_BACKGROUND);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setUsername(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setPassword(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        serverHostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setServerHost(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        serverPortEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setServerPort(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        firstnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setFirstname(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        lastnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setLastname(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = checkCharSeq(charSequence);
                getLoginViewModel().setEmail(string);
                updateButtons();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getLoginViewModel().setGender("m");
                updateButtons();
            }

        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getLoginViewModel().setGender("f");
                updateButtons();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginRequest request = new LoginRequest();
                request.setUsername(getLoginViewModel().getUsername());
                request.setPassword(getLoginViewModel().getPassword());

                Login(request);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RegisterRequest request = new RegisterRequest();
                request.setUsername(getLoginViewModel().getUsername());
                request.setPassword(getLoginViewModel().getPassword());
                request.setEmail(getLoginViewModel().getEmail());
                request.setFirstname(getLoginViewModel().getFirstname());
                request.setLastname(getLoginViewModel().getLastname());
                request.setGender(getLoginViewModel().getGender());

                Register(request);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                clearDatabase();
            }
        });



        return view;
    }

    private void getData(String authtoken){

        Handler getDataThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                boolean success = false;
                Bundle bundle = message.getData();
                boolean firstIsSuccess = bundle.getBoolean(SUCCESS_KEY);
                boolean secondIsSuccess = bundle.getBoolean(SECOND_SUCCESS_KEY);
                String firstMessage = bundle.getString(MESSAGE_KEY);
                String secondMessage = bundle.getString(SECOND_MESSAGE_KEY);

                if(firstIsSuccess && secondIsSuccess){
                    switchViews();
                    String firstname = DataCache.getInstance().getFirstname();
                    String lastname = DataCache.getInstance().getLastname();
                    String toast = firstname + " " + lastname;
                    makeToast(toast);
                } else {
                    makeToast("Error Loading Data");
                    Log.d(TAG, "First Message: " + firstMessage);
                    Log.d(TAG, "Second Message: " + secondMessage);
                }
            }
        };

        GetDataTask task = new GetDataTask(getDataThreadMessageHandler, authtoken);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);


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
                DataCache.getInstance().OrganizeData();
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


    private void Login(LoginRequest request){
        Handler threadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                boolean success = false;
                Bundle bundle = message.getData();
                success = bundle.getBoolean(SUCCESS_KEY);
                String success_message = bundle.getString(MESSAGE_KEY);
                Log.d(TAG, "message: " + success_message);

                if(success) {
                    String authtoken = DataCache.getInstance().getAuthtoken();
                    Log.d(TAG, "authtoken:" + authtoken);
                    Log.d(TAG, "username: " + DataCache.getInstance().getUsername());
                    Log.d(TAG, "PersonID: " + DataCache.getInstance().getPersonID());
                    getData(authtoken);
                } else {
                    Log.d(TAG, "Failure");
                    makeToast("Login Failure!");
                }
            }
        };

        DataCache.reset();

        LoginTask task = new LoginTask(threadMessageHandler, request, getLoginViewModel().getServerHost(),
                getLoginViewModel().getServerPort());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        LoginRequest request;

        public LoginTask(Handler messageHandler, LoginRequest request, String serverHost,
                         String serverPort){
            this.request = request;
            this.messageHandler = messageHandler;
            if(serverHost == null || serverHost.equals("")) {
                serverHost = SERVER_HOST;
            }
            if(serverPort == null || serverPort.equals("")){
                serverPort = SERVER_PORT;
            }
            serverProxy = new ServerProxy(serverHost, serverPort);
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

    private void Register(RegisterRequest request){
        Handler threadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                boolean success = false;
                Bundle bundle = message.getData();
                success = bundle.getBoolean(SUCCESS_KEY);
                String success_message = bundle.getString(MESSAGE_KEY);
                Log.d(TAG, "message: " + success_message);

                if(success) {
                    String authtoken = DataCache.getInstance().getAuthtoken();
                    Log.d(TAG, "authtoken:" + authtoken);
                    Log.d(TAG, "username: " + DataCache.getInstance().getUsername());
                    Log.d(TAG, "PersonID: " + DataCache.getInstance().getPersonID());
                    getData(authtoken);
                } else {
                    Log.d(TAG, "Failure");
                    makeToast("Register Failure!");
                }
            }
        };

        DataCache.reset();

        RegisterTask task = new RegisterTask(threadMessageHandler, request, getLoginViewModel().getServerHost(),
                getLoginViewModel().getServerPort());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    private static class RegisterTask implements Runnable {
        private final Handler messageHandler;
        RegisterRequest request;

        public RegisterTask(Handler messageHandler, RegisterRequest request, String serverHost,
                            String serverPort){
            this.request = request;
            this.messageHandler = messageHandler;
            if(serverHost == null) {
                serverHost = SERVER_HOST;
            }
            if(serverPort == null){
                serverPort = SERVER_PORT;
            }
            serverProxy = new ServerProxy(serverHost, serverPort);
        }

        @Override
        public void run() {
            RegisterResult result = serverProxy.Register(request);
            DataCache.getInstance().setAuthtoken(result.getAuthtoken());
            DataCache.getInstance().setPersonID(result.getPersonID());
            DataCache.getInstance().setUsername(result.getUsername());
            sendMessage(result);
        }

        private void sendMessage(RegisterResult result) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE_KEY, result.getMessage());
            messageBundle.putBoolean(SUCCESS_KEY, result.isSuccess());
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private void clearDatabase(){
        Handler clearThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                boolean success = false;
                Bundle bundle = message.getData();
                success = bundle.getBoolean(SUCCESS_KEY);
                String successMessage = bundle.getString(MESSAGE_KEY);
                if(success){
                    Log.d(TAG, "successfully cleared database");
                } else {
                    Log.d(TAG, "Failure clearing db: " + successMessage);
                }
            }
        };

        ClearDatabaseTask task = new ClearDatabaseTask(clearThreadHandler, getLoginViewModel().getServerHost(),
                getLoginViewModel().getServerPort());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    private static class ClearDatabaseTask implements Runnable {
        private final Handler messageHandler;

        public  ClearDatabaseTask(Handler messageHandler, String serverHost, String serverPort) {
            this.messageHandler = messageHandler;
            if(serverHost == null) {
                serverHost = SERVER_HOST;
            }
            if(serverPort == null){
                serverPort = SERVER_PORT;
            }
            serverProxy = new ServerProxy(serverHost, serverPort);
        }

        @Override
        public void run() {
            Result clearResult = serverProxy.clearDatabase();
            sendMessage(clearResult);
        }

        private void sendMessage(Result clearResult) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(MESSAGE_KEY, clearResult.getMessage());
            messageBundle.putBoolean(SUCCESS_KEY, clearResult.isSuccess());
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }

    }

}

