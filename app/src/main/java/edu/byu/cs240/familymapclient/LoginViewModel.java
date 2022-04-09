package edu.byu.cs240.familymapclient;

import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private String username = "";
    private String password = "";
    private String serverHost = "";
    private String serverPort = "";
    private String firstname = "";
    private String lastname = "";
    private String email = "";
    private String gender = "";


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean containsRequiredForLogin() {
        if ((serverHost != null && !serverHost.equals("")) &&
                (serverPort != null && !serverPort.equals("")) &&
                (username != null && !username.equals("")) &&
                (password != null && !password.equals(""))){
            return true;
        } else {
            return false;
        }
    }

    public boolean containsRequiredForRegister() {
        if((serverHost == null || !serverHost.equals("")) && (serverPort == null || !serverPort.equals("")) && (username == null || !username.equals(""))
        && (password == null || !password.equals("")) && (firstname == null || !firstname.equals("")) && (lastname == null || !lastname.equals(""))
        && (email == null || !email.equals("")) && (gender == null || !gender.equals(""))) {
            return true;
        } else {
            return false;
        }
    }
}
