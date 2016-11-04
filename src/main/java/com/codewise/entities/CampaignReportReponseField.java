package com.codewise.entities;

public enum CampaignReportReponseField
{
    VISITS("visits"), CONVERSIONS("conversions");

    String keyName;

    CampaignReportReponseField(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }

}
