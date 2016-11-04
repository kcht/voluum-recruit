package com.codewise.voluum;

import com.codewise.exceptions.InvalidResponseCodeException;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Map;

public class RestfulClient
{
    private final static Logger logger = Logger.getLogger(RestfulClient.class.getName());

    protected static HttpURLConnection setRequestHeaders(HttpURLConnection httpConnection, Map<String, String> headers){
        for(Map.Entry<String, String> header: headers.entrySet())
        {
            httpConnection.setRequestProperty(header.getKey(),  header.getValue());
        }
        return httpConnection;
    }

    protected static HttpURLConnection prepareRequest(String url, RESTMethod method, Map<String, String> headers, String payload)
        throws IOException
    {
        URL restServiceURL = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod(method.toString());
        setRequestHeaders(httpConnection, headers);

        if(payload != null){
            httpConnection.setDoOutput(true);
            OutputStream os = httpConnection.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
            pw.write(payload);
            pw.close();
        }
        return httpConnection;
    }

    public static String getResponseFromConnection(HttpURLConnection httpConnection) throws IOException {
        BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String output;

        while ((output = responseBuffer.readLine()) != null)
        {
            System.out.println(output);
            sb.append(output);
        }
        return sb.toString();
    }

    public static void validateResponseCode(HttpURLConnection httpConnection, int expectedResponseCode)
        throws IOException, InvalidResponseCodeException
    {
        int actualResponseCode = httpConnection.getResponseCode();
        if(expectedResponseCode != actualResponseCode){
            throw new InvalidResponseCodeException(
                actualResponseCode,
                actualResponseCode + ": " + httpConnection.getResponseMessage()
            );
        }
        else{
            logger.info("Got expected response code: " + expectedResponseCode);
        }
    }

}