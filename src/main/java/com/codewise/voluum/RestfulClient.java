package com.codewise.voluum;

import com.codewise.entities.Campaign;
import com.codewise.entities.CampaignReportUtils;
import com.codewise.exceptions.InvalidResponseCodeException;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.json.HTTP;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Base64;

public class RestfulClient
{
    private final static Logger logger = Logger.getLogger(RestfulClient.class.getName());


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
        System.out.println(authStringEnc);
        System.out.println(new String(Base64.getDecoder().decode(authStringEnc)));
        httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();

        return getPropertyFromJSON(responseJSON, "token");

    }

    public static int getNumberOfVisits(String campaignId, String cwauthToken) throws IOException
    {
        String responseJSON = getReportJSONForCampaign(campaignId, cwauthToken);
        return CampaignReportUtils.getNumberOfVisitsFromResponse(responseJSON);
    }

    public static int getNumberOfConversions(String campaignId, String cwauthToken) throws IOException
    {
        String responseJSON = getReportJSONForCampaign(campaignId, cwauthToken);
        return CampaignReportUtils.getNumberOfConversionsFromResponse(responseJSON);
    }

    private static String getReportJSONForCampaign(String campaignId, String cwauthToken) throws  IOException{
        String url = CampaignReportUtils.campaignRecordRequestUrl(campaignId);
        URL restServiceURL = new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("cwauth-token", cwauthToken);
        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();
        return responseJSON;
    }


    public static void main(String... s) throws Exception
    {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.loadProperties();
        System.out.println(applicationProperties.getUsername());

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

    public static void performPostback(String locationRandomId, String cwauthToken) throws IOException
    {
        String postbackAddress = "http://auayi.voluumtrk.com/postback?cid=" +locationRandomId;

        System.out.println(postbackAddress);
        URL restServiceURL = new URL(postbackAddress);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("cwauth-token", cwauthToken);


            validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);

        httpConnection.disconnect();
    }
    public static String visitCampaignURL(String campaignURL, boolean redirectMode) throws IOException, URISyntaxException
    {

        URL restServiceURL = new URL(campaignURL);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("POST"); //despite specification when performing request for GET it gives 404 not found.
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        httpConnection.setInstanceFollowRedirects(redirectMode);
        if(redirectMode){
            validateResponseCode(httpConnection, HttpURLConnection.HTTP_NOT_FOUND);
        }
        else{
            validateResponseCode(httpConnection, HttpURLConnection.HTTP_MOVED_TEMP);
        }

        String location = httpConnection.getHeaderField("Location");

        httpConnection.disconnect();
        return location;
    }

    public static Campaign getCampaign(String cwauthToken, String campaignId) throws IOException
    {
        String host = "https://core.voluum.com/campaigns/" + campaignId;
        URL restServiceURL = new URL(host);

        HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("cwauth-token", cwauthToken);
        httpConnection.setDoOutput(true);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
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
    public static Campaign createCampaign(String cwauthToken) throws IOException, InvalidResponseCodeException
    {
        //todo move payload to request Utils, url as parameter
        //campaign name

        String campaignName = VoluumRequestUtils.generateRandomName(10);
        String requestPayload = "{"
            + "\"namePostfix\":" + campaignName + ","
            + "\"costModel\":\"NOT_TRACKED\","
            + "\"clickRedirectType\":\"REGULAR\","
            + "\"trafficSource\":{\"id\":\"afea734c-8a4a-4f04-bfe6-2e720c1ccb86\"},"
            + "\"redirectTarget\":\"DIRECT_URL\","
            + "\"client\":"
                + "{\"id\":\"4093bd73-c37c-4717-8a18-539d653dc4f2\","
                + "\"clientCode\":\"auayi\","
                + "\"mainDomain\":\"auayi.voluumtrk.com\","
                + "\"defaultDomain\":\"voluumtrk.com\","
                + "\"customParam1Available\":false,"
                + "\"realtimeRoutingAPI\":false,"
                + "\"rootRedirect\":false},"
                + "\"costModelHidden\":true,"
                + "\"directRedirectUrl\":\"http://example.com/{clickid}\"}";
        String host = "https://core.voluum.com/campaigns";

        System.out.println(requestPayload);

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