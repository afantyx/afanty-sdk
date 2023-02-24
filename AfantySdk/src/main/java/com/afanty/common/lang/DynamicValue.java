package com.afanty.common.lang;

public class DynamicValue {
    private static final int STATUS_UNLOAD = 0;
    private static final int STATUS_LOADING = 1;
    private static final int STATUS_LOADED = 2;
    private int mStatus;
    private long mUpdateDuration;
    private long mLastSetTime = 0L;
    private Object mValue = null;
    private Object mDefaultValue = null;

    public DynamicValue(Object value, boolean isDefault, long updateDuration) {
        if (isDefault) {
            mDefaultValue = value;
            mStatus = STATUS_UNLOAD;
        } else {
            mValue = value;
            mStatus = STATUS_LOADED;
            mLastSetTime = System.currentTimeMillis();
        }
        mUpdateDuration = updateDuration;
    }

    public boolean isNeedUpdate() {
        return (Math.abs(System.currentTimeMillis() - mLastSetTime) > mUpdateDuration && mStatus != STATUS_LOADING);
    }

    public void updateValue(Object value) {
        updateValue(value, mUpdateDuration);
    }

    public void updateValue(Object value, long updateDuration) {
        mValue = value;
        mStatus = STATUS_LOADED;
        mLastSetTime = System.currentTimeMillis();
        mUpdateDuration = updateDuration;
    }


    public Object getObjectValue() {
        return (mValue != null) ? (Object) mValue : (Object) mDefaultValue;
    }

}

