package com.codewise.voluum;

import com.codewise.App;
import com.codewise.entities.Campaign;
import com.codewise.entities.CampaignReportUtils;
import com.codewise.exceptions.InvalidResponseCodeException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

public class VoluumClient extends RestfulClient
{
    public static String authenticate(String username, String password) throws IOException, InvalidResponseCodeException
    {
        String authString = username + ":" + password;
        String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));
        System.out.println(authStringEnc);
        System.out.println(new String(Base64.getDecoder().decode(authStringEnc)));

        Map<String, String> headers = VoluumRequestUtils.addBasicHeaders();
        headers.put("Authorization", "Basic " + authStringEnc);
        HttpURLConnection httpConnection = prepareRequest(App.authServiceUrl, RESTMethod.GET, headers, null);

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

    private static String getReportJSONForCampaign(String campaignId, String cwauthToken) throws IOException
    {
        String url = CampaignReportUtils.campaignRecordRequestUrl(campaignId);

        HttpURLConnection httpConnection = prepareRequest(
            url,
            RESTMethod.GET,
            VoluumRequestUtils.addBasicHeadersWithToken(cwauthToken),
            null);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        String responseJSON = getResponseFromConnection(httpConnection);
        httpConnection.disconnect();
        return responseJSON;
    }

    public static void performPostback(String locationRandomId, String cwauthToken) throws IOException
    {
        String postbackAddress = App.postBackUrlPrefix + locationRandomId;

        System.out.println(postbackAddress);
        HttpURLConnection httpConnection = prepareRequest(
            postbackAddress,
            RESTMethod.GET,
            VoluumRequestUtils.addBasicHeadersWithToken(cwauthToken),
            null);

        validateResponseCode(httpConnection, HttpURLConnection.HTTP_OK);
        httpConnection.disconnect();
    }

    public static String visitCampaignURL(String campaignURL, boolean redirectMode) throws IOException, URISyntaxException
    {
        HttpURLConnection httpConnection = prepareRequest(
            campaignURL,
            RESTMethod.POST,
            VoluumRequestUtils.addBasicHeaders(),
            null);
        // despite specification when performing request for GET it gives 404 not found.

        httpConnection.setInstanceFollowRedirects(redirectMode);
        if (redirectMode)
        {
            validateResponseCode(httpConnection, HttpURLConnection.HTTP_NOT_FOUND);
        }
        else
        {
            validateResponseCode(httpConnection, HttpURLConnection.HTTP_MOVED_TEMP);
        }

        String location = httpConnection.getHeaderField("Location");
        httpConnection.disconnect();
        return location;
    }

    public static Campaign getCampaign(String cwauthToken, String campaignId) throws IOException
    {
        String campaignGetUrl = App.coreServiceUrl + "/campaigns/" + campaignId;
        HttpURLConnection httpConnection = prepareRequest(
            campaignGetUrl,
            RESTMethod.GET,
            VoluumRequestUtils.addBasicHeadersWithToken(cwauthToken),
            null);

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
        String requestPayload = VoluumRequestUtils.getPayloadForCreateCampaignWithRandomName();

        String requestURL = App.coreServiceUrl + "/campaigns";

        HttpURLConnection httpConnection = prepareRequest(
            requestURL,
            RESTMethod.POST,
            VoluumRequestUtils.addBasicHeadersWithToken(cwauthToken),
            requestPayload);

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
