// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;

import java.io.Serializable;
import java.util.Locale;

/**
 * A Vast tracking URL with a "fractional" tracking threshold on the interval [0.0, 1.0].
 * The tracker should be triggered after the given fraction of the video has been played.
 */
public class VastFractionalProgressTracker extends VastTracker implements Comparable<VastFractionalProgressTracker>, Serializable {
    private static final long serialVersionUID = 0L;
    private final float mFraction;

    public VastFractionalProgressTracker(@NonNull final MessageType messageType,
                                         @NonNull final String content, float trackingFraction, String event) {
        super(messageType, content, event);
        Preconditions.checkArgument(trackingFraction >= 0);
        mFraction = trackingFraction;
    }

    public VastFractionalProgressTracker(@NonNull final String trackingUrl, float trackingFraction, String event) {
        this(MessageType.TRACKING_URL, trackingUrl, trackingFraction, event);
    }

    public float trackingFraction() {
        return mFraction;
    }

    @Override
    public int compareTo(@NonNull final VastFractionalProgressTracker other) {
        float you = other.trackingFraction();
        float me = trackingFraction();

        return Double.compare(me, you);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%2f: %s", mFraction, getContent());
    }
}
