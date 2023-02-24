package com.afanty.request.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class Data {
    public String id;
    public String name;
    public List<Segment> segment;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("name",name);
            jsob.put("segment",createSegArray());
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    private JSONArray createSegArray(){
        JSONArray array=null;
        if (segment!=null && !segment.isEmpty()){
            array = new JSONArray();
            for (Segment se:segment){
                array.put(se.toJSON());
            }
        }
        return array;
    }
}
