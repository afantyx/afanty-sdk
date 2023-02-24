package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Content {
    public String id;
    public Integer episode;
    public String title;
    public String series;
    public String season;
    public String artist;
    public String genre;
    public String album;
    public String isrc;
    public Producer producer;
    public String url;
    public List<String> cat;
    public Integer prodq;
    @Deprecated
    public Integer videoquality;
    public Integer context;
    public String contentrating;
    public String userrating;
    public Integer qagmediarating;
    public String keywords;
    public Integer livestream;
    public Integer sourcerelationship;
    public Integer len;
    public String language;
    public Integer embeddable;
    public List<Data> data;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("episode",episode);
            jsob.put("title",title);
            jsob.put("series",series);
            jsob.put("season",season);
            jsob.put("artist",artist);
            jsob.put("genre",genre);
            jsob.put("album",album);
            jsob.put("isrc",isrc);
            jsob.put("producer",producer);
            jsob.put("url",url);
            if (cat!=null && !cat.isEmpty())
                jsob.put("cat", JSONUtils.convertArr2JSON(cat));
            jsob.put("prodq",prodq);
            jsob.put("context",context);
            jsob.put("contentrating",contentrating);
            jsob.put("userrating",userrating);
            jsob.put("qagmediarating",qagmediarating);
            jsob.put("keywords",keywords);
            jsob.put("livestream",livestream);
            jsob.put("sourcerelationship",sourcerelationship);
            jsob.put("len",len);
            jsob.put("language",language);
            jsob.put("embeddable",embeddable);
            if (data!=null && !data.isEmpty()){
                jsob.put("data",createDates());
            }
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    private JSONArray createDates(){
        if (data == null || data.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Data data:data){
            jsonArray.put(data.toJSON());
        }
        return jsonArray;
    }
}
