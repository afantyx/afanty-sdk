package com.afanty.request;

import androidx.annotation.NonNull;

import com.afanty.request.models.App;
import com.afanty.request.models.Device;
import com.afanty.request.models.Imp;
import com.afanty.request.models.Regs;
import com.afanty.request.models.Site;
import com.afanty.request.models.Source;
import com.afanty.request.models.User;
import com.afanty.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BidRequest {
    private static final String TAG = "BidRequestBody";

    public String id;
    public List<Imp> imp;
    public Site site;
    public App app;
    public Device device;
    public User user;
    public Integer test= null;
    public Integer at = 2;
    public Integer tmax;
    public List<String> wseat;
    public List<String> bseat;
    public Integer allimps = null;
    public List<String> cur;
    public List<String> wlang;
    public List<String> bcat;
    public List<String> badv;
    public List<String> bapp;
    public Source source;
    public Regs regs;
    public Object ext;

    public BidRequest() {
    }

    public BidRequest setId(String id) {
        this.id = id;
        return this;
    }

    public BidRequest setImp(List<Imp> imp) {
        this.imp = imp;
        return this;
    }

    public BidRequest setSite(Site site) {
        this.site = site;
        return this;
    }

    public BidRequest setApp(App app) {
        this.app = app;
        return this;
    }

    public BidRequest setDevice(Device device) {
        this.device = device;
        return this;
    }

    public BidRequest setUser(User user) {
        this.user = user;
        return this;
    }

    public BidRequest setTest(int test) {
        this.test = test;
        return this;
    }

    public BidRequest setAt(int at) {
        this.at = at;
        return this;
    }

    public BidRequest setTmax(int tmax) {
        this.tmax = tmax;
        return this;
    }

    public BidRequest setWseat(List<String> wseat) {
        this.wseat = wseat;
        return this;
    }

    public BidRequest setBseat(List<String> bseat) {
        this.bseat = bseat;
        return this;
    }

    public BidRequest setAllimps(int allimps) {
        this.allimps = allimps;
        return this;
    }

    public BidRequest setCur(List<String> cur) {
        this.cur = cur;
        return this;
    }

    public BidRequest setWlang(List<String> wlang) {
        this.wlang = wlang;
        return this;
    }

    public BidRequest setBcat(List<String> bcat) {
        this.bcat = bcat;
        return this;
    }

    public BidRequest setBadv(List<String> badv) {
        this.badv = badv;
        return this;
    }

    public BidRequest setBapp(List<String> bapp) {
        this.bapp = bapp;
        return this;
    }

    public BidRequest setSource(Source source) {
        this.source = source;
        return this;
    }

    public BidRequest setRegs(Regs regs) {
        this.regs = regs;
        return this;
    }

    public BidRequest setExt(Object ext) {
        this.ext = ext;
        return this;
    }


    @NonNull
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            if (site!=null)
                json.put("site", site.toJSON());
            if (app!=null)
                json.put("app", app.toJSON());
            if (device!=null)
                json.put("device", device.toJSON());
            if (user!=null)
                json.put("user", user.toJSON());
            json.put("test", test);
            json.put("at", at);
            json.put("tmax", tmax);
            if (wseat!=null)
                json.put("wseat", wseat.toArray());
            if (bseat!=null)
                json.put("bseat", bseat.toArray());
            json.put("allimps", allimps);
            json.put("imp",createImp());
            if (cur!=null)
                json.put("cur", JSONUtils.convertArr2JSON(cur));
            if (wlang!=null)
                json.put("wlang", JSONUtils.convertArr2JSON(wlang));
            if (bcat!=null)
                json.put("bcat", JSONUtils.convertArr2JSON(bcat));
            if (badv!=null)
                json.put("badv", JSONUtils.convertArr2JSON(badv));
            if (bapp!=null)
                json.put("bapp", JSONUtils.convertArr2JSON(bapp));
            if (source!=null)
                json.put("source", source.toJSON());
            if (regs!=null)
                json.put("regs", regs.toJSON());
            json.put("ext", ext);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return json.toString();
    }

    private JSONArray createImp(){
        JSONArray jsonArray = new JSONArray();
        for(Imp timp:imp){
            jsonArray.put(timp.toJSON());
        }
        return jsonArray;
    }
}
