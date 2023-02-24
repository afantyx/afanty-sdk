package com.afanty.internal.webview.jsinterface;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsTagBridge {

    private static final String TAG = "Aft.JsTagBridge";

    private String mAdId = "", mPlacementId = "", mCreativeId = "", mFormatId = "";
    private IJSBrigeListener mIJSBrigeListener;

    public void setJSStatsParams(String adId, String placementId, String creativeId, String formatId) {
        this.mAdId = adId;
        this.mPlacementId = placementId;
        this.mCreativeId = creativeId;
        this.mFormatId = formatId;
    }

    public void setIJSBrigeListener(IJSBrigeListener iJSBrigeListener) {
        this.mIJSBrigeListener = iJSBrigeListener;
    }

    @JavascriptInterface
    public String getRollParam() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("ad_id", mAdId);
            jsonObject.put("placement_id", mPlacementId);
            jsonObject.put("creative_id", mCreativeId);
            jsonObject.put("formatid", mFormatId);
            jsonObject.put("tm", System.currentTimeMillis() + "");

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @JavascriptInterface
    public void isCarouselJsTag() {
        if (mIJSBrigeListener != null)
            mIJSBrigeListener.inCarousel();
    }

    @JavascriptInterface
    public void adStatsForJsTag(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String eventId = jsonObject.optString("eventId", "");
            if (TextUtils.isEmpty(eventId)) {
                return;
            }

            JSONArray infoArray = jsonObject.optJSONArray("info");
            HashMap<String, String> infos = new LinkedHashMap<String, String>();

            if (infoArray != null && infoArray.length() > 0) {
                for (int index = 0; index < infoArray.length(); index++) {
                    JSONObject info = infoArray.getJSONObject(index);
                    infos.putAll(getMapFromJson(info));
                }

            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getMapFromJson(JSONObject jsonObject) {
        Map<String, String> valueMap = new HashMap<>();
        String key, value;
        try {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                key = keys.next().trim();
                value = jsonObject.optString(key);
                if (!TextUtils.isEmpty(key))
                    valueMap.put(key, value);
            }
            return valueMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueMap;
    }

    public interface IJSBrigeListener {
        void inCarousel();
    }
}
