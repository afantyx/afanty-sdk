package com.afanty.ads;

import android.view.Gravity;

public final class VideoOptions {
    private final boolean mStartMuted;
    private final int mSoundGravity;

    public VideoOptions(Builder builder) {
        this.mStartMuted = builder.startMuted;
        this.mSoundGravity = builder.soundGravity;
    }

    public boolean getStartMuted() {
        return this.mStartMuted;
    }

    public int getSoundGravity() {
        return mSoundGravity;
    }

    public static final class Builder {
        private boolean startMuted = true;
        private int soundGravity = Gravity.START;

        public Builder() {
        }

        public VideoOptions.Builder setStartMuted(boolean startMuted) {
            this.startMuted = startMuted;
            return this;
        }

        public VideoOptions.Builder setSoundGravity(int soundGravity) {
            this.soundGravity = soundGravity;
            return this;
        }

        public VideoOptions build() {
            return new VideoOptions(this);
        }
    }
}