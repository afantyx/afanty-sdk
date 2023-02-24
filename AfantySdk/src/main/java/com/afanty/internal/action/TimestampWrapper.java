package com.afanty.internal.action;

import android.os.SystemClock;

import androidx.annotation.NonNull;

public class TimestampWrapper<T> {
    @NonNull
    public final T mInstance;
    public long mCreatedTimestamp;

    public TimestampWrapper(@NonNull final T instance) {
        mInstance = instance;
        mCreatedTimestamp = SystemClock.uptimeMillis();
    }
}
