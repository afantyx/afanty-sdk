package com.afanty.internal.reward;

import android.content.Context;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.base.FullScreenAdController;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.IFullScreenAd;
import com.afanty.ads.base.RTBBaseAd;
import com.afanty.internal.fullscreen.FullScreenAdListener;
import com.afanty.utils.log.Logger;

public class InnerRewardAd extends RTBBaseAd implements IFullScreenAd {
    private static final String TAG = "RTB.Reward";
    private RewardAdLoader mRewardAdLoader;

    public InnerRewardAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    protected void innerLoad() {
        if (mRewardAdLoader == null) {
            mRewardAdLoader = new RewardAdLoader(mContext, mTagId);
        }
        mRewardAdLoader.setRewardedAdListener(new FullScreenAdListener() {
            @Override
            public void onFullScreenAdLoaded() {
                onAdLoaded(new FullScreenAdController(getTagId(), InnerRewardAd.this));
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
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_COMPLETE);
            }
        });

        mRewardAdLoader.loadAd();
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.REWARDED_AD;
    }

    @Override
    public boolean isAdReady() {
        return mRewardAdLoader != null && mRewardAdLoader.isAdReady();
    }

    @Override
    public void show() {
        Logger.i(TAG, "#show() isAdReady = " + isAdReady() + ", tagId = " + mTagId);
        if (isAdReady())
            mRewardAdLoader.show();
    }

    @Override
    protected void destroy() {
        if (mRewardAdLoader != null)
            mRewardAdLoader.destroy();
    }

}
