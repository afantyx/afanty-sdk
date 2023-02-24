package com.afanty.config;

public class BasicAftConfig {
    private static final String TAG = "AftConfig";

    public static int getDownloadImagesTimeout() {
        return 30000;
    }

    public static boolean checkVideoHasReady() {
        return true;
    }

    public static boolean handleMarketSchema() {
        return false;
    }

    public static int getTrackConnectTimeout() {
        return 30000;
    }

    public static int getTrackReadTimeout() {
        return 30000;
    }

    public static boolean getPingRetryOnConnectionFailure() {
        return true;
    }

    public static int getAftPingRetryCount() {
        return 5;
    }

    public static boolean isUseHttpRedirect() {
        return false;
    }

    public static int getJumpMarketDelayTime() {
        return 3000;
    }

    public static long getAppListStatsBeginTime() {
        return  10 * 24 * 60 * 60 * 1000;
    }

    /**
     * Get App Dns Resolution Service Status Cloud Control Configuration
     */
    public static boolean getAppDnsSwitch() {
        return false;
    }

    public static long getSiAdInfoValidDuration() {
        return  1000 * 60 * 60 * 24 * 7;
    }
}
