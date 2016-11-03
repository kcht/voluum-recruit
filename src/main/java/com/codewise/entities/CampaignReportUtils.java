package com.codewise.entities;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by kchachlo on 2016-11-02.
 */
public class CampaignReportUtils
{
    public static void main(String... args){
        campaignRecordRequestUrl();
    }
    public static String  campaignRecordRequestUrl(){
        //todo: remove hack
        String reportURL = "https://portal.voluum.com/report";
        String campaignId = "c9450b92-b7d1-4131-917c-6cc237149eaf";

        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String dateFrom = localDateTime.format(dateTimeFormatter)+"Z";
        String dateTo = localDateTime.plusDays(1).format(dateTimeFormatter)+"Z";


        String url = reportURL + "?from=" + dateFrom + "&to=" + dateTo + "&groupBy=offer&filter1=campaign&filter1Value=" + campaignId;
        System.out.println(url);

        return url;

    }

    public static int getNumberOfVisitsFromResponse(String response){
        JSONObject json = new JSONObject(response);
        int visitsNumber =json.getJSONArray("rows").getJSONObject(0).getInt("visits");
        return visitsNumber;
    }

    public static int getNumberOfConversionsFromResponse(String response){
        JSONObject json = new JSONObject(response);
        int visitsNumber =json.getJSONArray("rows").getJSONObject(0).getInt("conversions");
        return visitsNumber;
    }
}
