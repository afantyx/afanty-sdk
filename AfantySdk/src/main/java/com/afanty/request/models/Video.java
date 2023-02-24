package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Video {
    public List<String> mimes;
    public Integer minduration;
    public Integer maxduration;
    public List<Integer> protocols;
    @Deprecated
    public Integer protocol;
    public Integer w;
    public Integer h;
    public Integer startdelay;
    public Integer placement;
    public Integer linearity;
    public Integer skip;
    public Integer skipmin = 0;
    public Integer skipafter = 0;
    public Integer sequence;
    public List<Integer> battr;
    public Integer maxextended;
    public Integer minbitrate;
    public Integer maxbitrate;
    public Integer boxingallowed = 1;
    public List<Integer> playbackmethod;
    public Integer playbackend;
    public List<Integer> delivery;
    public Integer pos;
    public List<Banner> companionad;
    public List<Integer> api;
    public List<Integer> companiontype;
    public Object ext;


    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            if (mimes!=null)
                jsob.put("mimes", JSONUtils.convertArr2JSON(mimes));
            jsob.put("minduration",minduration);
            jsob.put("maxduration",maxduration);
            if (protocols!=null)
                jsob.put("protocols", JSONUtils.convertArr2JSON(protocols));
            jsob.put("w",w);
            jsob.put("h",h);
            jsob.put("startdelay",startdelay);
            jsob.put("placement",placement);
            jsob.put("linearity",linearity);
            jsob.put("skip",skip);
            jsob.put("skipmin",skipmin);
            jsob.put("skipafter",skipafter);
            jsob.put("sequence",sequence);
            if (battr!=null && !battr.isEmpty())
                jsob.put("battr",battr.toArray());
            jsob.put("maxextended",maxextended);
            jsob.put("minbitrate",minbitrate);
            jsob.put("maxbitrate",maxbitrate);
            jsob.put("boxingallowed",boxingallowed);
            if (playbackmethod!=null && !playbackmethod.isEmpty())
                jsob.put("playbackmethod", JSONUtils.convertArr2JSON(playbackmethod));
            jsob.put("playbackend",playbackend);
            if (delivery!=null && !delivery.isEmpty())
                jsob.put("delivery", JSONUtils.convertArr2JSON(delivery));
            jsob.put("pos",pos);
            if (companionad!=null && !companionad.isEmpty())
                jsob.put("companionad",createBanners());
            if (api!=null && !api.isEmpty())
                jsob.put("api",api);
            if (companiontype!=null && !companiontype.isEmpty())
                jsob.put("companiontype",companiontype);
            jsob.put("ext",ext);
        }catch (JSONException e){
        }
        return jsob;
    }

    private JSONArray createBanners(){
        if (companionad == null || companionad.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Banner banner:companionad){
            jsonArray.put(banner.toJSON());
        }
        return jsonArray;
    }


}
