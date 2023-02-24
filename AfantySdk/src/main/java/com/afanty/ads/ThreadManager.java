package com.afanty.ads;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    public final static int TYPE_NORMAL = 2;
    public final static int TYPE_NETWORK_REQUEST = 4;
    public final static int TYPE_SOURCE_PRELOAD_TASK = 6;
    public final static int TYPE_SOURCE_ANALYTICS_TASK = 8;

    private static ThreadManager sSelf = null;

    private ExecutorService mNormalPool = null;
    private ExecutorService mNetworkRequestPool = null;
    private ExecutorService mSourcePreloadPool = null;
    private ExecutorService mSourceAnalyticsPool = null;

    private Handler mHandler;

    private ThreadManager() {
        mNormalPool = Executors.newCachedThreadPool();
        mNetworkRequestPool = Executors.newCachedThreadPool();

        mHandler = new Handler(Looper.getMainLooper()) {
        };
    }

    public static ThreadManager getInstance() {
        if (sSelf == null) {
            sSelf = new ThreadManager();
        }
        return sSelf;
    }

    public void run(DelayRunnableWork delayRunnableWork) {
        run(delayRunnableWork, TYPE_NORMAL);
    }

    public void run(DelayRunnableWork delayRunnableWork, int taskType) {
        ExecutorService executorService;
        switch (taskType) {
            case TYPE_NETWORK_REQUEST:
                executorService = mNetworkRequestPool;
                break;
            case TYPE_SOURCE_PRELOAD_TASK:
                if (mSourcePreloadPool == null) {
                    mSourcePreloadPool = Executors.newSingleThreadExecutor();
                }
                executorService = mSourcePreloadPool;
                break;
            case TYPE_SOURCE_ANALYTICS_TASK:
                if (mSourceAnalyticsPool == null) {
                    mSourceAnalyticsPool = Executors.newSingleThreadExecutor();
                }
                executorService = mSourceAnalyticsPool;
                break;
            default:
                executorService = mNormalPool;
        }
        run(delayRunnableWork, executorService);
    }

    public void run(DelayRunnableWork delayRunnableWork, ExecutorService executorService) {
        if (executorService == null) {
            return;
        }
        if (delayRunnableWork instanceof DelayRunnableWork.UICallBackDelayRunnableWork) {
            ((DelayRunnableWork.UICallBackDelayRunnableWork) delayRunnableWork).setUIHandler(mHandler);
        }
        executorService.execute(delayRunnableWork);
    }

}
