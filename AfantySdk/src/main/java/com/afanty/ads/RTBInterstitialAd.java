package com.afanty.ads;

import android.content.Context;

import com.afanty.ads.base.RTBWrapper;

public class RTBInterstitialAd extends RTBFullScreenAd {
    public RTBInterstitialAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.INTERSTITIAL;
    }

    @Override
    protected RTBWrapper getLoadedAd() {
        return mAdWrapper != null ? mAdWrapper : null;
    }
}
