package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Regs {
    public Integer coppa;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();;
        try {
            jsonObject.put("coppa",coppa);
            jsonObject.put("ext",ext);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
