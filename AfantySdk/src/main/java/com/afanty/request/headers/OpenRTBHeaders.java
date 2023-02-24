package com.afanty.request.headers;

import java.util.HashMap;
import java.util.Map;

public final class OpenRTBHeaders {
    public static Map<String,String> headers(){
        Map<String, String> headers = new HashMap<>();
        headers.put("x-openrtb-version", "2.5");
        headers.put("Content-Type", "application/json");
        headers.put("Accept-Charset", "utf-8");
        return headers;
    }
}
