package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class App {
    public String id;
    public String name;
    public String bundle;
    public String domain;
    public String storeurl;
    public List<String> cat;
    public List<String> sectioncat;
    public List<String> pagecat;
    public String ver;
    public Integer privacypolicy;
    public Integer paid;
    public Publisher publisher;
    public Content content;
    public String keywords;
    public Long sv;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("name",name);
            jsob.put("bundle",bundle);
            jsob.put("domain",domain);
            jsob.put("storeurl",storeurl);
            if (cat!=null && !cat.isEmpty())
                jsob.put("cat", JSONUtils.convertArr2JSON(cat));
            if (sectioncat!=null && !sectioncat.isEmpty())
                jsob.put("sectioncat", JSONUtils.convertArr2JSON(sectioncat));
            if (pagecat!=null && !pagecat.isEmpty())
                jsob.put("pagecat", JSONUtils.convertArr2JSON(pagecat));
            jsob.put("ver",ver);
            jsob.put("privacypolicy",privacypolicy);
            jsob.put("paid",paid);
            if (publisher!=null)
                jsob.put("publisher",publisher.toJSON());
            if (content!=null)
                jsob.put("content",content.toJSON());
            jsob.put("keywords",keywords);
            jsob.put("sv",sv);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
