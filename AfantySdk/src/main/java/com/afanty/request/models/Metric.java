package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Metric {
    public String type;
    public float value;
    public String vendor;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("type",type);
            jsob.put("value",value);
            jsob.put("pvendoros",vendor);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
