// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

public class VideoViewAbilityTracker extends VastTracker {
    private final int mViewablePlaytimeMS;
    private final int mPercentViewable;

    public VideoViewAbilityTracker(final int viewablePlaytimeMS, final int percentViewable,
                                   @NonNull final String trackerUrl, String event) {
        super(trackerUrl, event);
        mViewablePlaytimeMS = viewablePlaytimeMS;
        mPercentViewable = percentViewable;
    }

    public int getViewablePlaytimeMS() {
        return mViewablePlaytimeMS;
    }

    public int getPercentViewable() {
        return mPercentViewable;
    }
}
