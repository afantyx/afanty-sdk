package com.afanty.internal.webview;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static JSONObject toJSONObject(String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("responseCode", code);
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    public static JSONObject toJSONObject(String code, Exception e) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("responseCode", code);
            jsonObject.put("exception", e.toString());
        } catch (JSONException je) {
        }
        return jsonObject;
    }

}
