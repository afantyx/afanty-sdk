package com.afanty.internal.fullscreen;


import com.afanty.internal.internal.CreativeType;

/**
 * Rewarded ad Factory
 */
public class RewardAdFactory {
    private static RewardAdFactory sInstance;

    private RewardAdFactory() {
    }

    public static RewardAdFactory getInstance() {
        if (sInstance == null) {
            synchronized (RewardAdFactory.class) {
                if (sInstance == null)
                    sInstance = new RewardAdFactory();
            }
        }
        return sInstance;
    }

    public BaseFullScreenAd getFullScreenAd(int creativeType) {
        switch (creativeType) {
            case CreativeType.TYPE_V_V1P2T2:
            case CreativeType.TYPE_PT_P2T2:
            case CreativeType.TYPE_V_V1:
            case CreativeType.TYPE_V_V1P1T1:
                return new VideoFullScreenAd();
            case CreativeType.TYPE_P_P1:
                return new SingleImageFullScreenAd();
            default:
                return null;
        }
    }
}
