package com.codewise.voluum;

import com.codewise.entities.Campaign;
import com.codewise.entities.CampaignReportUtils;
import com.codewise.exceptions.InvalidResponseCodeException;
import org.apache.log4j.Logger;
import org.json.HTTP;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Base64;

public class RestfulClient
{
    static Logger logger = Logger.getLogger(RestfulClient.class);

    private static final String authenticationServiceURL = "http://security.voluum.com/login";
    private static final String username = "sdit.recruit1@codewise.com";
    private static final String password = "";


    public static void setRequestHeaders(HttpURLConnection httpConnection){
        httpConnection.setRequestProperty("Accept", "application/json");

    }

    public static String getPropertyFromJSON(String payload, String property){
        JSONObject jsonObject = new JSONObject(payload);
        return jsonObject.getString(property);
    }
    public static String authenticate(String username, String password) throws IOException, InvalidResponseCodeException
    {
        URL restServiceURL = new URL(authenticationServiceURL);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("GET");
        setRequestHeaders(httpConnection);
        String authString = username + ":" + password;
        String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));
        httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();

        return getPropertyFromJSON(responseJSON, "token");

    }

    public static int getNumberOfVisits(String campaignId, String cwauthToken) throws IOException
    {
        String url = CampaignReportUtils.campaignRecordRequestUrl();
        URL restServiceURL = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("cwauth-token", cwauthToken);
        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();

        return
            CampaignReportUtils.getNumberOfVisitsFromResponse(responseJSON);

    }


    public static void main(String... s) throws Exception
    {

        // createCampaign();
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

    public static void validateResponseCode(HttpURLConnection httpConnection, int expectedResponseCode) throws IOException, InvalidResponseCodeException{
        int actualResponseCode = httpConnection.getResponseCode();
        if(expectedResponseCode != actualResponseCode){
            throw new InvalidResponseCodeException(actualResponseCode, actualResponseCode + ": " + httpConnection.getResponseMessage());
        }
        else{
            logger.info("Got expected response code: " + expectedResponseCode);
        }
    }

    public static String visitCampaignURL(String campaignURL) throws IOException, URISyntaxException
    {

        String encode = "http://rskxz.voluumtrk.com/voluum/" + URLEncoder.encode("6d2a1cfc-3381-4f71-ba5b-73a2da713fecss", "UTF-8");
        System.out.println(encode);
        URL restServiceURL = new URL(encode);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_MOVED_TEMP);

        String location = httpConnection.getHeaderField("Location");
        System.out.println(httpConnection.getResponseCode());

        String response =  getResponseFromConnection(httpConnection);

        JSONObject jsonObject = new JSONObject(response);

        httpConnection.disconnect();
        return location;
    }

    public static Campaign createCampaign(String cwauthToken) throws IOException, InvalidResponseCodeException
    {
        //todo move payload to request Utils, url as parameter
        //campaign name


        String requestPayload = "{\"namePostfix\":\"Campaign1113\",\"costModel\":\"NOT_TRACKED\",\"clickRedirectType\":\"REGULAR\",\"trafficSource\":{\"id\":\"6051135c-a890-4618-9efc-ab6dade95960\"},\"redirectTarget\":\"DIRECT_URL\",\"client\":{\"id\":\"8045a943-b4bb-4af9-b63b-674e7e758f47\",\"clientCode\":\"rskxz\",\"mainDomain\":\"rskxz.voluumtrk.com\",\"defaultDomain\":\"voluumtrk.com\",\"customParam1Available\":false,\"realtimeRoutingAPI\":false,\"rootRedirect\":false},\"costModelHidden\":true,\"directRedirectUrl\":\"http://example.com/{clickid}\"}";
        String host = "https://core.voluum.com/campaigns";

        URL restServiceURL = new URL(host);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("cwauth-token", cwauthToken);
        httpConnection.setDoOutput(true);

        OutputStream os = httpConnection.getOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        pw.write(requestPayload);
        pw.close();

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_CREATED);
        String responsePayload = getResponseFromConnection(httpConnection);

        httpConnection.disconnect();

        JSONObject jsonObject = new JSONObject(responsePayload);
        Campaign campaign = new Campaign()
            .withId(jsonObject.getString("id"))
            .withName(jsonObject.getString("name"))
            .withDirectRedirectUrl(jsonObject.getString("directRedirectUrl"))
            .withUrl(jsonObject.getString("url"));

        return campaign;
    }


}