package com.afanty.internal.fullscreen;

import com.afanty.ads.AdError;

public interface FullScreenAdListener {
    void onFullScreenAdLoaded();

    void onFullScreenAdFailed(AdError adError);

    void onFullScreenAdShow();

    void onFullScreenAdShowError(AdError adError);

    void onFullScreenAdClicked();

    void onFullScreenAdDismiss();

    void onFullScreenAdRewarded();
}
