package com.afanty.internal.interstitial;

import android.content.ActivityNotFoundException;
import android.content.Context;

import androidx.annotation.NonNull;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.internal.FullAdActivity;
import com.afanty.internal.base.BaseHandleLoader;
import com.afanty.internal.fullscreen.BaseFullScreenAd;
import com.afanty.internal.fullscreen.FullScreenAdListener;
import com.afanty.internal.fullscreen.InterstitialAdFactory;
import com.afanty.request.BaseRTBRequest;
import com.afanty.request.RTBNativeRequest;

public class InterstitialAdLoader extends BaseHandleLoader {
    private static final String TAG = "RTB.InterstitialAdLoader";
    private FullScreenAdListener mAdListener;
    private BaseFullScreenAd mBaseInterstitial;

    protected InterstitialAdLoader(Context context, String tagId) {
        super(context, tagId);
    }

    public void setInterstitialAdListener(@NonNull FullScreenAdListener interstitialAdListener) {
        this.mAdListener = interstitialAdListener;
    }

    @Override
    protected void onAdLoaded() {
        mBaseInterstitial = InterstitialAdFactory.getInstance().getFullScreenAd(getBid().getCreativeType());
        if (mBaseInterstitial == null) {
            onAdLoadError(AdError.UN_SUPPORT_TYPE_ERROR);
            return;
        }
        mBaseInterstitial.setBidAndListener(getBid(), mAdListener);
        mBaseInterstitial.setAdStyle(AdStyle.INTERSTITIAL);
        if (mAdListener != null) {
            mAdListener.onFullScreenAdLoaded();
        }
    }

    @Override
    protected void onAdLoadError(AdError error) {
        if (mAdListener != null) {
            mAdListener.onFullScreenAdFailed(error);
        }
    }

    @Override
    protected void _load() {
        buildRequest().startLoad(mResponseAdRequestListener);
    }
    @Override
    protected BaseRTBRequest buildRequest() {
        return new RTBNativeRequest(mTagId,true);
    }

    public void show() {
        if (mContext == null) {
            return;
        }
        if (!isAdReady()) {
            mAdListener.onFullScreenAdShowError(new AdError(AdError.ErrorCode.NO_FILL, "No ad to show!"));
            return;
        }

        if (isAdExpired()) {
            mAdListener.onFullScreenAdShowError(AdError.AD_EXPIRED);
            return;
        }

        try {
            FullAdActivity.startFullScreenActivity(mContext, mBaseInterstitial);
        } catch (ActivityNotFoundException ex) {
            String error = "Activity not found - did you declare it in AndroidManifest.xml?";
            mAdListener.onFullScreenAdShowError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, error));
        } catch (Exception exception) {
            mAdListener.onFullScreenAdShowError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, exception.getMessage()));
        }
    }

    @Override
    protected boolean needWaitVideoDownloadFinished() {
        return true;
    }

    public boolean isAdReady() {
        return getBid() != null && getBid().getAdmBean() != null;
    }
}
