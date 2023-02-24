package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Native {
    public String request;
    public String ver;
    public List<Integer> api;
    public List<Integer> battr;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("request",request);
            jsob.put("ver",ver);
            if (api!=null && !api.isEmpty())
                jsob.put("api",api);
            if (battr!=null && !battr.isEmpty())
                jsob.put("battr", JSONUtils.convertArr2JSON(battr));
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
