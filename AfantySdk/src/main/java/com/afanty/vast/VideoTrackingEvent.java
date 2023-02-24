// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal Video Tracking events, defined in ad server
 */
public enum VideoTrackingEvent {

    CREATIVE_VIEW("creativeView"),
    START("start"),
    FIRST_QUARTILE("firstQuartile"),
    MIDPOINT("midpoint"),
    THIRD_QUARTILE("thirdQuartile"),
    COMPLETE("complete"),
    COMPANION_AD_VIEW("companionAdView"),
    COMPANION_AD_CLICK("companionAdClick"),
    UNKNOWN(""),

    MUTE("mute"),
    UNMUTE("unmute"),
    PAUSE("pause"),
    REWIND("rewind"),
    RESUME("resume"),
    FULL_SCREEN("fullscreen"),
    EXIT_FULL_SCREEN("exitFullscreen"),
    EXPAND("expand"),
    COLLAPSE("collapse"),
    ACCEPT_INVITATION("acceptInvitation"),
    CLOSE("close"),
    SKIP("skip"),
    CLOSE_LINEAR("closeLinear"),
    ERROR("error");

    private final String name;

    VideoTrackingEvent(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public static VideoTrackingEvent fromString(@Nullable final String name) {
        if (name == null) {
            return UNKNOWN;
        }

        for (VideoTrackingEvent event : VideoTrackingEvent.values()) {
            if (name.equals(event.getName())) {
                return event;
            }
        }

        return UNKNOWN;
    }
}
