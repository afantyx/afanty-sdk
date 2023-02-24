package com.afanty;

import android.content.Context;
import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.config.BasicAftConfig;
import com.afanty.models.Bid;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.log.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttributionManager {
    private static final String TAG = "AD.Aft.AttributionManager";
    private static long DEF_INIT_RETRY_DURATION = 1000 * 3;
    private static long DEF_RETRY_DURATION_INCREASE = 1000 * 2;
    private static ExecutorService mExecutorForAD = Executors.newCachedThreadPool();
    private static volatile AttributionManager sInstance;
    private volatile boolean mSdkInitialized = false;
    private volatile boolean isInitializing = false;
    private String mUserAgent;
    private boolean hasCallBack;

    private AttributionManager() {
    }

    public static AttributionManager getInstance() {
        if (sInstance == null) {
            synchronized (AttributionManager.class) {
                if (sInstance == null)
                    sInstance = new AttributionManager();
            }
        }
        return sInstance;
    }

    public boolean isSdkInitialized() {
        return mSdkInitialized;
    }

    public void init(final Context context) {
        if (isSdkInitialized() || isInitializing)
            return;
        isInitializing = true;
        mSdkInitialized = true;

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork(1000) {
            @Override
            public void callBackOnUIThread() {
                mUserAgent = CommonUtils.getWebViewUA();
                isInitializing = false;
            }
        });
    }

    public void reportShow(final List<String> trackUrls, final Bid bid, final AdReportListener adReportListener) {
        if (trackUrls == null || trackUrls.isEmpty() || bid == null)
            return;

        getAdReporter().execute(new Runnable() {
            @Override
            public void run() {
                boolean hasReportFailure = false;
                for (int i = 0; i < trackUrls.size(); i++) {
                    String url = trackUrls.get(i);
                    boolean success = false;
                    int retryCount = 0;
                    int retryClout = BasicAftConfig.getAftPingRetryCount();
                    while (!success && retryCount < retryClout) {
                        success = TrackUrlsHelper.reportTrackUrlWithUA(url, mUserAgent, TrackType.SHOW, retryCount, retryClout, bid.adid);
                        Logger.d("AD_REPORT", "["+success+"]imp:"+url);
                        retryCount++;
                        if (!success) {
                            try {
                                Thread.sleep(DEF_INIT_RETRY_DURATION + DEF_RETRY_DURATION_INCREASE * i);
                            } catch (Exception ignore) {
                            }
                        }
                    }
                    if (!success)
                        hasReportFailure = true;
                }

                if (adReportListener != null)
                    adReportListener.reportResult(hasReportFailure);
            }
        });
    }


    public void reportClick(final List<String> trackUrls, final Bid adData, final AdReportListener adReportListener) {
        if (trackUrls == null || trackUrls.isEmpty())
            return;
        if (TextUtils.isEmpty(mUserAgent))
            mUserAgent = CommonUtils.getWebViewUA();

        getAdReporter().execute(new Runnable() {
            @Override
            public void run() {
                boolean hasReportFailure = false;
                for (int i = 0; i < trackUrls.size(); i++) {
                    String url = trackUrls.get(i);
                    boolean success = false;
                    int retryCount = 0;
                    int retryMax = BasicAftConfig.getAftPingRetryCount();
                    while (!success && retryCount < retryMax) {
                        success = TrackUrlsHelper.reportTrackUrlWithUA(url, mUserAgent, TrackType.CLICK, retryCount, retryMax, adData.getAdId());
                        retryCount++;
                        if (!success) {
                            try {
                                Thread.sleep(DEF_INIT_RETRY_DURATION + DEF_RETRY_DURATION_INCREASE * i);
                            } catch (Exception ignore) {
                            }
                        }
                    }
                    if (!success)
                        hasReportFailure = true;
                }
                if (adReportListener != null)
                    adReportListener.reportResult(hasReportFailure);
            }
        });
    }


    protected ExecutorService getAdReporter() {
        return mExecutorForAD;
    }

    public interface AdReportListener {
        void reportResult(boolean hasReportFailure);
    }

}
