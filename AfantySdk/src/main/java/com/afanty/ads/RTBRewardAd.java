package com.afanty.ads;

import android.content.Context;

import com.afanty.ads.base.RTBWrapper;

public class RTBRewardAd extends RTBFullScreenAd {
    public RTBRewardAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.REWARDED_AD;
    }

    @Override
    protected RTBWrapper getLoadedAd() {
        return mAdWrapper != null ? mAdWrapper : null;
    }
}
