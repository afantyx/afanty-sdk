package com.afanty.internal.reward;

import android.content.ActivityNotFoundException;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.internal.FullAdActivity;
import com.afanty.internal.base.BaseHandleLoader;
import com.afanty.internal.fullscreen.BaseFullScreenAd;
import com.afanty.internal.fullscreen.FullScreenAdListener;
import com.afanty.internal.fullscreen.RewardAdFactory;
import com.afanty.internal.internal.CreativeType;
import com.afanty.request.BaseRTBRequest;
import com.afanty.request.RTBNativeRequest;
import com.afanty.utils.AdDataUtils;
import com.afanty.vast.VastManager;
import com.afanty.vast.VastVideoConfig;

public class RewardAdLoader extends BaseHandleLoader {
    private FullScreenAdListener mAdListener;
    private BaseFullScreenAd mBaseRewarded;

    protected RewardAdLoader(Context context, String tagId) {
        super(context, tagId);
    }

    public void setRewardedAdListener(@NonNull FullScreenAdListener adListener) {
        this.mAdListener = adListener;
    }

    @Override
    protected void onAdLoaded() {
        mBaseRewarded = RewardAdFactory.getInstance().getFullScreenAd(getBid().getCreativeType());
        if (mBaseRewarded == null) {
            onAdLoadError(AdError.UN_SUPPORT_TYPE_ERROR);
            return;
        }
        mBaseRewarded.setBidAndListener(getBid(), mAdListener);
        mBaseRewarded.setAdStyle(AdStyle.REWARDED_AD);

        if (!isAdReady()) {
            onAdLoadError(new AdError(AdError.ErrorCode.NO_FILL, "no Ad return"));
            return;
        }
        if (mAdListener != null) {
            mAdListener.onFullScreenAdLoaded();
        }
    }

    @Override
    protected void onAdLoadError(AdError error) {
        onFullScreenAdShowError(error);
    }

    private void onFullScreenAdShowError(AdError error) {
        if (mAdListener != null)
            mAdListener.onFullScreenAdShowError(error);
    }

    @Override
    protected void _load() {
        buildRequest().startLoad(mResponseAdRequestListener);
    }

    @Override
    protected BaseRTBRequest buildRequest() {
        return new RTBNativeRequest(mTagId);
    }

    public boolean isAdReady() {
        return getBid() != null && getBid().getAdmBean() != null;
    }

    public void show() {
        if (mContext == null) {
            return;
        }
        if (!isAdReady()) {
            onFullScreenAdShowError(new AdError(AdError.ErrorCode.NO_FILL, "No ad to show!"));
            return;
        }

        if (isAdExpired()) {
            onFullScreenAdShowError(AdError.AD_EXPIRED);
            return;
        }

        if (CreativeType.isVAST(getBid()) && AdDataUtils.getVastVideoConfig(getBid()) == null) {
            //vast offline ads/cached ads Need to re-set VastVideoConfig
            VastManager vastManager = new VastManager(mContext, true);
            vastManager.prepareVastVideoConfiguration(getBid().getVast(), new VastManager.VastManagerListener() {
                @Override
                public void onVastVideoConfigurationPrepared(@Nullable VastVideoConfig vastVideoConfig) {
                    if (null == vastVideoConfig) {
                        onFullScreenAdShowError(AdError.NO_VAST_CONTENT_ERROR);
                        return;
                    }
                    try {
                        getBid().setVastVideoConfig(vastVideoConfig);
                        startRewardActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, "", mContext);
        } else {
            startRewardActivity();
        }
    }

    private void startRewardActivity() {
        try {
            FullAdActivity.startFullScreenActivity(mContext, mBaseRewarded);
        } catch (ActivityNotFoundException ex) {
            String error = "AdRewarded not found - did you declare it in AndroidManifest.xml?";
            onFullScreenAdShowError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, error));
        } catch (Exception exception) {
            onFullScreenAdShowError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, exception.getMessage()));
        }
    }

    @Override
    protected boolean needWaitVideoDownloadFinished() {
        return true;
    }

}
