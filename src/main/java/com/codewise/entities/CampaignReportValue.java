package com.codewise.entities;

/**
 * Created by kchachlo on 2016-11-04.
 */
public enum CampaignReportValue
{
    VISITS("visits"), CONVERSIONS("conversions");

    String keyName;

    CampaignReportValue(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }

}
