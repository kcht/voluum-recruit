package com.codewise.voluum;

import com.codewise.entities.Campaign;
import com.codewise.entities.CampaignReportUtils;
import com.codewise.exceptions.InvalidResponseCodeException;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RestfulClient
{
    private final static Logger logger = Logger.getLogger(RestfulClient.class.getName());

    private static final String authenticationServiceURL = "http://security.voluum.com/login";

    private static HttpURLConnection setRequestHeaders(HttpURLConnection httpConnection, Map<String, String> headers){
        for(Map.Entry<String, String> header: headers.entrySet())
        {
            httpConnection.setRequestProperty(header.getKey(),  header.getValue());
        }
        return httpConnection;
    }

    private static HttpURLConnection prepareRequest(String url, RESTMethod method, Map<String, String> headers, String payload) throws IOException
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

    private static Map<String, String> addBasicHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private static Map<String, String> addBasicHeadersWithToken(String token){
        Map<String, String> headers = addBasicHeaders();
        headers.put("cwauth-token", token);
        return headers;
    }

    public static String authenticate(String username, String password) throws IOException, InvalidResponseCodeException
    {

        String authString = username + ":" + password;
        String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));
        System.out.println(authStringEnc);
        System.out.println(new String(Base64.getDecoder().decode(authStringEnc)));

        Map<String, String> headers = addBasicHeaders();
        headers.put("Authorization", "Basic " + authStringEnc);
        HttpURLConnection httpConnection = prepareRequest(AppProperties.AUTH_SERVICE_URL, RESTMethod.GET, headers, null);


        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();

        return JSONUtils.getPropertyFromJSON(responseJSON, "token");

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

        HttpURLConnection httpConnection = prepareRequest(url, RESTMethod.GET, addBasicHeadersWithToken(cwauthToken), null);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();
        return responseJSON;
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
        String postbackAddress = AppProperties.POSTBACK_URL_PREFIX +locationRandomId;

        System.out.println(postbackAddress);
        HttpURLConnection httpConnection = prepareRequest(postbackAddress, RESTMethod.GET, addBasicHeadersWithToken(cwauthToken), null);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        httpConnection.disconnect();
    }
    public static String visitCampaignURL(String campaignURL, boolean redirectMode) throws IOException, URISyntaxException
    {
        HttpURLConnection httpConnection = prepareRequest(campaignURL, RESTMethod.POST, addBasicHeaders(), null);
        //despite specification when performing request for GET it gives 404 not found.

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
        String url = AppProperties.CORE_SERVICE_URL +"/campaigns/" + campaignId;
        HttpURLConnection httpConnection = prepareRequest(url, RESTMethod.GET, addBasicHeadersWithToken(cwauthToken), null);

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

        String requestURL = AppProperties.CORE_SERVICE_URL + "/campaigns";

        HttpURLConnection httpConnection = prepareRequest(requestURL, RESTMethod.POST, addBasicHeadersWithToken(cwauthToken), requestPayload);

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