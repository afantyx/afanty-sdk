// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;

import java.io.Serializable;
import java.util.Locale;

/**
 * A Vast tracking URL with an "absolute" trigger threshold. The tracker should be triggered
 * after a fixed number of milliseconds have been played.
 */
public class VastAbsoluteProgressTracker extends VastTracker
        implements Comparable<VastAbsoluteProgressTracker>, Serializable {
    private static final long serialVersionUID = 0L;
    private final int mTrackingMilliseconds;

    public VastAbsoluteProgressTracker(@NonNull final MessageType messageType,
                                       @NonNull final String content, int trackingMilliseconds, String event) {
        super(messageType, content, event);
        Preconditions.checkArgument(trackingMilliseconds >= 0);
        mTrackingMilliseconds = trackingMilliseconds;
    }

    public VastAbsoluteProgressTracker(@NonNull final String trackingUrl,
                                       int trackingMilliseconds, String event) {
        this(MessageType.TRACKING_URL, trackingUrl, trackingMilliseconds, event);
    }

    public int getTrackingMilliseconds() {
        return mTrackingMilliseconds;
    }

    @Override
    public int compareTo(@NonNull final VastAbsoluteProgressTracker other) {
        int you = other.getTrackingMilliseconds();
        int me = getTrackingMilliseconds();

        return me - you;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%dms: %s", mTrackingMilliseconds, getContent());
    }
}
