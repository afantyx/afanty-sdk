package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Deal {
    public String id;
    public float bidfloor = 0;
    public String bidfloorcur = "USD";
    public Integer at;
    public List<String> wseat;
    public List<String> wadomain;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("bidfloor",bidfloor);
            jsob.put("bidfloorcur",bidfloorcur);
            jsob.put("at",at);
            if (wseat!=null && !wseat.isEmpty())
                jsob.put("wseat",wseat);
            if (wadomain!=null && !wadomain.isEmpty())
                jsob.put("wadomain",wadomain);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
