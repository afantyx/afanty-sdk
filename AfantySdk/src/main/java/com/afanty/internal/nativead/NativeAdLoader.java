package com.afanty.internal.nativead;

import android.content.Context;

import com.afanty.request.BaseRTBRequest;
import com.afanty.ads.AdError;
import com.afanty.internal.base.BaseHandleLoader;
import com.afanty.request.RTBNativeRequest;
import com.afanty.utils.log.Logger;

public class NativeAdLoader extends BaseHandleLoader {
    private static final String TAG = "RTB.Native";
    private AdLoadListener mAdListener;

    protected NativeAdLoader(Context context, String tagId) {
        super(context, tagId);
    }


    public void setAdListener(AdLoadListener adListener) {
        this.mAdListener = adListener;
    }


    @Override
    protected void _load() {
        buildRequest().startLoad(mResponseAdRequestListener);
    }

    @Override
    protected BaseRTBRequest buildRequest() {
        return new RTBNativeRequest(mTagId);
    }


    @Override
    protected void onAdLoaded() {
        if (mAdListener != null)
            mAdListener.onDataLoaded(getBid());
    }

    @Override
    protected void onAdLoadError(AdError error) {
        Logger.d(TAG, "#onAdLoadError:" + error.getErrorMessage());
        if (mAdListener != null)
            mAdListener.onDataError(error);
    }

    @Override
    protected boolean needParseVastAsNative() {
        return true;
    }
}
