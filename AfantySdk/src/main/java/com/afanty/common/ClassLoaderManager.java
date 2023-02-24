package com.afanty.common;

import androidx.annotation.Nullable;

import com.afanty.ads.AdStyle;
import com.afanty.internal.banner.InnerBannerAd;
import com.afanty.internal.interstitial.InnerInterstitialAd;
import com.afanty.internal.nativead.InnerNativeAd;
import com.afanty.internal.reward.InnerRewardAd;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClassLoaderManager {
    private static final Map<String, String> loaderManager = new HashMap<String, String>() {
        @Nullable
        @Override
        public String put(String key, String value) {
            return super.put(key.toLowerCase(Locale.US), value);
        }
    };

    static {
        loaderManager.put(AdStyle.NATIVE.getName(), InnerNativeAd.class.getName());
        loaderManager.put(AdStyle.BANNER.getName(), InnerBannerAd.class.getName());
        loaderManager.put(AdStyle.INTERSTITIAL.getName(), InnerInterstitialAd.class.getName());
        loaderManager.put(AdStyle.REWARDED_AD.getName(), InnerRewardAd.class.getName());
    }

    public static String getMediationLoaderClazz(AdStyle adStyle) {
        if (adStyle == null) {
            return null;
        }
        return loaderManager.get((adStyle.getName()).toLowerCase(Locale.US));
    }

}
