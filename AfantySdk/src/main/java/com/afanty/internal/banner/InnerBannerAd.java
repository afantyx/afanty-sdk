package com.afanty.internal.banner;

import android.content.Context;
import android.view.View;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AdSize;
import com.afanty.ads.base.BannerAdController;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.IBannerAd;
import com.afanty.ads.base.RTBBaseAd;

public class InnerBannerAd extends RTBBaseAd implements IBannerAd {
    private static final String TAG = "RTB.InnerBannerAd";
    private BannerAdLoader mBannerAdLoader;
    private AdSize mAdSize = AdSize.BANNER;
    private AdView mAdView;

    public InnerBannerAd(Context context, String tagId) {
        super(context, tagId);
    }

    public void setAdSize(AdSize adSize) {
        this.mAdSize = adSize;
    }

    @Override
    protected void innerLoad() {
        mBannerAdLoader = new BannerAdLoader(mContext, mTagId);
        mBannerAdLoader.setRequestedAdSize(mAdSize);
        mBannerAdLoader.setBannerAdListener(new AdView.BannerAdListener() {
            @Override
            public void onBannerLoaded(AdView banner) {
                mAdView = banner;
                onAdLoaded(new BannerAdController(getTagId(), InnerBannerAd.this));
            }

            @Override
            public void onBannerFailed(AdView banner, AdError adError) {
                onAdLoadError(adError);
            }

            @Override
            public void onBannerClicked(AdView banner) {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_CLICKED);
            }

            @Override
            public void onImpression(AdView banner) {
                notifyAdAction(IAdObserver.AdEvent.AD_ACTION_IMPRESSION);
            }
        });
        mBannerAdLoader.loadAd();
    }

    @Override
    public boolean isAdReady() {
        if (mBannerAdLoader.isAdExpired()) {
            return false;
        }
        return mAdView != null;
    }


    @Override
    public AdStyle getAdStyle() {
        return AdStyle.BANNER;
    }

    @Override
    public AdSize getAdSize() {
        return mAdSize;
    }

    @Override
    public View getAdView() {
        return mAdView;
    }

    @Override
    public int getRefreshInterval() {
        if (mBannerAdLoader == null) {
            return 0;
        }
        if (mBannerAdLoader.getBid() == null) {
            return 0;
        }
        return mBannerAdLoader.getBid().getRefreshInterval();
    }

    public void onDestroy() {
        mAdView = null;
        if (mBannerAdLoader != null)
            mBannerAdLoader.onDestroy();
    }

}
