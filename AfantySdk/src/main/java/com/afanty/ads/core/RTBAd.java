package com.afanty.ads.core;

import android.content.Context;
import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AdSize;
import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.base.IAdObserver;

public abstract class RTBAd {
    private static final String TAG = "RTBAd";
    protected String mTagId;
    protected Context mContext;
    protected RTBWrapper mAdWrapper;
    protected AdStyle mAdStyle;
    protected AdSize mAdSize;
    protected BaseAdLoadHelper mAdLoaderManager;

    protected IAdObserver.AdLoadInnerListener mAdLoadInnerListener;
    protected IAdObserver.AdLoadCallback mAdLoadCallback;
    private IAdObserver.AdEventListener mAdEventListener;
    protected IAdObserver.AdEventListener mObserverAdEventListener;

    public RTBAd(Context context, String tagId) {
        this.mTagId = tagId;
        this.mContext = context;
    }

    public void setAdLoadListener(IAdObserver.AdLoadCallback adLoadCallback) {
        this.mAdLoadCallback = adLoadCallback;
    }

    public void setAdActionListener(IAdObserver.AdEventListener adEventListener) {
        mObserverAdEventListener = adEventListener;
    }

    public void load() {
        innerLoad();
    }

    protected void innerLoad() {
        innerLoad(false);
    }

    protected void innerLoad(boolean isAutoTrigger) {
        if (mAdLoaderManager == null)
            mAdLoaderManager = AdLoadHelperStorage.getAdLoadHelper(mContext, mTagId);
        if (mAdLoaderManager == null) {
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onAdLoadError(AdError.PARAMETER_ERROR);
            }
            return;
        }
        if (mAdStyle == null) mAdStyle = getAdStyle();

        if (mAdStyle == null) {
            if (mAdLoadCallback != null) {
                mAdLoadCallback.onAdLoadError(AdError.PARAMETER_ERROR);
            }
            return;
        }
        doStartLoad(isAutoTrigger);
    }

    protected void doStartLoad() {
        doStartLoad(false);
    }

    protected void doStartLoad(boolean isAutoTrigger) {
        mAdLoaderManager.setAdStyle(getAdStyle())
                .setAdListener(createAdLoadInnerListener(isAutoTrigger))
                .setAdSize(mAdSize)
                .doStartLoad();
    }

    protected IAdObserver.AdLoadInnerListener createAdLoadInnerListener(boolean isAutoTrigger) {
        if (mAdLoadInnerListener == null)
            mAdLoadInnerListener = new IAdObserver.AdLoadInnerListener() {
                @Override
                public void onAdLoaded(RTBWrapper adWrapper) {
                    mAdWrapper = adWrapper;
                    if (mAdLoadCallback != null) mAdLoadCallback.onAdLoaded(RTBAd.this);
                }

                @Override
                public void onAdLoadError(AdError adError) {
                    if (mAdLoadCallback != null) mAdLoadCallback.onAdLoadError(adError);
                }
            };
        return mAdLoadInnerListener;
    }

    protected IAdObserver.AdEventListener getAdActionListener() {
        if (mAdEventListener == null)
            mAdEventListener = new IAdObserver.AdEventListener() {
                @Override
                public void onAdImpressionError(AdError error) {
                    if (mObserverAdEventListener != null)
                        mObserverAdEventListener.onAdImpressionError(error);
                }

                @Override
                public void onAdImpression() {
                    if (mObserverAdEventListener != null)
                        mObserverAdEventListener.onAdImpression();

                }

                @Override
                public void onAdClicked() {
                    if (mObserverAdEventListener != null)
                        mObserverAdEventListener.onAdClicked();
                }

                @Override
                public void onAdCompleted() {
                    if (mAdStyle == AdStyle.REWARDED_AD)
                        if (mObserverAdEventListener != null)
                            mObserverAdEventListener.onAdCompleted();
                }

                @Override
                public void onAdClosed(boolean hasRewarded) {
                    IAdObserver.AdEventListener adEventListener = mObserverAdEventListener == null ? null : mObserverAdEventListener;
                    AdLoadHelperStorage.removeAdLoadHelper(mTagId);
                    if (adEventListener != null) {
                        adEventListener.onAdClosed(hasRewarded);
                    }
                }
            };
        return mAdEventListener;
    }

    public abstract AdStyle getAdStyle();

    public void destroy() {
        mContext = null;
        mAdLoadCallback = null;
        mAdEventListener = null;

        AdLoadHelperStorage.removeAdLoadHelper(mTagId);
    }

    public boolean isAdReady() {
        return getLoadedAd() != null;
    }

    protected abstract RTBWrapper getLoadedAd();
}
