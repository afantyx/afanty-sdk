package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Segment {
    public String id;
    public String name;
    public String value;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("name",name);
            jsob.put("value",value);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
