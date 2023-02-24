package com.afanty.ads;

import android.content.Context;

import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.base.FullScreenAdController;
import com.afanty.ads.core.RTBAd;

public abstract class RTBFullScreenAd extends RTBAd {
    public RTBFullScreenAd(Context context, String tagId) {
        super(context, tagId);
    }

    public void show() {
        RTBWrapper loadedAd = getLoadedAd();
        if (loadedAd != null && loadedAd instanceof FullScreenAdController && loadedAd.isAdReady()) {
            loadedAd.setAdActionListener(getAdActionListener());
            ((FullScreenAdController) loadedAd).show();
        } else if (getAdActionListener() != null) {
            getAdActionListener().onAdImpressionError(AdError.NO_FILL);
        }
    }
}
