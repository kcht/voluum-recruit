package com.codewise.entities;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codewise.entities.CampaignReportValue.CONVERSIONS;
import static com.codewise.entities.CampaignReportValue.VISITS;


public class CampaignReportUtils
{

    public static String  campaignRecordRequestUrl(String campaignId){
        //todo: remove hack
        String reportURL = "https://portal.voluum.com/report";

        LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String dateFrom = localDateTime.withDayOfMonth(3).withMonth(11).format(dateTimeFormatter)+"Z";
        String dateTo = localDateTime.plusDays(1).format(dateTimeFormatter)+"Z";


        String url = reportURL + "?from=" + dateFrom + "&to=" + dateTo + "&groupBy=offer&filter1=campaign&filter1Value=" + campaignId;
        System.out.println(url);

        return url;

    }

    public static int getNumberOfVisitsFromResponse(String response){
        return getIntValueFromCampaignReportJSON(VISITS, response);
    }

    public static int getNumberOfConversionsFromResponse(String response){
        return getIntValueFromCampaignReportJSON(CONVERSIONS, response);
    }

    public static int getIntValueFromCampaignReportJSON(CampaignReportValue campaignReportValue, String response){
        JSONObject json = new JSONObject(response);
        int valueForKey = json.getJSONArray("rows").getJSONObject(0).getInt(campaignReportValue.getKeyName());
        return valueForKey;
    }
}
