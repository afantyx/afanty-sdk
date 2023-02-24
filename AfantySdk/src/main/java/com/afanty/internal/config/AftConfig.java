package com.afanty.internal.config;

public class AftConfig {

    public static int getInterstitialClickArea() {
        return 1;
    }

    public static int getImpressionMinTimeViewed() {
        return 100;
    }

    public static int getImpressionMinPercentageViewed() {
        return 50;
    }

    public static Integer getImpressionMinVisiblePx() {
        return null;
    }

    public static Integer getBannerImpressionMinVisiblePx() {
        return 1;
    }

    public static int getAftVastDownloadTimeOut() {
        return  2 * 60 * 1000;
    }

    public static int getAftVideoDownloadTimeOut() {
        return  2 * 60 * 1000;
    }

    public static int getAftNativeVideoPausePercentage() {
        return  90;
    }

    public static int getAftNativeVideoResumePercentage() {
        return  100;
    }
    public static int getBtnCharacterCount() {
        return 15;
    }
}
