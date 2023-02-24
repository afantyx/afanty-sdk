// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;

import java.io.Serializable;

/**
 * State encapsulation for VAST tracking URLs that may or may not only be called once. For example,
 * progress trackers are only called once, but error trackers are repeatable.
 */
public class VastTracker implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final MessageType mMessageType;
    @NonNull
    private final String mContent;
    private boolean mCalled;
    private boolean mIsRepeatable;
    private String mEvent;

    public enum MessageType {TRACKING_URL, QUARTILE_EVENT}

    public VastTracker(@NonNull final MessageType messageType, @NonNull final String content, String event) {
        Preconditions.checkNotNull(messageType);
        Preconditions.checkNotNull(content);

        mMessageType = messageType;
        mContent = content;
        mEvent = event;
    }

    // Legacy implementation implied URL tracking
    public VastTracker(@NonNull final String trackingUrl, String event) {
        this(MessageType.TRACKING_URL, trackingUrl, event);
    }

    public VastTracker(@NonNull String trackingUrl, boolean isRepeatable, String event) {
        this(trackingUrl, event);
        mIsRepeatable = isRepeatable;
    }

    @NonNull
    public MessageType getMessageType() {
        return mMessageType;
    }

    @NonNull
    public String getContent() {
        return mContent;
    }

    public void setTracked() {
        mCalled = true;
    }

    public boolean isTracked() {
        return mCalled;
    }

    public boolean isRepeatable() {
        return mIsRepeatable;
    }

    public String getEvent() {
        return mEvent;
    }
}
