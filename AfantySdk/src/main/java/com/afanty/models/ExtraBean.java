package com.afanty.models;

import com.afanty.internal.internal.ProductData;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExtraBean {
    protected int actionType;
    protected String url;
    protected int fmt_id;
    protected ProductData productData;
    protected int a_d;
    protected int c_type;
    protected JSONArray a_tracker;

    public ExtraBean(JSONObject jsonObject) {
        url = jsonObject.optString("cu");
        actionType = jsonObject.optInt("tp");
        String appInfo = jsonObject.optString("app_info");
        try{
            productData = new ProductData(new JSONObject(appInfo));
        }catch (Exception ignore){}

        fmt_id = jsonObject.optInt("fmt_id");
        a_d = jsonObject.optInt("a_d");
        c_type = jsonObject.optInt("c_type");
        a_tracker = jsonObject.optJSONArray("a_tracker");
    }

    public int getFmt_id() {
        return fmt_id;
    }
}
