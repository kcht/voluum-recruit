package com.codewise.voluum;

import org.json.JSONObject;

/**
 * Created by kchachlo on 2016-11-04.
 */
public class JSONUtils
{
    public static String getPropertyFromJSON(String payload, String property){
        JSONObject jsonObject = new JSONObject(payload);
        return jsonObject.getString(property);
    }
}
