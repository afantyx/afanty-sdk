package com.afanty.internal.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.afanty.request.BaseRTBRequest;
import com.afanty.ads.AdError;
import com.afanty.ads.AdSize;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.banner.render.BannerFactory;
import com.afanty.internal.base.BaseHandleLoader;
import com.afanty.internal.config.AftConfig;
import com.afanty.request.RTBBannerRequest;
import com.afanty.utils.DensityUtils;
import com.afanty.utils.log.Logger;

public class BannerAdLoader extends BaseHandleLoader {
    private static final String TAG = "RTB.Banner";
    private BannerVisibilityTracker mVisibilityTracker;
    private final AdView mAdView;
    private AdSize mAdSize;
    private AdView.BannerAdListener mBannerAdListener;

    protected BannerAdLoader(Context context, String tagId) {
        super(context, tagId);
        mAdView = new AdView(mContext);
    }

    public void setBannerAdListener(@NonNull AdView.BannerAdListener bannerAdListener) {
        this.mBannerAdListener = bannerAdListener;

    }

    @Override
    protected void onAdLoaded() {
        Logger.d(TAG, "#onAdLoaded");
        if (getBid() != null) {
            internalLoad();
        } else {
            onAdLoadError(AdError.INTERNAL_ERROR);
        }
    }

    @Override
    protected void onAdLoadError(AdError error) {
        Logger.d(TAG, "#onAdLoadError:" + error.toString());
        mBannerAdListener.onBannerFailed(mAdView, error);
    }

    private void internalLoad() {
        final AbsBaseBanner bannerAd = BannerFactory.getInstance().getBanner(getBid().getCreativeType());
        if (getBid() == null) {
            return;
        }
        if (bannerAd != null) {
            bannerAd.loadBanner(mContext, mAdSize, mAdView, getBid(), new BannerLoaderInterface() {
                @Override
                public void onAdBannerSuccess(final View view) {
                    mBannerAdListener.onBannerLoaded(mAdView);
                    initVisibilityTracker(view);
                    Logger.d(TAG, "#onAdBannerSuccess");
                }

                private void initVisibilityTracker(final View view) {
                    mVisibilityTracker = new BannerVisibilityTracker(mContext, mAdView, view,
                            AftConfig.getBannerImpressionMinVisiblePx(), AftConfig.getImpressionMinTimeViewed());
                    mVisibilityTracker.setBannerVisibilityTrackerListener(
                            () -> {
                                Logger.d(TAG, "#onVisibilityChanged show");
                                mBannerAdListener.onImpression(mAdView);
                                ActionUtils.increaseShowCount(getBid());
                            });
                }

                @Override
                public void onAdBannerFailed(AdError adError) {
                    onAdLoadError(adError);
                }

                @Override
                public void onAdBannerClicked() {
                    mBannerAdListener.onBannerClicked(mAdView);
                }
            });
        } else {
            onAdLoadError(AdError.UN_SUPPORT_TYPE_ERROR);
        }
    }


    public void setRequestedAdSize(AdSize adSize) {
        mAdSize = adSize;
        initAdViewLayoutParams(adSize);
    }

    private void initAdViewLayoutParams(AdSize adSize) {
        int width = DensityUtils.dip2px(adSize.getWidth());
        int height = DensityUtils.dip2px(adSize.getHeight());
        mAdView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

    public void onDestroy() {
        BannerFactory.getInstance().getBanner(getBid().getCreativeType()).destroy();
        mAdView.removeAllViews();
    }


    @Override
    protected void _load() {
        buildRequest().startLoad(mResponseAdRequestListener);
    }

    @Override
    protected BaseRTBRequest buildRequest() {
        return new RTBBannerRequest(mTagId);
    }

}
