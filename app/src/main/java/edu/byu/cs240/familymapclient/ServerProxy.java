package edu.byu.cs240.familymapclient;

import com.google.gson.Gson;

import request.LoadRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;
import result.Result;

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
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

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
            RegisterResult result = gson.fromJson(respData, RegisterResult.class);

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
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

    public Result loadDatabase() {
        try {

            String jsonFilePath = "/Users/spencerbrosnahan/240_projects/Family Map Client/passoffFiles/LoadData.json";

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/load");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            //TODO: get json object from file
            File file = new File(jsonFilePath);

            LoadRequest request;

            try(FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                request = gson.fromJson(bufferedReader, LoadRequest.class);
            }


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
            RegisterResult result = gson.fromJson(respData, RegisterResult.class);

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    public Result clearDatabase() {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(false);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            InputStream respBody;

            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                respBody = http.getInputStream();
            } else {
                respBody = http.getErrorStream();
            }
            String respData = readString(respBody);
            Result result = gson.fromJson(respData, Result.class);

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
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
