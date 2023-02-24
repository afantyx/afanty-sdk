package com.afanty.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.INativeAd;
import com.afanty.ads.base.RTBBaseAd;
import com.afanty.ads.core.RTBAd;

import java.util.List;

public class RTBNativeAd extends RTBAd {
    private INativeAd mNativeAd;

    public RTBNativeAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.NATIVE;
    }

    protected void doStartLoad(boolean isAutoTrigger) {
        mAdLoaderManager.setAdStyle(getAdStyle())
                .setAdListener(new IAdObserver.AdLoadInnerListener() {
                    @Override
                    public void onAdLoaded(RTBWrapper adWrapper) {
                        if (adWrapper == null) {
                            if (mAdLoadCallback != null)
                                mAdLoadCallback.onAdLoadError(AdError.NO_FILL);
                            return;
                        }
                        mAdWrapper = adWrapper;
                        RTBBaseAd rtbAd = adWrapper.getRTBAd();
                        if (rtbAd instanceof INativeAd) {
                            mNativeAd = (INativeAd) rtbAd;
                            if (mAdLoadCallback != null)
                                mAdLoadCallback.onAdLoaded(RTBNativeAd.this);
                        } else {
                            if (mAdLoadCallback != null)
                                mAdLoadCallback.onAdLoadError(AdError.UN_SUPPORT_TYPE_ERROR);
                        }
                    }

                    @Override
                    public void onAdLoadError(AdError adError) {
                        if (mAdLoadCallback != null)
                            mAdLoadCallback.onAdLoadError(adError);
                    }
                })
                .doStartLoad();
    }

    @Override
    protected RTBWrapper getLoadedAd() {
        if (mNativeAd == null) {
            return null;
        }
        return mAdWrapper;
    }

    public String getPosterUrl() {
        return mNativeAd == null ? "" : mNativeAd.getPosterUrl();
    }

    public String getIconUrl() {
        return mNativeAd == null ? "" : mNativeAd.getIconUrl();
    }

    public String getTitle() {
        return mNativeAd == null ? "" : mNativeAd.getTitle();
    }

    public String getContent() {
        return mNativeAd == null ? "" : mNativeAd.getContent();
    }

    public String getCallToAction() {
        return mNativeAd == null ? "" : mNativeAd.getCallToAction();
    }

    public View getAdIconView() {
        return mNativeAd == null ? null : mNativeAd.getAdIconView();
    }

    public View getAdMediaView(Object... object) {
        return mNativeAd == null ? null : mNativeAd.getAdMediaView(object);
    }

    public ViewGroup getCustomAdContainer() {
        return mNativeAd == null ? null : mNativeAd.getCustomAdContainer();
    }

    public void prepare(View view, FrameLayout.LayoutParams adOptionParams) {
        prepare(view, null, adOptionParams);
    }

    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams adOptionParams) {
        if (mNativeAd == null || mAdWrapper == null)
            return;
        if (mNativeAd instanceof RTBBaseAd) {
            ((RTBBaseAd) mNativeAd).setAdActionListener(getAdActionListener());
        }
        if (clickViewList == null || clickViewList.isEmpty()) {
            mNativeAd.prepare(view, adOptionParams);
        } else {
            mNativeAd.prepare(view, clickViewList, adOptionParams);
        }
        mAdWrapper.onImpression();
    }

    @Override
    public void destroy() {
        if (mNativeAd == null)
            return;
        mNativeAd.destroy();
    }

    public INativeAd getNativeAd() {
        if (mNativeAd != null) {
            return mNativeAd;
        }
        RTBWrapper adWrapper = getLoadedAd();
        if (adWrapper != null && adWrapper.getRTBAd() instanceof INativeAd) {
            mNativeAd = (INativeAd) adWrapper.getRTBAd();
        }
        return mNativeAd;
    }

}
