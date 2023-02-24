package com.afanty.internal.banner;

import android.view.View;

import com.afanty.ads.AdError;

public interface BannerLoaderInterface {
    void onAdBannerSuccess(View view);

    void onAdBannerFailed(AdError adError);

    void onAdBannerClicked();
}
