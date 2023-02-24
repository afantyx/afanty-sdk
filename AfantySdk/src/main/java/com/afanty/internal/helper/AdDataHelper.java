package com.afanty.internal.helper;

import android.text.TextUtils;

import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;

import java.util.ArrayList;
import java.util.List;

public class AdDataHelper {
    private static final String TAG = "AdDataHelper";
    private static final String MACRO_VIEW_ID = "__VIEWID__";

    public static List<String> getReplaceMacroSiteUrls(List<String> urls, Bid adData) {
        if (adData == null || urls == null || urls.isEmpty())
            return urls;
        List<String> replaceSizeUrls = new ArrayList<>();
        for (String url : urls) {
            url = AdsUtils.replaceMacroUrlsForSite(url, adData.getPlacementId(), adData.getSid());
            url = AdsUtils.replaceMarcoUrls(url, MACRO_VIEW_ID, adData.getViewId());
            replaceSizeUrls.add(url);
        }
        return replaceSizeUrls;
    }


    private static int getIntResolution(String resolution) {
        if (AdmBean.VideoBean.DASH_RESOLUTION.equals(resolution))
            return 0;
        return Integer.parseInt(resolution.split("p")[0]);
    }

    public static int getVastResolution(String resolution) {
        if (AdmBean.VideoBean.DASH_RESOLUTION.equals(resolution))
            return 0;
        String[] returns = resolution.split("p")[1].split("/");
        try {
            return Integer.parseInt(returns[returns.length - 1]);
        } catch (Exception e) {
            return 0;
        }
    }




    public static AdmBean.VideoBean getRTBMaxResolution(List<AdmBean.VideoBean> videoSourceList) {
        int maxResolution = 0;
        AdmBean.VideoBean maxVideoSource = null;
        if (videoSourceList == null || videoSourceList.size() <= 0)
            return null;

        for (AdmBean.VideoBean videoSource : videoSourceList) {
            if (videoSource == null || TextUtils.isEmpty(videoSource.getResolution()) || videoSource.getResolution().equals(AdmBean.VideoBean.DASH_RESOLUTION))
                continue;

            int resolution = getIntResolution(videoSource.getResolution());
            if (resolution >= maxResolution) {
                maxResolution = resolution;
                maxVideoSource = videoSource;
            }
        }
        return maxVideoSource;
    }

    public static AdmBean.VideoBean getRTBMinResolution(List<AdmBean.VideoBean> videoSourceList) {
        int minResolution = Integer.MAX_VALUE;
        AdmBean.VideoBean minVideoSource = null;
        if (videoSourceList == null || videoSourceList.size() <= 0)
            return null;

        for (AdmBean.VideoBean videoSource : videoSourceList) {
            if (videoSource == null || TextUtils.isEmpty(videoSource.getResolution()) || videoSource.getResolution().equals(AdmBean.VideoBean.DASH_RESOLUTION))
                continue;

            int resolution = getIntResolution(videoSource.getResolution());
            if (resolution <= minResolution) {
                minResolution = resolution;
                minVideoSource = videoSource;
            }
        }
        return minVideoSource;
    }

}
