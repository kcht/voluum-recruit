package com.codewise.voluum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class JavaNetURLRESTFulClient {

    private static final String targetURL = "http://security.voluum.com/login";
    private static final String username = "sdit.recruit1@codewise.com";
    private static final String password = "";


    public static void main(String[] args) {

        try {

            URL restServiceURL = new URL(targetURL);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            String authString = username + ":" + password;
            String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));
            httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);



            if (httpConnection.getResponseCode() != 200)
            {



                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                    + httpConnection.getResponseCode());


            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                (httpConnection.getInputStream())));

            String output;
            System.out.println("Output from Server:  \n");

            while ((output = responseBuffer.readLine()) != null)
            {
                System.out.println(output);
            }

            httpConnection.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}