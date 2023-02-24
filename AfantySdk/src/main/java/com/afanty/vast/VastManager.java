// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

import com.afanty.vast.download.VastDownloadError;
import com.afanty.vast.download.VastDownloadListener;
import com.afanty.vast.download.VastDownloadManager;
import com.afanty.vast.utils.AsyncTasks;
import com.afanty.vast.utils.VisibleForTesting;


/**
 * Given a VAST xml document, this class manages the lifecycle of parsing and finding a video and
 * possibly companion ad. It provides the API for clients to prepare a
 * {@link VastVideoConfig}.
 */
public class VastManager implements VastXmlManagerAggregator.VastXmlManagerAggregatorListener {
    private static final String TAG = "Ad.VastManager";

    /**
     * Users of this class should subscribe to this listener to get updates
     * when a video is found or when no video is available.
     */
    public interface VastManagerListener {
        /**
         * Called when a video is found or if the VAST document is invalid. Passes in {@code null}
         * when the VAST document is invalid.
         *
         * @param vastVideoConfig A configuration that can be used for displaying a VAST
         *                        video or {@code null} if the VAST document is invalid.
         */
        void onVastVideoConfigurationPrepared(@Nullable final VastVideoConfig vastVideoConfig);
    }

    @Nullable
    private VastManagerListener mVastManagerListener;
    @Nullable
    private VastXmlManagerAggregator mVastXmlManagerAggregator;
    @Nullable
    private String mDspCreativeId;
    private double mScreenAspectRatio;
    private int mScreenWidthDp;

    private final boolean mShouldPreCacheVideo;

    private VastDownloadManager mVastDownloadManager;

    private String mAftAdId;
    private String mPlacementId;

    private long mExpire;

    public String getAftAdId() {
        return mAftAdId;
    }

    public void setAftAdId(String adId) {
        this.mAftAdId = adId;
    }

    public String getPlacementId() {
        return mPlacementId;
    }

    public void setPlacementId(String placementId) {
        this.mPlacementId = placementId;
    }

    private long getExpire() {
        return mExpire;
    }

    public void setExpire(long expire) {
        this.mExpire = expire;
    }

    public VastManager(@NonNull final Context context, boolean shouldPreCacheVideo) {
        initializeScreenDimensions(context);
        mShouldPreCacheVideo = shouldPreCacheVideo;
        mVastDownloadManager = new VastDownloadManager(context);
    }

    /**
     * Creates and starts an async task that parses the VAST xml document.
     *
     * @param vastXml             The initial VAST xml document
     * @param vastManagerListener Notified when a video configuration has been found or when
     *                            the VAST document is invalid
     */
    @SuppressLint("RestrictedApi")
    public void prepareVastVideoConfiguration(@Nullable final String vastXml,
                                              @NonNull final VastManagerListener vastManagerListener,
                                              @Nullable String dspCreativeId,
                                              @NonNull final Context context) {
        Preconditions.checkNotNull(vastManagerListener, "vastManagerListener cannot be null");
        Preconditions.checkNotNull(context, "context cannot be null");

        if (mVastXmlManagerAggregator == null) {
            mVastManagerListener = vastManagerListener;
            mVastXmlManagerAggregator = new VastXmlManagerAggregator(this,
                    mScreenAspectRatio,
                    mScreenWidthDp,
                    context.getApplicationContext());
            mDspCreativeId = dspCreativeId;

            try {
                AsyncTasks.safeExecuteOnExecutor(mVastXmlManagerAggregator, vastXml);
            } catch (Exception e) {
                mVastManagerListener.onVastVideoConfigurationPrepared(null);
            }
        }
    }

    /**
     * Stops the VAST aggregator from continuing to follow wrapper redirects.
     */
    public void cancel() {
        if (mVastXmlManagerAggregator != null) {
            mVastXmlManagerAggregator.cancel(true);
            mVastXmlManagerAggregator = null;
        }
    }

    @Override
    public void onAggregationComplete(@Nullable final VastVideoConfig vastVideoConfig) {
        if (mVastManagerListener == null) {
            throw new IllegalStateException(
                    "mVastManagerListener cannot be null here. Did you call " +
                            "prepareVastVideoConfiguration()?");
        }

        if (vastVideoConfig == null) {
            mVastManagerListener.onVastVideoConfigurationPrepared(null);
            return;
        }
        if (!TextUtils.isEmpty(mDspCreativeId)) {
            vastVideoConfig.setDspCreativeId(mDspCreativeId);
        }

        // Return immediately if we already have a cached video or if video precache is not required.
        if (!mShouldPreCacheVideo) {
            Log.d(TAG, "onParseComplete shouldPreCacheVideo");
            mVastManagerListener.onVastVideoConfigurationPrepared(vastVideoConfig);
            return;
        }

        final VastDownloadListener miDownloadListener = new VastDownloadListener() {
            @Override
            public void onDownloadFailed(String url, VastDownloadError error) {
                Log.d(TAG, "down load error " + error);
                mVastManagerListener.onVastVideoConfigurationPrepared(null);
            }

            @Override
            public void onDownloadSuccess(String url, String localPath, long size) {
                Log.d(TAG, "down load success " + localPath);
                vastVideoConfig.setDiskMediaFileUrl(localPath);
                mVastManagerListener.onVastVideoConfigurationPrepared(vastVideoConfig);
            }

            @Override
            public void onDownloading(String url, int progress) {
                Log.d(TAG, "down load ing " + progress);
            }
        };
        Log.d(TAG, "start download");
        mVastDownloadManager.setDownLoadUrl(vastVideoConfig.getNetworkMediaFileUrl());
        mVastDownloadManager.startTask(miDownloadListener, getAftAdId(), getPlacementId(), getExpire());
    }

    @SuppressLint("RestrictedApi")
    private void initializeScreenDimensions(@NonNull final Context context) {
        Preconditions.checkNotNull(context, "context cannot be null");
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final int screenWidth = display.getWidth();
        final int screenHeight = display.getHeight();
        // Use the screen density to convert x and y (in pixels) to DP. Also, check the density to
        // make sure that this is a valid density and that this is not going to divide by 0.
        float density = context.getResources().getDisplayMetrics().density;
        if (density <= 0) {
            density = 1;
        }

        mScreenAspectRatio = (double) screenWidth / screenHeight;
        mScreenWidthDp = (int) (screenWidth / density);
    }

    @VisibleForTesting
    @Deprecated
    int getScreenWidthDp() {
        return mScreenWidthDp;
    }

    @VisibleForTesting
    @Deprecated
    double getScreenAspectRatio() {
        return mScreenAspectRatio;
    }
}
