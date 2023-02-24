package com.afanty.ads.base;

import android.view.View;

import com.afanty.ads.AdSize;

public interface IBannerAd {

    AdSize getAdSize();

    View getAdView();

    int getRefreshInterval();
}
