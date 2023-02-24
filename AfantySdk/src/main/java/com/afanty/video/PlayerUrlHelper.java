package com.afanty.video;

import android.os.Bundle;
import android.text.TextUtils;

import com.afanty.internal.helper.AdDataHelper;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.SourceDownloadUtils;
import com.afanty.vast.VastMediaXmlManager;
import com.afanty.vast.VastVideoConfig;
import com.afanty.vast.VastXmlManagerAggregator;

import java.util.List;

public class PlayerUrlHelper {

    private static final String TAG = "AD.Video.Dash";

    public static String getVideoPlayUrl(Bid bid) {
        if (bid == null)
            return null;
        List<AdmBean.VideoBean> videos = bid.getVideos();
        if (videos == null || videos.size() <= 0)
            return null;

        int maxResolution = 0;
        String downloadUrl = "";
        for (AdmBean.VideoBean videoSource : videos) {
            if (videoSource == null || TextUtils.isEmpty(videoSource.getResolution()) || AdmBean.VideoBean.DASH_RESOLUTION.equals(videoSource.getResolution()))
                continue;

            if (SourceDownloadUtils.hasDownloadVideo(videoSource.getUrl()) && getIntResolution(videoSource.getResolution()) >= maxResolution) {
                maxResolution = getIntResolution(videoSource.getResolution());
                downloadUrl = videoSource.getUrl();
            }
        }

        if (!TextUtils.isEmpty(downloadUrl)) {
            return downloadUrl;
        } else if (bid != null && SourceDownloadUtils.hasDownloadVideo(bid.getVastPlayUrl())) {
            return bid.getVastPlayUrl();
        } else if (!TextUtils.isEmpty(getDashUrl(bid))) {
            return getDashUrl(bid);
        } else
            return "";
    }

    public static String getDashUrl(Bid bid) {
        return getVideoDownloadUrl(bid, false);
    }

    public static String getVideoDownloadUrl(Bid bid, boolean isFlash) {
        if (bid == null) {
            return null;
        }
        AdmBean adm = bid.getAdmBean();
        if (adm == null) {
            return null;
        }
        boolean isStrongNetwork = getNetStatus(isFlash);

        String url;
        if (!isStrongNetwork) {
            url = adm.getMinResolutionVideo() != null ? adm.getMinResolutionVideo().getUrl() : null;
            return url;
        } else {
            url = adm.getMaxResolutionVideo() != null ? adm.getMaxResolutionVideo().getUrl() : null;
            return url;
        }
    }

    /**
     * return true if network is strong
     */
    public static boolean getNetStatus(boolean isFlash) {
        return isPingGoodNet(isFlash);
    }

    public static int getIntResolution(String resolution) {
        if (AdmBean.VideoBean.DASH_RESOLUTION.equals(resolution))
            return 0;
        return Integer.parseInt(resolution.split("p")[0]);
    }

    public static boolean isPingGoodNet(boolean isFlash) {
        //TODO ping good net
        return true;
    }

    public static Bundle getVastVideoDownloadUrl(VastVideoConfig vastVideoConfig) {
        Bundle bestMediaFileUrl = new Bundle();
        String minUrl = null, maxUrl = null;
        int minWidth = -1, maxWidth = -1;
        int minHeight = -1, maxHeight = -1;

        int minResolution = Integer.MAX_VALUE, maxResolution = 0;
        for (VastMediaXmlManager vastMediaXmlManager : vastVideoConfig.getMediaFiles()) {
            if (vastMediaXmlManager == null)
                continue;
            if (!TextUtils.isEmpty(vastMediaXmlManager.getMediaUrl())) {
                if (AdDataHelper.getVastResolution(vastMediaXmlManager.getMediaUrl()) > maxResolution) {
                    maxResolution = AdDataHelper.getVastResolution(vastMediaXmlManager.getMediaUrl());
                    maxUrl = vastMediaXmlManager.getMediaUrl();
                    maxWidth = vastMediaXmlManager.getWidth() == null ? 0 : vastMediaXmlManager.getWidth();
                    maxHeight = vastMediaXmlManager.getHeight() == null ? 0 : vastMediaXmlManager.getHeight();
                }
                if (AdDataHelper.getVastResolution(vastMediaXmlManager.getMediaUrl()) < minResolution) {
                    minResolution = AdDataHelper.getVastResolution(vastMediaXmlManager.getMediaUrl());
                    minUrl = vastMediaXmlManager.getMediaUrl();
                    minWidth = vastMediaXmlManager.getWidth() == null ? 0 : vastMediaXmlManager.getWidth();
                    minHeight = vastMediaXmlManager.getHeight() == null ? 0 : vastMediaXmlManager.getHeight();
                }
            }
        }

        if (isPingGoodNet(false)) {
            bestMediaFileUrl.putString(VastXmlManagerAggregator.BEST_URL, maxUrl);
            bestMediaFileUrl.putInt(VastXmlManagerAggregator.BEST_WIDTH, maxWidth);
            bestMediaFileUrl.putInt(VastXmlManagerAggregator.BEST_HEIGHT, maxHeight);
        } else {
            bestMediaFileUrl.putString(VastXmlManagerAggregator.BEST_URL, minUrl);
            bestMediaFileUrl.putInt(VastXmlManagerAggregator.BEST_WIDTH, minWidth);
            bestMediaFileUrl.putInt(VastXmlManagerAggregator.BEST_HEIGHT, minHeight);
        }

        return bestMediaFileUrl;
    }
}
