package com.afanty.vast;

import android.content.Context;
import android.text.TextUtils;

import com.afanty.internal.internal.AdConstants;
import com.afanty.models.Bid;
import com.afanty.utils.SettingConfig;

public class VastHelper {
    private final static String TAG = "AD.VastHelper";

    public static void rtbTryDownLoadVastXml(Context context, final Bid bid, String vastStr, boolean isPreCacheVideo, final VastParseResult vastParseResult) {
        if (bid == null || TextUtils.isEmpty(bid.getVast())) {
            if (vastParseResult != null)
                vastParseResult.onVastParseError(AdConstants.Vast.NO_VAST_CONTENT);
            return;
        }

        VastManager mVastManager = new VastManager(context, isPreCacheVideo);
        mVastManager.setAftAdId(bid.adid);
        mVastManager.setExpire(-1);
        mVastManager.prepareVastVideoConfiguration(vastStr, vastVideoConfig -> {
            if (vastVideoConfig != null && !TextUtils.isEmpty(vastVideoConfig.getDiskMediaFileUrl())) {
                String adcId = bid.adid + bid.cid;
                bid.setVastVideoConfig(vastVideoConfig);
                SettingConfig.setCachedVastUrlByAdcId(adcId, vastVideoConfig.getNetworkMediaFileUrl());
                if (vastParseResult != null)
                    vastParseResult.onVastParseSuccess();
            } else {
                if (vastParseResult != null)
                    vastParseResult.onVastParseError("DownLoadVastXml## video download failed or there is no video");
            }
        }, "", context);
    }


    public static void rtbTryParseVastXml(Context context, final Bid bid, String vastStr, final VastParseResult vastParseResult) {
        if (bid == null || bid.getAdmBean() == null || TextUtils.isEmpty(vastStr)) {
            vastParseResult.onVastParseSuccess();
            return;
        }
        VastManager mVastManager = new VastManager(context, true);
        mVastManager.setAftAdId(bid.adid);
        mVastManager.setExpire(-1);
        mVastManager.prepareVastVideoConfiguration(vastStr, vastVideoConfig -> {
                    try {
                        for (VastMediaXmlManager vastMediaXmlManager : vastVideoConfig.getMediaFiles()) {
                            if (vastMediaXmlManager == null)
                                continue;
                            if (vastMediaXmlManager.getMediaUrl() != null && vastMediaXmlManager.getMediaUrl().equals(vastVideoConfig.getNetworkMediaFileUrl())) {
                                bid.setVastPlayUrl(vastMediaXmlManager.getMediaUrl());
                                if (vastMediaXmlManager.getWidth() != null)
                                    bid.setWidth(vastMediaXmlManager.getWidth());
                                if (vastMediaXmlManager.getHeight() != null)
                                    bid.setHeight(vastMediaXmlManager.getHeight());


                            }
                        }

                        for (VastTracker impressionTrackers : vastVideoConfig.getImpressionTrackers()) {
                            if (impressionTrackers == null)
                                continue;
                            if (!TextUtils.isEmpty(impressionTrackers.getContent())) {
                                bid.appendTrackImpressionUrls(impressionTrackers.getContent());
                            }
                        }

                        for (VastTracker fractionalTrackers : vastVideoConfig.getFractionalTrackers()) {
                            if (fractionalTrackers == null)
                                continue;
                        }

                        for (VastTracker clickTrackers : vastVideoConfig.getClickTrackers()) {
                            if (clickTrackers == null)
                                continue;
                        }

                        for (VastTracker absoluteTrackers : vastVideoConfig.getAbsoluteTrackers()) {
                            if (absoluteTrackers == null)
                                continue;
                        }

                        for (VastTracker closetrackers : vastVideoConfig.getCloseTrackers()) {
                            if (closetrackers == null)
                                continue;
                        }

                        for (VastTracker completeTrackers : vastVideoConfig.getCompleteTrackers()) {
                            if (completeTrackers == null)
                                continue;
                        }


                        if (!TextUtils.isEmpty(vastVideoConfig.getAdTitle())) {
                            if (bid.getAdmBean() != null && bid.getAdmBean().getTitleBean() != null) {
                                bid.getAdmBean().getTitleBean().setText(vastVideoConfig.getAdTitle());
                            }
                        }


                        bid.setVastVideoConfig(vastVideoConfig);

                        vastParseResult.onVastParseSuccess();
                    } catch (Exception e) {
                        vastParseResult.onVastParseError(e.getMessage());
                    }
                },
                "", context);
    }

    private static boolean isTracker(String url) {
        return !TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"));
    }

    private static Long vastDurationToLong(String vastDuration) {
        Long totalSecond = 0L;
        String[] time = vastDuration.split(":");
        for (int index = time.length - 1; index >= 0; index--) {
            totalSecond = totalSecond + Long.parseLong(time[index]) * (long) Math.pow(60, (time.length - 1 - index));
        }
        return totalSecond;
    }

    public interface VastParseResult {
        void onVastParseError(String errorMessage);

        void onVastParseSuccess();
    }
}
