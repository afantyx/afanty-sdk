package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Format {
    public Integer w;
    public Integer h;
    public Integer wratio;
    public Integer hratio;
    public Integer wmin;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("w",w);
            jsob.put("h",h);
            jsob.put("wratio",wratio);
            jsob.put("hratio",hratio);
            jsob.put("wmin",wmin);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
