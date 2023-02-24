package com.afanty.request.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class User {
    public String id;
    public String buyeruid;
    public Integer yob;
    public String gender;
    public String keywords;
    public String customdata;
    public Geo geo;
    public List<Data> data;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("buyeruid",buyeruid);
            jsob.put("yob",yob);
            jsob.put("gender",gender);
            jsob.put("keywords",keywords);
            jsob.put("customdata",customdata);
            if (geo!=null)
                jsob.put("geo",geo.toJSON());
            if (data!=null && !data.isEmpty())
                jsob.put("data",createDatas());
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    private JSONArray createDatas(){
        if (data == null || data.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Data da:data){
            jsonArray.put(da.toJSON());
        }
        return jsonArray;
    }
}
