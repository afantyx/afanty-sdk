package com.afanty.request.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Pmp {
    public Integer private_auction = 0;
    public List<Deal> deals;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("private_auction",private_auction);
            if (deals!=null && !deals.isEmpty())
                jsob.put("deals",createDeals());
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    private JSONArray createDeals(){
        if (deals == null || deals.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Deal deal:deals){
            jsonArray.put(deal.toJSON());
        }
        return jsonArray;
    }
}
