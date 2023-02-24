package com.afanty.ads.base;

import androidx.annotation.NonNull;

public class RTBWrapper {

    protected RTBBaseAd mRTBAd;
    private final String mTagId;
    protected boolean hasShown;

    public RTBWrapper(String tagId, RTBBaseAd rtbBaseAd) {
        this.mTagId = tagId;
        this.mRTBAd = rtbBaseAd;
    }

    public RTBBaseAd getRTBAd() {
        return mRTBAd;
    }

    public void setRtbAd(RTBBaseAd rtbAd) {
        this.mRTBAd = rtbAd;
    }

    public void setAdActionListener(IAdObserver.AdEventListener adEventListener) {
        if (mRTBAd != null)
            mRTBAd.setAdActionListener(adEventListener);
    }
    
    public boolean isValid() {
        return !hasShown && isAdReady();
    }

    public boolean isAdReady() {
        return mRTBAd != null && mRTBAd.isAdReady();
    }

    @NonNull
    public String getTagId() {
        return mTagId;
    }

    public void onImpression() {
        hasShown = true;
    }

}
