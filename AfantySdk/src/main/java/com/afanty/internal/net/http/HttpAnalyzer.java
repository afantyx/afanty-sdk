package com.afanty.internal.net.http;

import android.os.SystemClock;
import android.text.TextUtils;

import com.afanty.internal.net.utils.NetworkStatus;
import com.afanty.utils.ContextUtils;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpAnalyzer {
    private static final String TAG = "HttpAnalyzer.AD";
    private static boolean needLog = false;
    public static final String KEY_CFG_HTTP_STATS_RATE = "http_stats_rate_denom";

    enum HttpStep {
        Init("init"), DNSStart("dns_start"), DNSEnd("dns_end"), ConnectStart("connect_start"), ConnectSStart("connect_s_start"), ConnectSEnd("connect_s_end"), ConnectEnd("connect_end"), ConnectAcquire("connect_acq"),
        SendHeaderStart("send_header_start"), SendHeaderEnd("send_header_end"), SendBodyStart("send_body_start"), SendBodyEnd("send_body_end"), RecvHeaderStart("recv_header_start"), RecvHeaderEnd("recv_header_end"),
        RecvBodyStart("recv_body_start"), RecvBodyEnd("recv_body_end"), Success("success");

        private String mValue;

        HttpStep(String value) {
            mValue = value;
        }

        private final static Map<String, HttpStep> VALUES = new HashMap<String, HttpStep>();

        static {
            for (HttpStep item : HttpStep.values())
                VALUES.put(item.mValue, item);
        }

        public static HttpStep fromString(String value) {
            return VALUES.containsKey(value) ? VALUES.get(value.toLowerCase()) : Init;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    private String mTraceId;
    private String mPortal;
    private String mLoadType;

    private final String mUrl, mMethod;
    private HttpStep mCurrentStep;
    private String mIpAddress;
    private int mHttpCode;
    private long mContentLength, mReadBytes, mWriteBytes, mDuration, mFirstRecvDuration, mDnsDuration, mConnectDuration, mSendDuration, mRecvDuration, mRespDuration;

    private long mStartTime, mStepStartTime;
    private String mCacheHit;
    private int mRedirectCount;
    private JSONArray mRedirectUrls = new JSONArray();
    private AtomicBoolean mCompleted = new AtomicBoolean(false);

    private static class SystemProperties {
        private static boolean isReflectInited = false;
        private static Method getPropertyMethod = null;

        public static void init() {
            if (!isReflectInited) {
                isReflectInited = true;
                try {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    getPropertyMethod = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                    getPropertyMethod.setAccessible(true);
                } catch (Throwable throwable) {
                }
            }
        }

        public static String get(String property, String defaultValue) {
            String propertyValue = defaultValue;
            if (getPropertyMethod != null) {
                try {
                    propertyValue = (String) getPropertyMethod.invoke(null, property, defaultValue);
                } catch (Throwable throwable) {
                }
            }
            return propertyValue;
        }

    }

    HttpAnalyzer(String traceId, String url, String portal, String loadType, String method) {
        mTraceId = traceId;
        mUrl = url;
        mMethod = method;
        mPortal = portal;
        mLoadType = loadType;
        mCurrentStep = HttpStep.Init;

    }

    private static final String[] DNS_SERVER_PROPERTIES = new String[]{"net.dns1",
            "net.dns2", "net.dns3", "net.dns4"};

    public static String[] readDnsServersFromSystemProperties() {
        SystemProperties.init();
        String[] dnsServers = new String[4];
        int i = 0;
        for (String property : DNS_SERVER_PROPERTIES) {
            String server = SystemProperties.get(property, "");
            if (server != null && !server.isEmpty() && i < 4) {
                /* just need the string, no matter whether it is ip or host name.
                try {
                    InetAddress ip = InetAddress.getByName(server);
                    if (ip == null) continue;
                    server = ip.getHostAddress();
                    if (server == null || server.isEmpty()) {
                        continue;
                    }
                } catch (Throwable throwable) {
                    continue;
                }
                */
                dnsServers[i++] = server;

            }
        }
        return dnsServers;
    }

    public String getTraceId() {
        return mTraceId;
    }

    public void traceStart() {
        mStartTime = SystemClock.elapsedRealtime();
        mStepStartTime = mStartTime;
    }

    public void traceDnsStart(String domainName) {
        mCurrentStep = HttpStep.DNSStart;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceDnsStop() {
        long current = SystemClock.elapsedRealtime();
        mCurrentStep = HttpStep.DNSEnd;
        mDnsDuration = current - mStepStartTime;
        mStepStartTime = current;
    }

    public void traceConnectStart(String ipAddress) {
        mCurrentStep = HttpStep.ConnectStart;
        mIpAddress = ipAddress;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceConnectSStart() {
        mCurrentStep = HttpStep.ConnectSStart;
    }

    public void traceConnectSEnd() {
        mCurrentStep = HttpStep.ConnectSEnd;
    }

    public void traceConnectEnd() {
        mCurrentStep = HttpStep.ConnectEnd;
        long current = SystemClock.elapsedRealtime();
        mConnectDuration = current - mStepStartTime;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceConnectFailed() {
        long current = SystemClock.elapsedRealtime();
        mConnectDuration = current - mStepStartTime;
        mStepStartTime = current;
    }

    public void traceConnectAcquired() {
        mCurrentStep = HttpStep.ConnectAcquire;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceSendHeaderStart() {
        mCurrentStep = HttpStep.SendHeaderStart;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceSendHeaderEnd() {
        mCurrentStep = HttpStep.SendHeaderEnd;
        mSendDuration = SystemClock.elapsedRealtime() - mStepStartTime;
    }

    public void traceSendBodyStart() {
        mCurrentStep = HttpStep.SendBodyStart;
    }

    public void traceSendBodyEnd(long writeBytes) {
        mCurrentStep = HttpStep.SendBodyEnd;
        mWriteBytes = writeBytes;
        mSendDuration = SystemClock.elapsedRealtime() - mStepStartTime;
    }

    public void traceRecvHeaderStart() {
        mCurrentStep = HttpStep.RecvHeaderStart;
        mStepStartTime = SystemClock.elapsedRealtime();
    }

    public void traceRecvHeaderEnd(int httpCode, long contentLength, String cacheHit) {
        mCurrentStep = HttpStep.RecvHeaderEnd;
        mHttpCode = httpCode;
        mContentLength = contentLength;
        mCacheHit = cacheHit;

        long current = SystemClock.elapsedRealtime();
        mFirstRecvDuration = current - mStartTime;
        mRecvDuration = current - mStepStartTime;
        mRespDuration = current - mStepStartTime;

        if (mHttpCode < 200 || mHttpCode >= 300)
            traceEnd(null);
    }

    public void traceRecvBodyStart() {
        mCurrentStep = HttpStep.RecvBodyStart;
    }

    public void traceRecvBodyEnd(long readBytes) {
        mReadBytes = readBytes;
        mCurrentStep = HttpStep.RecvBodyEnd;
        mRecvDuration = SystemClock.elapsedRealtime() - mStepStartTime;
    }

    public void traceRevRedirect(int httpCode, String location) {
        mRedirectCount++;
        mRedirectUrls.put(location);
    }

    public void traceEnd(Exception exception) {
        if (TextUtils.isEmpty(mTraceId) || !mCompleted.compareAndSet(false, true)) {
            return;
        }


        mDuration = SystemClock.elapsedRealtime() - mStartTime;
        boolean success = (mHttpCode >= 200 && mHttpCode < 300) && (exception == null);
        if (success)
            mCurrentStep = HttpStep.Success;
        String errMsg = success ? null : ("http status:" + mHttpCode + ((exception != null) ? ", " + (TextUtils.isEmpty(exception.getMessage()) ? "no message" : exception.getMessage()) : ""));
        String paramHost = "";
        try {
            int paramPos = mUrl.indexOf("?");
            URL url = new URL(mUrl);
            String paramUrl = (mUrl.substring(0, (paramPos < 0) ? mUrl.length() : paramPos)) + "(" + mMethod + ")";
            paramHost = url.getHost();
            String path = url.getPath();
            String suffix = getExtension(path);
            String paramPath = TextUtils.isEmpty(suffix) ? path : "*." + suffix;

            boolean isDirectUrl = mUrl.contains("googlevideo.com");

            if (!paramPath.equals("*.m3u8") && !paramPath.equals("*.mpd") && !shouldCollect() && !isDirectUrl)
                return;

            String paramNet = NetworkStatus.getNetworkStatusEx(ContextUtils.getContext()).getNetTypeDetailForStats();

            HashMap<String, String> paramsDetail = new LinkedHashMap<String, String>();
            paramsDetail.put("trace_id", mTraceId);
            paramsDetail.put("url", isDirectUrl ? mUrl : paramUrl);
            paramsDetail.put("business", mPortal);
            if (!TextUtils.isEmpty(mLoadType)) {
                paramsDetail.put("load_type", mLoadType);
            }
            paramsDetail.put("host", paramHost);
            paramsDetail.put("path", paramPath);
            paramsDetail.put("network", paramNet);
            paramsDetail.put("result", mCurrentStep.toString());
            paramsDetail.put("total_duration", String.valueOf(mDuration));
            paramsDetail.put("first_recv_duration", String.valueOf(mFirstRecvDuration));
            paramsDetail.put("content_length", String.valueOf(mContentLength));
            paramsDetail.put("error_code", String.valueOf(mHttpCode));
            paramsDetail.put("error_msg", errMsg);

            paramsDetail.put("ipaddr", mIpAddress);
            paramsDetail.put("dns_duration", String.valueOf(mDnsDuration));
            paramsDetail.put("connect_duration", String.valueOf(mConnectDuration));
            paramsDetail.put("send_duration", String.valueOf(mSendDuration));
            paramsDetail.put("recv_duration", String.valueOf(mRecvDuration));
            paramsDetail.put("resp_duration", String.valueOf(mRespDuration));
            paramsDetail.put("read_bytes", String.valueOf(mReadBytes));
            paramsDetail.put("cdn_cache", mCacheHit);
            paramsDetail.put("redirect_count", String.valueOf(mRedirectCount));
            paramsDetail.put("redirect_urls", mRedirectUrls.toString());
            paramsDetail.put("write_bytes", String.valueOf(mWriteBytes));

            if (mIpAddress != null && !mIpAddress.equals("") &&
                    (paramPath.equals("*.mpd") ||
                            paramPath.equals("*.m3u8") || isDirectUrl) && ContextUtils.get(url.toString()) == null) {
                ContextUtils.add("serveraddr_" + url.toString(), mIpAddress);
            }

            String dns_server = "";
            try {
                String[] dnsServers = readDnsServersFromSystemProperties();
                for (int i = 0; i < dnsServers.length && i < 4 && dnsServers[i] != null && !dnsServers[i].equals(""); i++) {
                    if (i != 0) dns_server += ",";
                    dns_server += dnsServers[i];
                }
            } catch (Throwable throwable) {
            }
            paramsDetail.put("dns_server", dns_server);
            float downloadSpeed = (mReadBytes == 0 || mRecvDuration == 0) ? 0 : (1.0f * mReadBytes / 1000 / (1.0f * mRecvDuration / 1000));
            long realSendDuration = mSendDuration + mRespDuration;
            float uploadSpeed = (mWriteBytes == 0 || realSendDuration == 0) ? 0 : (1.0f * mWriteBytes / 1000 / (1.0f * realSendDuration / 1000));
            paramsDetail.put("download_speed", String.valueOf(downloadSpeed));
            paramsDetail.put("upload_speed", String.valueOf(uploadSpeed));
//            AdStatsHelper.onEvent(ContextUtils.getContext(), Stats.Http.AFT_NET_HTTP_CONNECT_DETAIL, paramsDetail);
        } catch (Exception e) {
        } finally {
        }
    }

    /**
     * return file extension name, without .
     */
    private String getExtension(String filename) {
        String extension = "";
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length() - 1) {
                extension = filename.substring(dot + 1);
            }
        }
        return extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpAnalyzer that = (HttpAnalyzer) o;

        return mTraceId.equals(that.mTraceId);
    }

    @Override
    public int hashCode() {
        return mTraceId.hashCode();
    }

    private boolean shouldCollect() {
        return !TextUtils.isEmpty(mUrl) && mUrl.contains("/feedback/upload");
    }
}
