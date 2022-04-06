package com.hanghae99.boilerplate.signupLogin.kakao.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.ServerError;

@Component
@Slf4j
public class Connection {

    @Autowired
    ObjectMapper objectMapper;

    public KakaoUserData getaccessToken( String code) throws IOException {

        HttpURLConnection connection =getConnection("https://kauth.kakao.com/oauth/token");

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("grant_type=authorization_code");
        stringBuilder.append("&client_id=91ee90dad2384a8f06ab7106b2f92daf");
        stringBuilder.append("&redirect_uri=https://www.boiler-plate.org/api/kakao/login");
        stringBuilder.append("&code=" + code);

        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();

        int status = connection.getResponseCode();
        if(status != 200) {
            throw new ConnectException("status : "+status);
        }


        KakaoUserData user = objectMapper.readValue(getResult(connection), KakaoUserData.class);
        bufferedWriter.close();


        return user;
    }

    public TemporaryUser getUserData(String token) throws IOException {
        HttpURLConnection connection = getConnection("https://kapi.kakao.com/v2/user/me");

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization","Bearer "+ token);

        if (connection.getResponseCode() != 200) {
            throw new ConnectException("status : "+connection.getResponseCode());
        }

        JsonNode jsonNode = readConnectionStringToJson(connection);

        String nickname = jsonNode.get("properties").get("nickname").textValue();
        String profileImageUrl = jsonNode.get("properties").get("profile_image").textValue();
        String email = jsonNode.get("kakao_account").get("email").textValue();

        TemporaryUser temporaryUser = new TemporaryUser(email, nickname, profileImageUrl);
        log.info("success get kakao user infromation email:{}", temporaryUser.getEmail());
        return temporaryUser;

    }

    public HttpURLConnection getConnection(String requestURL) throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }
    public JsonNode readConnectionStringToJson(HttpURLConnection connection) throws IOException {
        String result = getResult(connection);
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(result);
        JsonNode jsonNode = objectMapper.readTree(result);
        return jsonNode;
    }

    public String getResult(HttpURLConnection connection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        bufferedReader.close();
        return result;
    }
}