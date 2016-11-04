package com.codewise.voluum;

import org.json.JSONObject;

public class JSONUtils
{
    public static String getPropertyFromJSON(String payload, String property){
        JSONObject jsonObject = new JSONObject(payload);
        return jsonObject.getString(property);
    }
}
