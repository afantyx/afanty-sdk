package com.afanty.ads.base;

import android.view.View;

import com.afanty.ads.AdSize;

public class BannerAdController extends RTBWrapper {

    public BannerAdController(String tagId, RTBBaseAd rtbAd) {
        super(tagId, rtbAd);
    }

    public AdSize getAdSize() {
        return mRTBAd instanceof IBannerAd ? ((IBannerAd) mRTBAd).getAdSize() : null;
    }

    public View getAdView() {
        View view = mRTBAd instanceof IBannerAd ? ((IBannerAd) mRTBAd).getAdView() : null;
        if (view != null)
            onImpression();
        hasShown = true;
        return view;
    }

    public int getRefreshInterval() {
        int time  = mRTBAd instanceof IBannerAd ? ((IBannerAd) mRTBAd).getRefreshInterval() : 0;
        return time;
    }

}
