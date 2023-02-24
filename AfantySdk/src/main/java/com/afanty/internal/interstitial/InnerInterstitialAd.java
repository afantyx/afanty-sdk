package com.afanty.internal.interstitial;

import android.content.Context;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.base.FullScreenAdController;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.IFullScreenAd;
import com.afanty.ads.base.RTBBaseAd;
import com.afanty.internal.fullscreen.FullScreenAdListener;
import com.afanty.utils.log.Logger;

public class InnerInterstitialAd extends RTBBaseAd implements IFullScreenAd {
    private static final String TAG = "RTB.Interstitial";
    private InterstitialAdLoader mInterstitialLoader;

    public InnerInterstitialAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    protected void innerLoad() {
        if (mInterstitialLoader == null)
            mInterstitialLoader = new InterstitialAdLoader(mContext, mTagId);

        mInterstitialLoader.setInterstitialAdListener(new FullScreenAdListener() {
            @Override
            public void onFullScreenAdLoaded() {
                onAdLoaded(new FullScreenAdController(getTagId(), InnerInterstitialAd.this));
            }

            @Override
            public void onFullScreenAdFailed(AdError adError) {
                onAdLoadError(adError);
            }

            @Override
            public void onFullScreenAdShow() {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_IMPRESSION);
            }

            @Override
            public void onFullScreenAdShowError(AdError adError) {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_IMPRESSION_ERROR);
            }

            @Override
            public void onFullScreenAdClicked() {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_CLICKED);
            }

            @Override
            public void onFullScreenAdDismiss() {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_CLOSED);
            }

            @Override
            public void onFullScreenAdRewarded() {

            }
        });
        mInterstitialLoader.loadAd();
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.INTERSTITIAL;
    }

    @Override
    public boolean isAdReady() {
        return mInterstitialLoader != null && mInterstitialLoader.isAdReady();
    }

    @Override
    public void show() {
        Logger.i(TAG, "Interstitial show, isReady = " + isAdReady() + ", mTagId = " + mTagId);
        if (isAdReady()) {
            mInterstitialLoader.show();
        }
    }

    @Override
    protected void destroy() {
        if (mInterstitialLoader != null)
            mInterstitialLoader.destroy();
    }
}
