// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.vast.utils.Preconditions;

import java.io.Serializable;
import java.util.List;

public class VastCompanionAdConfig implements Serializable {
    private static final long serialVersionUID = 0L;

    private final int mWidth;
    private final int mHeight;
    @NonNull
    private final VastResource mVastResource;
    @Nullable
    private final String mClickThroughUrl;
    @NonNull
    private final List<VastTracker> mClickTrackers;
    @NonNull
    private final List<VastTracker> mCreativeViewTrackers;

    public VastCompanionAdConfig(
            int width,
            int height,
            @NonNull VastResource vastResource,
            @Nullable String clickThroughUrl,
            @NonNull List<VastTracker> clickTrackers,
            @NonNull List<VastTracker> creativeViewTrackers) {
        Preconditions.checkNotNull(vastResource);
        Preconditions.checkNotNull(clickTrackers, "clickTrackers cannot be null");
        Preconditions.checkNotNull(creativeViewTrackers, "creativeViewTrackers cannot be null");

        mWidth = width;
        mHeight = height;
        mVastResource = vastResource;
        mClickThroughUrl = clickThroughUrl;
        mClickTrackers = clickTrackers;
        mCreativeViewTrackers = creativeViewTrackers;
    }

    /**
     * Add click trackers.
     *
     * @param clickTrackers List of URLs to hit
     */
    public void addClickTrackers(@NonNull final List<VastTracker> clickTrackers) {
        Preconditions.checkNotNull(clickTrackers, "clickTrackers cannot be null");
        mClickTrackers.addAll(clickTrackers);
    }

    /**
     * Add creativeView trackers that are supposed to be fired when the companion ad is visible.
     *
     * @param creativeViewTrackers List of URLs to hit when this companion is viewed
     */
    public void addCreativeViewTrackers(@NonNull final List<VastTracker> creativeViewTrackers) {
        Preconditions.checkNotNull(creativeViewTrackers, "creativeViewTrackers cannot be null");
        mCreativeViewTrackers.addAll(creativeViewTrackers);
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

    @Nullable
    public String getClickThroughUrl() {
        return mClickThroughUrl;
    }

    @NonNull
    public List<VastTracker> getClickTrackers() {
        return mClickTrackers;
    }

    @NonNull
    public List<VastTracker> getCreativeViewTrackers() {
        return mCreativeViewTrackers;
    }
}
