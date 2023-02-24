package com.afanty.request.models;

import com.afanty.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Audio {
    public List<String> mimes;
    public Integer minduration;
    public Integer maxduration;
    public List<Integer> protocols;
    public Integer startdelay;
    public Integer sequence;
    public List<Integer> battr;
    public Integer maxextended;
    public Integer minbitrate;
    public Integer maxbitrate;
    public List<Integer> delivery;
    public List<Banner> companionad;
    public List<Integer> api;
    public List<Integer> companiontype;
    public Integer maxseq;
    public Integer feed;
    public Integer stitched;
    public Integer nvol;
    public Object ext;

    public JSONObject toJSON(){
        JSONObject jsob = new JSONObject();
        try {
            if (mimes!=null && !mimes.isEmpty())
                jsob.put("format", JSONUtils.convertArr2JSON(mimes));
            jsob.put("minduration",minduration);
            jsob.put("maxduration",maxduration);
            if (protocols!=null && !protocols.isEmpty())
                jsob.put("protocols", JSONUtils.convertArr2JSON(protocols));
            jsob.put("startdelay",startdelay);
            jsob.put("sequence",sequence);
            if (battr!=null && !battr.isEmpty())
                jsob.put("battr",battr.toArray());
            jsob.put("maxextended",maxextended);
            jsob.put("minbitrate",minbitrate);
            jsob.put("maxbitrate",maxbitrate);
            if (delivery!=null && !delivery.isEmpty())
                jsob.put("delivery",delivery);
            if (companionad!=null && !companionad.isEmpty())
                jsob.put("companionad",createBanners());
            if (api!=null && !api.isEmpty())
                jsob.put("api",api);
            if (companiontype!=null && !companiontype.isEmpty())
                jsob.put("companiontype", JSONUtils.convertArr2JSON(companiontype));
            jsob.put("maxseq",maxseq);
            jsob.put("feed",feed);
            jsob.put("stitched",stitched);
            jsob.put("nvol",nvol);
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
