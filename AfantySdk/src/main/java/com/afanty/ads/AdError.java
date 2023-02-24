package com.afanty.ads;

import android.text.TextUtils;

public class AdError extends Exception {
    public static final AdError NETWORK_ERROR = new AdError(ErrorCode.NETWORK_ERROR, "Network Error");
    public static final AdError NO_FILL = new AdError(ErrorCode.NO_FILL, "No Fill");
    public static final AdError DIS_CONDITION_ERROR = new AdError(ErrorCode.DIS_CONDITION, "Display Condition Error");
    public static final AdError PARAMETER_ERROR = new AdError(ErrorCode.PARAMETER_ERROR, "Parameter Error");

    public static final AdError INTERNAL_ERROR = new AdError(ErrorCode.INTERNAL_ERROR, "Internal Error");
    public static final AdError TIMEOUT_ERROR = new AdError(ErrorCode.TIMEOUT, "Time out");

    public static final AdError UNKNOWN_ERROR = new AdError(ErrorCode.UNKNOWN, "Unknown error");
    public static final AdError NO_VAST_CONTENT_ERROR = new AdError(ErrorCode.NO_VAST_CONTENT, "No Vast Content");
    public static final AdError DOWNLOAD_VAST_ERROR = new AdError(ErrorCode.DOWNLOAD_VAST_ERROR, "Download Vast Error");
    public static final AdError UN_SUPPORT_TYPE_ERROR = new AdError(ErrorCode.UN_SUPPORT_TYPE, "Unsupported create type");
    public static final AdError DOWNLOAD_VIDEO_ERROR = new AdError(ErrorCode.DOWNLOAD_VIDEO_ERROR, "Download Video Error");
    public static final AdError AD_EXPIRED = new AdError(ErrorCode.AD_EXPIRED, "This Ad is Expired");


    private final int mErrorCode;
    private final String mErrorMsg;

    public AdError(int errorCode, String errorMsg) {
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = "unknown error";
        }

        this.mErrorCode = errorCode;
        this.mErrorMsg = errorMsg;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorMessage() {
        return this.mErrorMsg;
    }

    public AdError setErrorMessage(String msg) {
        return new AdError(mErrorCode, msg);
    }

    @Override
    public String toString() {
        return "[code = " + mErrorCode + ", msg = " + mErrorMsg + "]";
    }

    public interface ErrorCode {
        int NETWORK_ERROR = 1000;
        int NO_FILL = 1001;
        int DIS_CONDITION = 1002;
        int PARAMETER_ERROR = 1003;

        int SERVER_ERROR = 2000;
        int INTERNAL_ERROR = 2001;
        int TIMEOUT = 2002;

        int UNKNOWN = 3000;
        int NO_VAST_CONTENT = 3001;
        int DOWNLOAD_VAST_ERROR = 3002;
        int UN_SUPPORT_TYPE = 3003;
        int DOWNLOAD_VIDEO_ERROR = 3004;
        int AD_EXPIRED = 3005;

    }
}
