package com.afanty.internal.banner;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import com.afanty.ads.AdSize;
import com.afanty.internal.Ad;
import com.afanty.internal.action.ActionTrigger;
import com.afanty.models.Bid;

import java.lang.ref.WeakReference;

public abstract class AbsBaseBanner {
    private static final String TAG = "RTB.RTBAbsBaseBanner";
    private WeakReference<BannerLoaderInterface> mAdListener;
    private Bid mBid;

    private ActionTrigger mActionTrigger;

    protected void setBidAndListener(Bid bid, BannerLoaderInterface listener) {
        this.mBid = bid;
        mAdListener = new WeakReference<>(listener);
        initTrigger();
    }

    @Nullable
    public BannerLoaderInterface getAdListener() {
        return mAdListener == null ? null : mAdListener.get();
    }

    public void performActionForInternalClick(Context context) {
        if (mActionTrigger != null)
            mActionTrigger.performClick(context, null);
    }

    public void performActionForInternalClick(Context context, String sourceType) {
        if (mActionTrigger != null)
            mActionTrigger.performClick(context, null, sourceType);
    }

    public void performActionForAdClicked(Context context, final String sourceType, final int downloadOptTrig) {
        if (mActionTrigger != null)
            mActionTrigger.performActionForAdClicked(context, sourceType, downloadOptTrig);
    }

    public abstract void loadBanner(Context context, AdSize adSize, AdView adView, Bid bid, BannerLoaderInterface bannerLoaderInterface);

    protected abstract boolean isAdSizeConform(AdSize adSize, Bid bid);

    protected Point resolvedAdSize(AdSize adSize) {
        return null;
    }
    public void destroy() {
    }


    private void initTrigger() {
        mActionTrigger = new ActionTrigger(mBid, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (getAdListener() == null)
                    return;

                if (msg.what == Ad.AD_LOAD_CLICK) {
                    getAdListener().onAdBannerClicked();
                }
            }
        });
    }
}
