package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Source {
    public Integer fd;
    public String tid;
    public String pchain;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();;
        try {
            jsonObject.put("fd",fd);
            jsonObject.put("tid",tid);
            jsonObject.put("pchain",pchain);
            jsonObject.put("ext",ext);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
