// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.vast.utils.Preconditions;

import java.io.Serializable;
import java.util.List;

/**
 * The data and event handlers for the icon displayed during a VAST 3.0 video.
 */
public class VastIconConfig implements Serializable {
    private static final long serialVersionUID = 0L;

    private final int mWidth;
    private final int mHeight;
    private final int mOffsetMS;
    @Nullable
    private final Integer mDurationMS;
    @NonNull
    private final VastResource mVastResource;
    @NonNull
    private final List<VastTracker> mClickTrackingUris;
    @Nullable
    private final String mClickThroughUri;
    @NonNull
    private final List<VastTracker> mViewTrackingUris;

    VastIconConfig(int width,
                   int height,
                   @Nullable Integer offsetMS,
                   @Nullable Integer durationMS,
                   @NonNull VastResource vastResource,
                   @NonNull List<VastTracker> clickTrackingUris,
                   @Nullable String clickThroughUri,
                   @NonNull List<VastTracker> viewTrackingUris) {
        Preconditions.checkNotNull(vastResource);
        Preconditions.checkNotNull(clickTrackingUris);
        Preconditions.checkNotNull(viewTrackingUris);

        mWidth = width;
        mHeight = height;
        mOffsetMS = offsetMS == null ? 0 : offsetMS;
        mDurationMS = durationMS;
        mVastResource = vastResource;
        mClickTrackingUris = clickTrackingUris;
        mClickThroughUri = clickThroughUri;
        mViewTrackingUris = viewTrackingUris;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @NonNull
    public VastResource getVastResource() {
        return mVastResource;
    }
}
