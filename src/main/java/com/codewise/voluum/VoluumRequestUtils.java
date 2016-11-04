package com.codewise.voluum;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VoluumRequestUtils
{
    private static String generateRandomName(int length){
        String candidateChars = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
        }

        return sb.toString();
    }

    public static Map<String, String> addBasicHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static Map<String, String> addBasicHeadersWithToken(String token){
        Map<String, String> headers = addBasicHeaders();
        headers.put("cwauth-token", token);
        return headers;
    }

    public static String getPayloadForCreateCampaignWithRandomName(){
        String campaignName = VoluumRequestUtils.generateRandomName(10);
        return "{"
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
    }


}
