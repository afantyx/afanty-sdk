package com.afanty.internal.base;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;

import com.afanty.ads.AdError;
import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.internal.Ad;
import com.afanty.internal.internal.AdRequestListener;
import com.afanty.models.Bid;
import com.afanty.models.BidResponse;
import com.afanty.request.BaseRTBRequest;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.NetworkUtils;
import com.afanty.utils.log.Logger;

public abstract class BaseLoader implements Ad {
    public static String TAG = "RTBBaseLoader";
    protected String mTagId;
    private static final long EXPIRED_TIME = 60 * 60 * 1000;
    protected Context mContext;
    protected AdRequestListener mResponseAdRequestListener;
    private Bid mBid;
    protected long mStartLoadTime = 0;
    protected long mLoadedTimeStamp;

    protected BaseLoader(Context context, String tagId) {
        if (context == null)
            throw new IllegalStateException("context cannot be null");

        if (TextUtils.isEmpty(tagId))
            throw new IllegalStateException("placementId cannot be null");
        this.mContext = context;
        this.mTagId = tagId;
        initAdRequestListener();
    }

    public Bid getBid() {
        return mBid;
    }

    public String getTagId() {
        return mTagId;
    }

    private void initAdRequestListener() {
        mResponseAdRequestListener = new AdRequestListener() {
            @Override
            public void onAdRequestError(String errorType, String msg) {
                int errorCode = getErrorCode(errorType);
                onAdDataLoadError(new AdError(errorCode, msg));
                Logger.d(TAG, "#onAdRequestError tagId = " + mTagId + ", msg:" + msg);
            }

            @Override
            public void onAdRequestSuccess(String jsonStr) {
                try {
                    BidResponse mBidResponse = new BidResponse(jsonStr,mTagId);
                    mLoadedTimeStamp = System.currentTimeMillis();
                    if (mBidResponse != null) {
                        mBid = mBidResponse.getFirstBid();
                        if (mBid != null) {
                            handleAdRequestSuccess();
                        }
                    }
                } catch (Exception e) {
                    onAdDataLoadError(new AdError(AdError.ErrorCode.UNKNOWN, e.getMessage()));
                }
            }
        };
    }


    @Override
    public void loadAd() {
        Pair<Boolean, Boolean> pair = NetworkUtils.checkConnected(ContextUtils.getContext());
        if (!pair.first && !pair.second) {
            onAdDataLoadError(AdError.NETWORK_ERROR);
            return;
        }
        if (TextUtils.isEmpty(mTagId)) {
            onAdDataLoadError(AdError.PARAMETER_ERROR);
            return;
        }
        mStartLoadTime = System.currentTimeMillis();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            ThreadManager.getInstance().run(new DelayRunnableWork() {
                @Override
                public void execute() {
                    load();
                }
            }, ThreadManager.TYPE_NETWORK_REQUEST);
        } else {
            load();
        }
    }

    private void load() {
        try {
            _load();
        } catch (Exception e) {
            onAdDataLoadError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, e.getMessage()));
        }
    }

    private int getErrorCode(String errorType) {
        int errorCode;
        switch (errorType) {
            case AdRequestListener.NETWORK:
                errorCode = AdError.ErrorCode.NETWORK_ERROR;
                break;
            case AdRequestListener.BUILD:
                errorCode = AdError.ErrorCode.INTERNAL_ERROR;
                break;
            case AdRequestListener.SERVER:
                errorCode = AdError.ErrorCode.SERVER_ERROR;
                break;
            default:
                errorCode = AdError.ErrorCode.UNKNOWN;

        }
        return errorCode;
    }

    public boolean isAdExpired() {
        return System.currentTimeMillis() - mLoadedTimeStamp > EXPIRED_TIME;
    }

    protected abstract void onAdDataLoadError(AdError adError);

    protected abstract boolean onAdDataLoaded(boolean isFromCache);

    protected abstract void handleAdRequestSuccess();

    protected abstract void _load();

    protected abstract BaseRTBRequest buildRequest();

    protected boolean needParseVastAsNative() {
        return false;
    }

    protected boolean needWaitVideoDownloadFinished() {
        return false;
    }

    @Override
    public void destroy() {

    }

}
