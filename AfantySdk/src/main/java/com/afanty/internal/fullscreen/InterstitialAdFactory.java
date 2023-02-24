package com.afanty.internal.fullscreen;


import com.afanty.internal.internal.CreativeType;

/**
 * Interstitial Ad Factory
 */
public class InterstitialAdFactory {
    private static InterstitialAdFactory sInstance;

    private InterstitialAdFactory() {
    }

    public static InterstitialAdFactory getInstance() {
        if (sInstance == null) {
            synchronized (InterstitialAdFactory.class) {
                if (sInstance == null)
                    sInstance = new InterstitialAdFactory();
            }
        }
        return sInstance;
    }

    public BaseFullScreenAd getFullScreenAd(int creativeType) {
        switch (creativeType) {
            case CreativeType.TYPE_V_V1P2T2:
            case CreativeType.TYPE_PT_P2T2:
                return new NativeFullScreenAd();
            case CreativeType.TYPE_P_P1:
                return new SingleImageFullScreenAd();
            case CreativeType.TYPE_V_V1:
            case CreativeType.TYPE_V_V1P1T1:
                return new VideoFullScreenAd();
            default:
                return null;
        }
    }
}
