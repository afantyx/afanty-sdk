package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Site {
    public String id;
    public String name;
    public String domain;
    public List<String> cat;
    public List<String> sectioncat;
    public List<String> pagecat;
    public String page;
    public String ref;
    public String search;
    public Integer mobile;
    public Integer privacypolicy;
    public Publisher publisher;
    public Content content;
    public String keywords;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("id",id);
            jsob.put("name",name);
            jsob.put("domain",domain);
            if (cat!=null && !cat.isEmpty())
                jsob.put("cat", JSONUtils.convertArr2JSON(cat));
            if (sectioncat!=null && !sectioncat.isEmpty())
                jsob.put("sectioncat", JSONUtils.convertArr2JSON(sectioncat));
            if (pagecat!=null && !pagecat.isEmpty())
                jsob.put("pagecat", JSONUtils.convertArr2JSON(pagecat));
            jsob.put("page",page);
            jsob.put("ref",ref);
            jsob.put("search",search);
            jsob.put("mobile",mobile);
            jsob.put("privacypolicy",privacypolicy);
            jsob.put("publisher",publisher.toJSON());
            jsob.put("content",content.toJSON());
            jsob.put("keywords",keywords);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
