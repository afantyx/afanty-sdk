package com.afanty.internal.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afanty.ads.AdError;
import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.internal.config.AftConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.internal.CreativeType;
import com.afanty.utils.SourceDownloadUtils;
import com.afanty.vast.VastHelper;
import com.afanty.video.VideoDownloadInterface;

public abstract class BaseHandleLoader extends BaseLoader {
    private static final String TAG = "RTBBaseHandleLoader";
    private Handler mHandler;

    boolean vastTimeOutCalled;
    boolean isVastAdResult = false;

    protected BaseHandleLoader(Context context, String tagId) {
        super(context, tagId);
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                long duration = System.currentTimeMillis() - mStartLoadTime;
                try {
                    switch (msg.what) {
                        case AD_LOAD_SUCCESS:
                            onAdLoaded();
                            break;
                        case AD_LOAD_FAILED:
                            Object obj = msg.obj;
                            AdError adError = obj instanceof AdError ? (AdError) obj : AdError.UNKNOWN_ERROR;
                            onAdLoadError(adError);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    onAdLoadError(new AdError(AdError.ErrorCode.INTERNAL_ERROR, e.getMessage()));
                }
            }
        };
    }

    @Override
    protected void onAdDataLoadError(AdError adError) {
        mHandler.sendMessage(mHandler.obtainMessage(AD_LOAD_FAILED, adError));
    }

    @Override
    protected void handleAdRequestSuccess() {
        if (getBid() == null) {
            onAdDataLoadError(AdError.NO_FILL);
            return;
        }
        if (CreativeType.isVAST(getBid())) {
            if (needParseVastAsNative()) {
                handleNativeVast();
            } else {
                handleOriginVast();
            }
        } else if (CreativeType.isOnePoster(getBid())) {
            handOnePosterAd();
        } else {
            handleNativeCustomAd();
        }
        SourceDownloadUtils.tryDownloadImages(getBid());
    }

    @Override
    protected boolean onAdDataLoaded(boolean isFromCache) {
        if (getBid() == null) {
            onAdDataLoadError(AdError.NO_FILL);
            return false;
        }
        mHandler.sendMessage(mHandler.obtainMessage(AD_LOAD_SUCCESS));
        return false;
    }


    private void _onAdDataLoaded() {
        onAdDataLoaded(false);
    }

    private void handleOriginVast() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isVastAdResult) {
                    vastTimeOutCalled = true;
                    onAdDataLoadError(AdError.DOWNLOAD_VAST_ERROR);
                }
            }
        }, AftConfig.getAftVastDownloadTimeOut());


        if (TextUtils.isEmpty(getBid().getVast())) {
            onAdDataLoadError(AdError.NO_VAST_CONTENT_ERROR);
        } else {
            VastHelper.rtbTryDownLoadVastXml(mContext, getBid(), getBid().getVast(), true, new VastHelper.VastParseResult() {
                @Override
                public void onVastParseError(String errorMessage) {
                    if (vastTimeOutCalled) {
                        return;
                    }
                    isVastAdResult = true;
                    if (TextUtils.equals(AdConstants.Vast.NO_VAST_CONTENT, errorMessage)) {
                        onAdDataLoadError(AdError.NO_VAST_CONTENT_ERROR);
                    } else {
                        onAdDataLoadError(AdError.DOWNLOAD_VAST_ERROR);
                    }
                }

                @Override
                public void onVastParseSuccess() {
                    if (vastTimeOutCalled) {
                        return;
                    }
                    isVastAdResult = true;
                    try {
                        _onAdDataLoaded();
                    } catch (Exception e) {
                        onAdDataLoadError(AdError.NO_VAST_CONTENT_ERROR);
                    }
                }
            });
        }
    }

    private void handleNativeVast() {
        VastHelper.rtbTryParseVastXml(mContext, getBid(), getBid().getVast(), new VastHelper.VastParseResult() {
            @Override
            public void onVastParseError(String errorMessage) {
                onAdDataLoadError(new AdError(AdError.ErrorCode.UNKNOWN, errorMessage));
            }

            @Override
            public void onVastParseSuccess() {
                try {
                    _onAdDataLoaded();
                } catch (Exception e) {
                    onAdDataLoadError(new AdError(AdError.ErrorCode.UNKNOWN, e.getMessage()));
                }
            }
        });
    }

    private void handOnePosterAd() {
        long startTime = System.currentTimeMillis();
        boolean downloadSuccess = SourceDownloadUtils.downloadImageAndCheckReady(getBid());
        if (downloadSuccess) {
            _onAdDataLoaded();
        } else {
            onAdDataLoadError(AdError.DIS_CONDITION_ERROR);
        }
    }

    private void handleNativeCustomAd() {
        boolean isVideoAd = CreativeType.isVideo(getBid());
        if (isVideoAd && needWaitVideoDownloadFinished()) {

            SourceDownloadUtils.tryLoadVideoResource(getBid(), new VideoDownloadInterface.VideoDownLoadListener() {
                @Override
                public void onLoadSuccess(long downloadTime) {
                    ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
                        @Override
                        public void callBackOnUIThread() {
                            _onAdDataLoaded();
                        }
                    });
                }

                @Override
                public void onLoadError() {
                    onAdDataLoadError(AdError.DOWNLOAD_VIDEO_ERROR);
                }
            }, AftConfig.getAftVideoDownloadTimeOut());
        } else {
            if (isVideoAd) {
                SourceDownloadUtils.tryLoadVideoResource(getBid());
            }
            _onAdDataLoaded();
        }
    }

    protected abstract void onAdLoaded();

    protected abstract void onAdLoadError(AdError error);


}
