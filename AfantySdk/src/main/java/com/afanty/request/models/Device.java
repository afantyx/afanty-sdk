package com.afanty.request.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Device {
    public String ua;
    public Geo geo;
    public Integer dnt;
    public Integer lmt;
    public String ip;
    public String ipv6;
    public Integer devicetype;
    public String make;
    public String model;
    public String os;
    public String osv;
    public String hwv;
    public Integer h;
    public Integer w;
    public Integer ppi;
    public Float pxratio;
    public Integer js;
    public Integer geofetch;
    public String flashver;
    public String language;
    public String carrier;
    public String mccmnc;
    public Integer connectiontype;
    public String ifa;
    public String didsha1;
    public String didmd5;
    public String dpidsha1;
    public String dpidmd5;
    public String macsha1;
    public String macmd5;
    public String c_bit;
    public String abi;
    public JSONObject ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("ua",ua);
            jsob.put("Geo",geo.toJSON());
            jsob.put("dnt",dnt);
            jsob.put("lmt",lmt);
            jsob.put("ip",ip);
            jsob.put("ipv6",ipv6);
            jsob.put("devicetype",devicetype);
            jsob.put("make",make);
            jsob.put("model",model);
            jsob.put("os",os);
            jsob.put("osv",osv);
            jsob.put("hwv",hwv);
            jsob.put("h",h);
            jsob.put("w",w);
            jsob.put("ppi",ppi);
            jsob.put("pxratio",pxratio);
            jsob.put("js",js);
            jsob.put("geofetch",geofetch);
            jsob.put("flashver",flashver);
            jsob.put("language",language);
            jsob.put("carrier",carrier);
            jsob.put("mccmnc",mccmnc);
            jsob.put("connectiontype",connectiontype);
            jsob.put("ifa",ifa);
            jsob.put("didsha1",didsha1);
            jsob.put("didmd5",didmd5);
            jsob.put("dpidsha1",dpidsha1);
            jsob.put("dpidmd5",dpidmd5);
            jsob.put("macsha1",macsha1);
            jsob.put("macmd5",macmd5);
            jsob.put("c_bit",c_bit);
            jsob.put("abi",abi);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

}
