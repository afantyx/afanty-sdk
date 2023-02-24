package com.afanty;

import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.config.BasicAftConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.net.http.UrlResponse;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.models.Bid;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackUrlsHelper {

    private static final String TAG = "AD.TrackUrl";
    private static final String MACRO_PLAY_DURATION = "{PLAYDURATION}";

    private static final String MACRO_EFFECT_TYPE = "{EFFECT_TYPE}";
    private static final String MACRO_EXT = "{EXT}";

    public final static int EFFECT_ACTION_ADD = 10004;
    public final static int EFFECT_ACTION_ACTIVE = 10005;
    public static final int D_ADD_TRACKER = 10000;
    public static final int D_S_TRACKER = 10001;
    public static final int D_F_TRACKER = 10002;
    public static final int D_INS_TRACKER = 10003;

    public interface TrackerListener {
        void onResult(boolean result);
    }

    public static void reportVideoTrack(List<String> trackUrls, int duration, String adId) {
        List<String> urls = new ArrayList<>();
        for (String url : trackUrls) {
            String trackUrl = url.replace(MACRO_PLAY_DURATION, String.valueOf(duration));
            urls.add(trackUrl);
        }

        TrackUrlsHelper.reportTrackUrls(urls, TrackType.VIDEO, adId);
    }

    public static void reportTrackUrls(final List<String> urls, final TrackType trackType, final String adId) {
        if (urls == null || urls.isEmpty())
            return;
        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void callBackOnUIThread() {
                final String ua = CommonUtils.getWebViewUA();
                for (final String url : urls) {
                    ThreadManager.getInstance().run(new DelayRunnableWork("Report.Urls") {
                        @Override
                        public void execute() {
                            reportTrackUrlWithUA(url, ua, trackType, adId);
                        }
                    });
                }
            }
        });
    }

    public static void reportTrackUrls(final List<String> urls, final TrackType trackType, final String adId, final TrackerListener listener) {
        if (urls == null || urls.isEmpty())
            return;

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void callBackOnUIThread() {
                final String ua = CommonUtils.getWebViewUA();
                for (final String url : urls) {
                    ThreadManager.getInstance().run(new DelayRunnableWork("Report.Urls") {
                        @Override
                        public void execute() {
                            boolean result = reportTrackUrlWithUA(url, ua, trackType, adId);
                            if (listener != null)
                                listener.onResult(result);
                        }
                    });
                }
            }
        });
    }

    public static void reportTrackUrlsWithMacro(final List<String> urls, final TrackType trackType, final String adId,
                                                final String macro, final String value) {
        if (urls == null || urls.isEmpty())
            return;

        final List<String> modifiedUris = new ArrayList<String>();
        for (final String originalUri : urls) {
            String modifiedUri = originalUri;
            if (TextUtils.isEmpty(modifiedUri)) {
                continue;
            }
            modifiedUri = modifiedUri.replaceAll("\\[" + macro + "\\]", value);
            modifiedUris.add(modifiedUri);
        }

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void callBackOnUIThread() {
                final String ua = CommonUtils.getWebViewUA();
                for (final String url : modifiedUris) {
                    ThreadManager.getInstance().run(new DelayRunnableWork("Report.Urls") {
                        @Override
                        public void execute() {
                            reportTrackUrlWithUA(url, ua, trackType, adId);
                        }
                    });
                }
            }
        });
    }

    public static boolean reportTrackUrlWithUA(String url, String userAgent, TrackType type, String adId) {
        return reportTrackUrlWithUA(url, userAgent, type, 0, adId);
    }

    public static boolean reportTrackUrlWithUA(String url, String userAgent, TrackType type, int retryCount, String adId) {
        return reportTrackUrlWithUA(url, userAgent, type, retryCount, 0, adId);
    }

    public static boolean reportTrackUrlWithUA(String url, String userAgent, TrackType type, int retryCount, int totalReportCount, String adId) {
        if (TextUtils.isEmpty(url))
            return false;

        long startLoadTime = System.currentTimeMillis();
        String trackUrl = AdsUtils.replaceMacroUrls(url);

        try {
            if (AdsUtils.isGPDetailUrl(trackUrl)) {
                if (BasicAftConfig.handleMarketSchema()) {
                    if (trackUrl.startsWith("market://"))
                        trackUrl = trackUrl.replace("market://", "https://play.google.com/store/apps/");
                } else {
                    return true;
                }
            } else if (!HttpUtils.isHttpUrl(trackUrl)) {
                return true;
            }

            Map<String, String> header = new HashMap<String, String>();
            header.put("User-Agent", userAgent);
            Log.d("aft_t",trackUrl);
            UrlResponse response = HttpUtils.okGetForTracker(AdConstants.PortalKey.TRACK_HELPER, trackUrl, header, null, BasicAftConfig.getTrackConnectTimeout(), BasicAftConfig.getTrackReadTimeout(), BasicAftConfig.getPingRetryOnConnectionFailure());
            long duration = System.currentTimeMillis() - startLoadTime;
            if (response.getStatusCode() == 302) {
                List<String> urls = response.getHeaders().get("Location");
                if (urls == null || TextUtils.equals(urls.get(0), trackUrl))
                    return false;
                int maxRedirectCount = 10;
                if (retryCount++ >= maxRedirectCount) {
                    return false;
                }
                return reportTrackUrlWithUA(urls.get(0), userAgent, type, retryCount, totalReportCount, adId);
            } else if (response.getStatusCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            if (totalReportCount == 0 || retryCount == totalReportCount - 1) {
                long duration = System.currentTimeMillis() - startLoadTime;
            }
        }
        return false;
    }

    public static String getDomain(String url) {
        String host = "unKnown";
        try {
            host = Uri.parse(url).getHost();
        } catch (Exception ex) {
        }
        return host;
    }

    private static List<String> getTrackUrls(Bid adData) {
        if (adData == null)
            return Collections.emptyList();
        List<String> actionTrackUrls = adData.getTrackActionAdvertiserUrls();
        if (actionTrackUrls.isEmpty())
            return adData.getLandingPageTrackClickUrls().isEmpty() ? adData.getTrackClickUrls() : adData.getLandingPageTrackClickUrls();
        return actionTrackUrls;
    }

    public static String[] getTrackUrlArr(Bid adData) {
        List<String> trackUrls = getTrackUrls(adData);
        return trackUrls.toArray(new String[trackUrls.size()]);
    }

    public static void reportEffectTrackUrl(final String trackUrls, final String adId, final String pkg, final int action) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            ThreadManager.getInstance().run(new DelayRunnableWork() {
                @Override
                public void execute() throws Exception {
                    doReportEffectTrackUrl(trackUrls, adId, pkg, action);
                }
            });
        } else {
            doReportEffectTrackUrl(trackUrls, adId, pkg, action);
        }
    }

    public static String convertTracker(String[] trackerArray) {
        if (trackerArray == null || trackerArray.length==0) return "";
        StringBuilder builder = new StringBuilder();

        for (String trackUrl : trackerArray) {
            if (builder.length() == 0) {
                builder.append(trackUrl);
            } else {
                builder.append(",").append(trackUrl);
            }
        }
        return builder.toString();
    }

    public static String convertTrackerS2s(String trackerArray) {
        if (TextUtils.isEmpty(trackerArray))
            return "";
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(trackerArray);
            for (int i=0;i<jsonArray.length();i++){
                builder.append(jsonArray.optString(i)).append(",");
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
        }catch (Exception e){}
        return builder.toString();
    }

    public static String convertListTracker(List<String> trackerArray) {
        if (trackerArray == null || trackerArray.size()==0) return "";
        StringBuilder builder = new StringBuilder();

        for (String trackUrl : trackerArray) {
            if (builder.length() == 0) {
                builder.append(trackUrl);
            } else {
                builder.append(",").append(trackUrl);
            }
        }
        return builder.toString();
    }

    private static void doReportEffectTrackUrl(String trackUrls, String adId, String pkg, int action) {
        if (TextUtils.isEmpty(trackUrls)) {
            return;
        }
        String[] urls = trackUrls.split(",");
        if (urls.length >= 1) {
            JSONObject ext = new JSONObject();
            try {
                ext.put("event_time", System.currentTimeMillis());
            } catch (JSONException e) {
            }
            final String ua = CommonUtils.getWebViewUA();
            TrackType type;
            switch (action) {
                case EFFECT_ACTION_ACTIVE:
                    type = TrackType.ACTIVE;
                    break;
                case D_ADD_TRACKER:
                    type = TrackType.D_A_LIST;
                    break;
                case D_S_TRACKER:
                    type = TrackType.D_S_DOWNLOAD;
                    break;
                case D_F_TRACKER:
                    type = TrackType.D_F_DOWNLOAD;
                    break;
                case D_INS_TRACKER:
                    type = TrackType.D_C_AZ;
                    break;
                default:
                    type = TrackType.SI_ADD;
                    break;
            }
            for (String url : urls) {
                url = AdsUtils.replaceMarcoUrls(url, MACRO_EFFECT_TYPE, String.valueOf(action));
                url = AdsUtils.replaceMarcoUrls(url, MACRO_EXT, ext.toString());
                TrackUrlsHelper.reportTrackUrlWithUA(url, ua, type, adId);
            }
        }
    }

}
