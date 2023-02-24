package com.afanty.ads;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import com.afanty.ads.base.BannerAdController;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.IBannerAd;
import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.core.RTBAd;
import com.afanty.internal.banner.BannerViewController;

import java.lang.ref.WeakReference;

public class RTBBannerAd extends RTBAd implements IBannerAd {
    private static final String TAG = "RTBBannerAd";
    private final BannerViewController bannerViewController;
    private final Handler handler;
    private IAdObserver.AdLoadInnerListener manualAdLoadInnerListener;
    private IAdObserver.AdLoadInnerListener autoRefreshAdLoadInnerListener;


    public void setAdSize(AdSize adSize) {
        this.mAdSize = adSize;
    }

    public RTBBannerAd(Context context, String tagId) {
        super(context, tagId);
        mAdSize = AdSize.BANNER;
        handler = new AutoRefreshHandler(this);
        bannerViewController = new BannerViewController(context);
        bannerViewController.setBannerWindowStatusListener(new BannerViewController.BannerWindowStatusListener() {
            @Override
            public void onVisibility() {
                triggerAutoRefresh();
            }

            @Override
            public void onInvisible() {
                stopAutoRefresh();
            }
        });
    }

    @Override
    public IAdObserver.AdLoadInnerListener createAdLoadInnerListener(boolean isAutoTrigger) {
        if (!isAutoTrigger && manualAdLoadInnerListener == null) {
            manualAdLoadInnerListener = new IAdObserver.AdLoadInnerListener() {
                @Override
                public void onAdLoaded(RTBWrapper adWrapper) {
                    mAdWrapper = adWrapper;
                    if (mAdLoadCallback != null) mAdLoadCallback.onAdLoaded(RTBBannerAd.this);
                }

                @Override
                public void onAdLoadError(AdError adError) {
                    if (mAdLoadCallback != null) mAdLoadCallback.onAdLoadError(adError);
                }
            };
        }

        if (isAutoTrigger && autoRefreshAdLoadInnerListener == null)
            autoRefreshAdLoadInnerListener = new IAdObserver.AdLoadInnerListener() {
                @Override
                public void onAdLoaded(RTBWrapper adWrapper) {
                    mAdWrapper = adWrapper;
                    if (adWrapper instanceof BannerAdController) {
                        bannerViewController.refreshBanner((BannerAdController) adWrapper);
                    }
                    triggerAutoRefresh();
                }

                @Override
                public void onAdLoadError(AdError adError) {
                    triggerAutoRefresh();
                }
            };

        return mAdLoadInnerListener = (isAutoTrigger ? autoRefreshAdLoadInnerListener : manualAdLoadInnerListener);
    }

    private void triggerAutoRefresh() {
        if (getLoadedAd() != null && getLoadedAd() instanceof BannerAdController && getLoadedAd().isAdReady()) {
            int refreshInterval = ((BannerAdController) getLoadedAd()).getRefreshInterval();
            if (refreshInterval <= 0) {
                return;
            }
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0, refreshInterval * 1000);
        }
    }

    private void stopAutoRefresh() {

        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.BANNER;
    }

    @Override
    protected RTBWrapper getLoadedAd() {
        return mAdWrapper;
    }

    @Override
    public AdSize getAdSize() {
        return mAdSize;
    }

    @Override
    public View getAdView() {
        if (bannerViewController.isReady() && bannerViewController.hasAdWrapperChanged(mAdWrapper)) {
            return bannerViewController;
        }
        if (mAdWrapper != null && mAdWrapper instanceof BannerAdController && mAdWrapper.isAdReady()) {
            bannerViewController.setAdActionListener(getAdActionListener());
            bannerViewController.setBannerAdWrapper((BannerAdController) mAdWrapper);
        }
        return bannerViewController;
    }

    @Override
    public int getRefreshInterval() {
        return 0;
    }

    private static class AutoRefreshHandler extends Handler {
        WeakReference<RTBBannerAd> aftBannerWeakReference;

        AutoRefreshHandler(RTBBannerAd rtbBannerAd) {
            aftBannerWeakReference = new WeakReference<>(rtbBannerAd);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (aftBannerWeakReference == null || aftBannerWeakReference.get() == null)
                return;
            RTBBannerAd rtbBanner = aftBannerWeakReference.get();
            if (!isVisible(rtbBanner.bannerViewController)) {
                rtbBanner.stopAutoRefresh();
                return;
            }
            rtbBanner.innerLoad(true);
        }

        private boolean isVisible(final View view) {
            if (view == null || !view.isShown()) {
                return false;
            }

            Rect actualPosition = new Rect();
            boolean isGlobalVisible = view.getGlobalVisibleRect(actualPosition);
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            Rect screen = new Rect(0, 0, screenWidth, screenHeight);
            return isGlobalVisible && Rect.intersects(actualPosition, screen);
        }
    }

}
