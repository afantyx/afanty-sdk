package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Geo {
    public Float lat;
    public Float lon;
    public Integer type;
    public Integer accuracy;
    public Integer lastfix;
    public Integer ipservice;
    public String country;
    public String region;
    public String regionfips104;
    public String metro;
    public String city;
    public String zip;
    public Integer utcoffset;
    public Object ext;


    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("lat",lat);
            jsob.put("lon",lon);
            jsob.put("type",type);
            jsob.put("accuracy",accuracy);
            jsob.put("lastfix",lastfix);
            jsob.put("ipservice",ipservice);
            jsob.put("country",country);
            jsob.put("region",region);
            jsob.put("regionfips104",regionfips104);
            jsob.put("metro",metro);
            jsob.put("city",city);
            jsob.put("zip",zip);
            jsob.put("utcoffset",utcoffset);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
