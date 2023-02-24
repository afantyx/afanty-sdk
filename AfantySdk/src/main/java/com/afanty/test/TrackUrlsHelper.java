package com.afanty.test;

import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.config.BasicAftConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.net.http.UrlResponse;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackUrlsHelper {

    private static final String MACRO_PLAY_DURATION = "{PLAYDURATION}";



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
            UrlResponse response = HttpUtils.okGetForTracker(AdConstants.PortalKey.TRACK_HELPER, trackUrl, header, null, BasicAftConfig.getTrackConnectTimeout(), BasicAftConfig.getTrackReadTimeout(), BasicAftConfig.getPingRetryOnConnectionFailure());
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

}
