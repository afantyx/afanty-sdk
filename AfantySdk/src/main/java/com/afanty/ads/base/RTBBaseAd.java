package com.afanty.ads.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AdSize;

import java.util.Map;


public abstract class RTBBaseAd {
    protected String mTagId;
    protected Context mContext;
    private IAdObserver.AdLoadInnerListener mAdLoadInnerListener;
    private IAdObserver.AdEventListener mAdEventListener;
    private boolean mRewardedAdHasComplete;
    protected boolean hasShown;
    private final static int MSG_TIMEOUT = 1001;
    private final Handler mHandler;
    protected long TIMEOUT_DEF = 15 * 1000;

    public RTBBaseAd(Context context, String tagId) {
        this.mTagId = tagId;
        this.mContext = context;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null && msg.what == MSG_TIMEOUT) {
                    onAdLoadError(AdError.TIMEOUT_ERROR);
                }
            }
        };
    }

    public String getTagId() {
        return mTagId;
    }

    public void setAdLoadListener(IAdObserver.AdLoadInnerListener adLoadInnerListener) {
        this.mAdLoadInnerListener = adLoadInnerListener;
    }

    public void setAdActionListener(IAdObserver.AdEventListener adEventListener) {
        this.mAdEventListener = adEventListener;
    }

    public void setAdSize(AdSize adSize) {
    }

    public void load() {
        mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, TIMEOUT_DEF);
        try {
            innerLoad();
        } catch (Exception throwable) {
            onAdLoadError(new AdError(AdError.UNKNOWN_ERROR.getErrorCode(), throwable.getMessage()));
        }
    }

    public void onAdLoaded(RTBWrapper ad) {
        if (ad == null) {
            onAdLoadError(AdError.NO_FILL);
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        if (mAdLoadInnerListener != null) mAdLoadInnerListener.onAdLoaded(mTagId, ad);
    }

    protected void onAdLoadError(AdError adError) {
        mHandler.removeCallbacksAndMessages(null);

        if (mAdLoadInnerListener != null) mAdLoadInnerListener.onAdLoadError(mTagId, adError);
    }

    protected abstract void innerLoad();

    public boolean isValid() {
        return !hasShown && isAdReady();
    }

    protected void notifyAdAction(IAdObserver.AdEvent adEvent) {
        notifyAdAction(adEvent, null);
    }

    protected void notifyAdAction(IAdObserver.AdEvent adEvent, Map<String, Object> extraParams) {
        if (mAdEventListener == null) {
            return;
        }
        switch (adEvent) {
            case AD_ACTION_IMPRESSION_ERROR:
                AdError adError = AdError.UNKNOWN_ERROR;
                if (extraParams != null && extraParams.get("adError") instanceof AdError) {
                    adError = (AdError) (extraParams.get("adError"));
                }
                mAdEventListener.onAdImpressionError(adError);
                break;
            case AD_ACTION_IMPRESSION:
                mAdEventListener.onAdImpression();
                break;
            case AD_ACTION_CLICKED:
                mAdEventListener.onAdClicked();
                break;
            case AD_ACTION_COMPLETE:
                mAdEventListener.onAdCompleted();
                mRewardedAdHasComplete = true;
                break;
            case AD_ACTION_CLOSED:
                mAdEventListener.onAdClosed(mRewardedAdHasComplete);
                destroy();
                break;
            default:
        }

    }

    public void resetFullAdHasComplete() {
        this.mRewardedAdHasComplete = false;
    }

    public abstract AdStyle getAdStyle();

    protected void destroy() {
    }

    public abstract boolean isAdReady();

}
