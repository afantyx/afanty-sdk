package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Banner {
    public List<Format> format;
    public Integer w;
    public Integer h;
    @Deprecated
    public Integer wmax;
    @Deprecated
    public Integer hmax;
    @Deprecated
    public Integer wmin;
    @Deprecated
    public Integer hmin;
    public List<Integer> btype;
    public List<Integer> battr;
    public Integer pos;
    public List<String> mimes;
    public Integer topframe;
    public List<Integer> expdir;
    public List<Integer> api;
    public String id;
    public Integer vcm;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            if (format!=null)
                jsob.put("format", JSONUtils.convertArr2JSON(format));
            jsob.put("h",h);
            jsob.put("w",w);
            jsob.put("pos",pos);
            if (btype!=null)
                jsob.put("btype", JSONUtils.convertArr2JSON(btype));
            if (battr!=null)
                jsob.put("battr", JSONUtils.convertArr2JSON(battr));
            jsob.put("pos",pos);
            if (mimes!=null)
                jsob.put("mimes", JSONUtils.convertArr2JSON(mimes));
            jsob.put("topframe",topframe);
            if (expdir!=null)
                jsob.put("expdir", JSONUtils.convertArr2JSON(expdir));
            if (api!=null)
                jsob.put("api", JSONUtils.convertArr2JSON(api));
            jsob.put("id",id);
            jsob.put("vcm",vcm);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }
}
