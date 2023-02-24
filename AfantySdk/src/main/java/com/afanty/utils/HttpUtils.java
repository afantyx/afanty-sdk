package com.afanty.utils;

import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;

import com.afanty.internal.net.http.OkHttpClientFactory;
import com.afanty.internal.net.http.UrlResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static boolean includeKV(String url, String key, String value) {
        try {
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                return value.equals(uri.getQueryParameter(key));
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    public static Map<String, String> parseUrl(String url) {
        Assert.notNEWS(url);
        int index = url.indexOf(63);
        String paramsString = null;
        if (index >= 0) {
            paramsString = url.substring(index + 1);
            if (TextUtils.isEmpty(paramsString)) {
                return null;
            } else {
                String[] params = paramsString.split("&");
                if (params.length == 0) {
                    return null;
                } else {
                    Map<String, String> paramsMap = new HashMap<>();
                    String preKey = null;

                    for (String param : params) {
                        String[] values = param.split("=");
                        if (values.length != 2) {
                            if (preKey != null) {
                                String preValue = (String) paramsMap.get(preKey);
                                paramsMap.put(preKey, preValue + "&" + param);
                            }
                        } else {
                            try {
                                values[1] = URLDecoder.decode(values[1], "UTF-8");
                            } catch (UnsupportedEncodingException var10) {
                            } catch (Exception var11) {
                            }

                            paramsMap.put(values[0], values[1]);
                            preKey = values[0];
                        }
                    }

                    return paramsMap;
                }
            }
        } else {
            return null;
        }
    }

    public static UrlResponse okPostData(String portal, String urlStr, Map<String, String> headers, byte[] buffer, int connectTimeout, int readTimeout) throws IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");

        StringBuilder builder = new StringBuilder(urlStr);
        if (!urlStr.contains("?"))
            builder.append("?");
        if (builder.toString().contains("="))
            builder.append("&");
        builder.append("trace_id").append("=").append(urlEncode(traceId));

        if (headers == null)
            headers = new LinkedHashMap<>();
        headers.put("trace_id", traceId);
        headers.put("business", portal);

        URL url = new URL(builder.toString());
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        for (Map.Entry<String, String> header : headers.entrySet())
            requestBuilder.addHeader(header.getKey(), header.getValue());

        String contentType = headers.containsKey("Content-Type") ? headers.get("Content-Type") : "application/octet-stream";
        RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), buffer);
        requestBuilder.post(requestBody);
        OkHttpClient client = getApiOkHttpClient(portal, url.getHost(), connectTimeout, readTimeout);
        long mStartTime = SystemClock.elapsedRealtime();
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            UrlResponse urlResponse = new UrlResponse(response);

            reportApiRequestStatus(urlStr, portal, mStartTime, requestBody.contentLength(), urlResponse, "");
            return urlResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            reportApiRequestStatus(urlStr, portal, mStartTime, requestBody.contentLength(), null, ex.getMessage());
            throw ex;
        }
    }

    private static void reportApiRequestStatus(String mUrl, String mPortal, long mStartTime, long requestLength, UrlResponse mResponse, String mErrorMsg) {
        reportApiRequestStatus(mUrl, mPortal, mStartTime, requestLength, mResponse, mErrorMsg, false);
    }

    /**
     * Reporting interface request status burial points
     */
    public static void reportApiRequestStatus(String mUrl, String mPortal, long mStartTime, long requestLength, UrlResponse mResponse, String mErrorMsg, boolean isTrackRequest) {
        long mTotalDuration = SystemClock.elapsedRealtime() - mStartTime;
        long mContentLength = 0L;
        boolean mResult;
        int mStatusCode;
        String mStatusMsg;
        if (mResponse != null) {
            mStatusCode = mResponse.getStatusCode();
            mStatusMsg = mResponse.getStatusMessage();
            mContentLength = mResponse.getContent().length();
            if (mStatusCode == HttpURLConnection.HTTP_OK) {
                mResult = true;
            } else {
                if (isTrackRequest && mStatusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    return;
                } else {
                    mResult = false;
                }
            }
        } else {
            mStatusCode = -1;
            mStatusMsg = mErrorMsg;
            mResult = false;
        }
    }

    private static OkHttpClient getApiOkHttpClient(String portal, String host, int connectTimeout, int readTimeout) {
        return OkHttpClientFactory.obtainApiClient(portal, host, true, connectTimeout, readTimeout);
    }

    public static UrlResponse okGetForTracker(String portal, String urlStr, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout, boolean retryOnConnectionFailure) throws IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");

        if (params == null)
            params = new LinkedHashMap<>();
        params.put("trace_id", traceId);

        StringBuilder builder = new StringBuilder(urlStr);
        if (!urlStr.contains("?"))
            builder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.toString().contains("="))
                builder.append("&");
            builder.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
        }

        URL url = new URL(builder.toString());
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet())
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        requestBuilder.addHeader("trace_id", traceId);
        requestBuilder.addHeader("business", portal);
        OkHttpClient client = OkHttpClientFactory.obtainApiClient(portal, url.getHost(), retryOnConnectionFailure, connectTimeout, readTimeout);
        long mStartTime = SystemClock.elapsedRealtime();
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            UrlResponse urlResponse = new UrlResponse(response);
            reportApiRequestStatus(urlStr, portal, mStartTime, 0, urlResponse, "", true);
            return urlResponse;
        } catch (Exception ex) {
            reportApiRequestStatus(urlStr, portal, mStartTime, 0, null, ex.getMessage(), true);
            throw ex;
        }
    }

    public static Pair<Long, Long> parseContentRange(String range, long contentLength) {
        if (TextUtils.isEmpty(range))
            return null;
        range = range.replace("bytes ", "").trim();
        int index = range.indexOf('-');
        String firstBytePosStart = range;
        if (index >= 0)
            firstBytePosStart = range.substring(0, index);
        long firstBytePos = Long.parseLong(firstBytePosStart);
        index = range.indexOf('/');
        if (index >= 0) {
            long fileTotalLength = Long.parseLong(range.substring(index + 1));
            return new Pair<>(firstBytePos, fileTotalLength);
        } else
            return new Pair<>(firstBytePos, firstBytePos + contentLength);
    }

    public static UrlResponse okHead(String portal, String urlStr, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");

        if (params == null)
            params = new LinkedHashMap<>();

        StringBuilder builder = new StringBuilder(urlStr);
        if (!urlStr.contains("?"))
            builder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.toString().contains("="))
                builder.append("&");
            builder.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
        }

        URL url = new URL(builder.toString());
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.head().url(url);
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet())
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        requestBuilder.addHeader("trace_id", traceId);
        requestBuilder.addHeader("business", portal);

        OkHttpClient client = getApiOkHttpClient(portal, url.getHost(), connectTimeout, readTimeout);
        Response response = client.newCall(requestBuilder.build()).execute();
        return new UrlResponse(response);
    }

    public static UrlResponse okGet(String portal, String urlStr, Map<String, String> headers, Map<String, String> params, int connectTimeout, int readTimeout) throws IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");

        if (params == null)
            params = new LinkedHashMap<>();
        params.put("trace_id", traceId);

        StringBuilder builder = new StringBuilder(urlStr);
        if (!urlStr.contains("?"))
            builder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.toString().contains("="))
                builder.append("&");
            builder.append(entry.getKey()).append("=").append(urlEncode(entry.getValue()));
        }

        URL url = new URL(builder.toString());
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                try {
                    requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
                } catch (Exception e) {
                }
            }
        }
        requestBuilder.addHeader("trace_id", traceId);
        requestBuilder.addHeader("business", portal);
        OkHttpClient client = getApiOkHttpClient(portal, url.getHost(), connectTimeout, readTimeout);
        long mStartTime = SystemClock.elapsedRealtime();
        try {
            Response response = client.newCall(requestBuilder.build()).execute();
            UrlResponse urlResponse = new UrlResponse(response);
            reportApiRequestStatus(urlStr, portal, mStartTime, 0, urlResponse, "");
            return urlResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            reportApiRequestStatus(urlStr, portal, mStartTime, 0, null, ex.getMessage());
            throw ex;
        }
    }

    public static UrlResponse okGet(String portal, String urlStr, Map<String, String> headers, JSONObject params, int connectTimeout, int readTimeout) throws IOException {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        if (params == null) {
            params = new JSONObject();
        }

        StringBuilder builder = new StringBuilder(urlStr);
        String requestParam = "";
        try {
            params.put("trace_id", traceId);
            if (!urlStr.contains("?")) {
                builder.append("?");
            }
            Iterator iterator = params.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = String.valueOf(params.get(key));
                builder.append(key).append("=").append(value);
                if (builder.toString().contains("=")) {
                    builder.append("&");
                }
            }
            requestParam = builder.toString();
            if (requestParam.lastIndexOf("&") == requestParam.length() - 1) {
                requestParam = requestParam.substring(0, requestParam.length() - 1);
            }
        } catch (Exception exception) {
        }

        URL url = new URL(requestParam);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (headers != null) {
            Iterator var13 = headers.entrySet().iterator();

            while (var13.hasNext()) {
                Map.Entry<String, String> headerEntry = (Map.Entry) var13.next();
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        requestBuilder.addHeader("trace_id", traceId);
        requestBuilder.addHeader("business", portal);
        OkHttpClient client = getApiOkHttpClient(portal, url.getHost(), connectTimeout, readTimeout);
        Response response = client.newCall(requestBuilder.build()).execute();
        return new UrlResponse(response);
    }

    public static boolean isHttpUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;

        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.startsWith("http://") || lowerCaseUrl.startsWith("https://");
    }
}
