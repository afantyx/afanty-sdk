package com.afanty.ads.base;

public class FullScreenAdController extends RTBWrapper {

    public FullScreenAdController(String tagId, RTBBaseAd rtbBaseAd) {
        super(tagId, rtbBaseAd);
    }

    public void show() {
        if (mRTBAd instanceof IFullScreenAd) {
            mRTBAd.resetFullAdHasComplete();
            ((IFullScreenAd) mRTBAd).show();
            onImpression();
        }

        hasShown = true;
    }

}
