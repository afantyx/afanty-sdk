package com.afanty.internal.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.base.BannerAdController;
import com.afanty.ads.base.IAdObserver;

public class BannerViewController extends FrameLayout {
    private static final String TAG = "RTBBannerController";
    private BannerAdController mAdWrapper;
    private BannerWindowStatusListener bannerWindowStatusListener;
    private IAdObserver.AdEventListener adEventListener;

    public BannerViewController(@NonNull Context context) {
        super(context);
    }

    public BannerViewController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateBannerView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            if (bannerWindowStatusListener != null) {
                bannerWindowStatusListener.onVisibility();
            }
        } else if (visibility == INVISIBLE || visibility == GONE) {
            if (bannerWindowStatusListener != null) {
                bannerWindowStatusListener.onInvisible();
            }
        }
    }

    public void refreshBanner(BannerAdController bannerAdController) {
        setBannerAdWrapper(bannerAdController);
        updateBannerView();
    }

    private void updateBannerView() {
        if (!isReady())
            return;
        this.removeAllViews();
        mAdWrapper.setAdActionListener(adEventListener);
        this.addView(getAdView());
    }

    private View getAdView() {
        if (mAdWrapper != null && mAdWrapper instanceof BannerAdController) {
            return mAdWrapper.getAdView();
        }
        return null;
    }

    public boolean hasAdWrapperChanged(RTBWrapper adWrapper) {
        return adWrapper == this.mAdWrapper;
    }

    public void setAdActionListener(IAdObserver.AdEventListener adEventListener) {
        this.adEventListener = adEventListener;
    }

    public void setBannerAdWrapper(BannerAdController bannerAdController) {
        mAdWrapper = bannerAdController;
    }

    public boolean isReady() {
        return mAdWrapper != null && mAdWrapper.isValid();
    }

    public void setBannerWindowStatusListener(BannerWindowStatusListener bannerWindowStatusListener) {
        this.bannerWindowStatusListener = bannerWindowStatusListener;
    }

    public interface BannerWindowStatusListener {
        void onVisibility();

        void onInvisible();
    }

}
