package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Imp {
    public String id;
    public List<Metric> metric;
    public Banner banner;
    public Video video;
    public Audio audio;
    public Native aNative;
    public Pmp pmp;
    public String displaymanager;
    public String displaymanagerver;
    public Integer instl = 0;
    public String tagid;
    public float bidfloor = 0;
    public String bidfloorcur = "USD";
    public Integer clickbrowser;
    public Integer secure;
    public List<String> iframebuster;
    public Integer exp;
    public Object ext;

    public Imp() {
    }

    public Imp(String id,List<Metric> metric,Banner banner,Video video,Audio audio,Native aNative,Pmp pmp,
               String displaymanager,String displaymanagerver,int instl,String tagid,float bidfloor,String bidfloorcur,
               int clickbrowser,int secure,List<String> iframebuster,int exp,Object ext) {
        this.id=id;
        this.metric = metric;
        this.banner = banner;
        this.video=video;
        this.audio = audio;
        this.aNative = aNative;
        this.pmp = pmp;
        this.displaymanager = displaymanager;
        this.displaymanagerver = displaymanagerver;
        this.instl = instl;
        this.tagid = tagid;
        this.bidfloor = bidfloor;
        this.bidfloorcur = bidfloorcur;
        this.clickbrowser = clickbrowser;
        this.secure = secure;
        this.iframebuster = iframebuster;
        this.exp = exp;
        this.ext = ext;
    }

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("metric",createMetrics());
            if (banner!=null)
                jsob.put("banner",banner.toJSON());
            if (video!=null)
                jsob.put("video",video.toJSON());
            if (audio!=null)
                jsob.put("audio",audio.toJSON());
            if (aNative!=null)
                jsob.put("native",aNative.toJSON());
            if (pmp!=null)
                jsob.put("pmp",pmp.toJSON());
            jsob.put("displaymanager",displaymanager);
            jsob.put("displaymanagerver",displaymanagerver);
            jsob.put("instl",instl);
            jsob.put("tagid",tagid);
            jsob.put("bidfloor",bidfloor);
            jsob.put("bidfloorcur",bidfloorcur);
            jsob.put("clickbrowser",clickbrowser);
            jsob.put("secure",secure);
            if (iframebuster!=null && !iframebuster.isEmpty())
                jsob.put("iframebuster", JSONUtils.convertArr2JSON(iframebuster));
            jsob.put("exp",exp);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    public JSONArray createMetrics(){
        if (metric == null || metric.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Metric mt:metric){
            jsonArray.put(mt.toJSON());
        }
        return jsonArray;
    }

}
