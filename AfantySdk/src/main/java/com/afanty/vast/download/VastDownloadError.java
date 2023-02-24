package com.afanty.vast.download;

import android.text.TextUtils;

public class VastDownloadError {
    public static final VastDownloadError ERROR_NETWORK = new VastDownloadError(1000, "Network Error");
    public static final VastDownloadError ERROR_SIZE = new VastDownloadError(2000, "File size < 0");
    public static final VastDownloadError ERROR_URL = new VastDownloadError(3000, "url error");
    public static final VastDownloadError ERROR_PARAMS = new VastDownloadError(4000, "params error");
    public static final VastDownloadError ERROR_EXCEPTION = new VastDownloadError(5000, "exception");
    public static final VastDownloadError ERROR_EXCEPTION_IO = new VastDownloadError(5001, "io exception");

    private final int mCode;
    private final String mMsg;

    public VastDownloadError(int mCode, String mMsg) {
        if (TextUtils.isEmpty(mMsg)) {
            mMsg = "Unknown Error";
        }

        this.mCode = mCode;
        this.mMsg = mMsg;
    }

    public int getErrorCode() {
        return this.mCode;
    }

    public String getErrorMessage() {
        return this.mMsg;
    }
}
