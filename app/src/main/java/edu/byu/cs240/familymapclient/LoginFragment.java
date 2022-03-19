package edu.byu.cs240.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private Button loginButton;
    private Button registerButton;

    private LoginViewModel getLoginViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoginViewModel().Login();
                if(getLoginViewModel().isSuccess()){
                    //TODO: SEND A CALLBACK TO MAIN TO SWITCH VIEWS
                } else {
                    //TODO: Display a login failed toast
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoginViewModel().Register();
                if(getLoginViewModel().isSuccess()){
                    //TODO: SEND A CALLBACK TO MAIN TO SWITCH VIEWS
                } else {
                    //TODO: Display a login failed toast
                }
            }
        });

        return view;
    }
}

