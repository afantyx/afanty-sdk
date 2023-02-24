package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Producer {
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
                jsob.put("cat", JSONUtils.convertArr2JSON(cat));
            jsob.put("domain",domain);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
