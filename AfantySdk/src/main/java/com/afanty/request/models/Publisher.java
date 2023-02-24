package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Publisher {
    public String id;
    public String name;
    public List<String> cat;
    public String domain;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("name",name);
            if (cat!=null && !cat.isEmpty())
                jsob.put("cat",cat);
            jsob.put("domain",domain);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
