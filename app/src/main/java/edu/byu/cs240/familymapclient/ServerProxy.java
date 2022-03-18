package edu.byu.cs240.familymapclient;

import com.google.gson.Gson;

import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;
import java.io.*;
import java.net.*;

public class ServerProxy {
    String serverHost;
    String serverPort;
    Gson gson = new Gson();

    ServerProxy(String serverHost, String serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public LoginResult Login(LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            String json = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();

            writeString(json, reqBody);

            reqBody.close();

            InputStream respBody;

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                respBody = http.getInputStream();
            } else {
                respBody = http.getErrorStream();
            }
            String respData = readString(respBody);
            LoginResult result = gson.fromJson(respData, LoginResult.class);

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    public RegisterResult Register(RegisterRequest request) {
        return null;
    }

    public AllPersonResult getPeople(String authtoken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authtoken);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            InputStream respBody;

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                respBody = http.getInputStream();
            } else {
                respBody = http.getErrorStream();
            }
            String respData = readString(respBody);
            AllPersonResult result = gson.fromJson(respData, AllPersonResult.class);
            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public AllEventResult getEvents(String authtoken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authtoken);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            InputStream respBody;

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                respBody = http.getInputStream();
            } else {
                respBody = http.getErrorStream();
            }
            String respData = readString(respBody);
            AllEventResult result = gson.fromJson(respData, AllEventResult.class);

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void OrganizeData() {

    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
