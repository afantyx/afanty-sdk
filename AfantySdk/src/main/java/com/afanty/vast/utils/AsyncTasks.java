// Copyright 2018-2020 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class AsyncTasks {
    private static Executor sExecutor;
    private static Handler sUiThreadHandler;

    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = 1024;
    private static final int KEEP_ALIVE_SECONDS = 3;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    static {
        init();
    }

    private static void init() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new SynchronousQueue<>(), sThreadFactory);
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        sExecutor = threadPoolExecutor;

        sUiThreadHandler = new Handler(Looper.getMainLooper());
    }

    @VisibleForTesting
    public static void setExecutor(Executor executor) {
        sExecutor = executor;
    }

    /**
     * Starting with Honeycomb, default AsyncTask#execute behavior runs the tasks serially. This
     * method attempts to force these AsyncTasks to run in parallel with a ThreadPoolExecutor.
     */
    public static <P> void safeExecuteOnExecutor(final @NonNull AsyncTask<P, ?, ?> asyncTask, final @Nullable P... params) {
        Preconditions.checkNotNull(asyncTask, "Unable to execute null AsyncTask.");

        if (Looper.getMainLooper() == Looper.myLooper()) {
            asyncTask.executeOnExecutor(sExecutor, params);
        } else {
            sUiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    asyncTask.executeOnExecutor(sExecutor, params);
                }
            });
        }
    }
}
