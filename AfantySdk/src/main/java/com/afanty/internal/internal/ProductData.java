package com.afanty.internal.internal;

import com.afanty.config.BidConfigHelper;

import org.json.JSONObject;

public class ProductData {

    private static final String KEY_APP_NAME = "appname";
    private static final String KEY_APP_LOGO = "applogo";
    private static final String KEY_APP_DESC = "app_description";
    private static final String KEY_PKG_NAME = "packagename";

    private static final String KEY_APP_VERSION_NAME = "app_version_name";
    private static final String KEY_APP_VERSION_CODE = "app_version_code";
    private static final String KEY_APP_SIZE = "app_size";
    private static final String KEY_MIUI_BACKUP_URL = "click_url_backup";
    private static final String KEY_AMP_APP_ID = "amp_app_id";

    private static final String KEY_APP_URL = "apk_url";

    private final String mPkgName;
    private final String mAppName;
    private final String mAppLogo;
    private final String mAppDesc;
    private final String mAppVersionName;
    private final int mAppVersionCode;
    private final long mApkSize;
    private String mApkUrl;
    private final String mMiBackupUrl;
    private final String mAMPAppId;

    public ProductData(JSONObject jsonObject) {
        mPkgName = jsonObject.optString(KEY_PKG_NAME);

        mAppName = jsonObject.optString(KEY_APP_NAME);
        mAppLogo = jsonObject.optString(KEY_APP_LOGO);
        mAppDesc = jsonObject.optString(KEY_APP_DESC);
        mAppVersionName = jsonObject.optString(KEY_APP_VERSION_NAME);
        mAppVersionCode = jsonObject.optInt(KEY_APP_VERSION_CODE, -1);
        mApkSize = jsonObject.optLong(KEY_APP_SIZE, -1L);
        mApkUrl = jsonObject.optString(KEY_APP_URL);
        mMiBackupUrl = jsonObject.optString(KEY_MIUI_BACKUP_URL);
        mAMPAppId = jsonObject.optString(KEY_AMP_APP_ID);
    }

    public String getPkgName() {
        return mPkgName;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getAppLogo() {
        return mAppLogo;
    }

    public String getAppDesc() {
        return mAppDesc;
    }

    public String getAppVersionName() {
        return mAppVersionName;
    }

    public int getAppVersionCode() {
        return mAppVersionCode;
    }

    public long getApkSize() {
        return mApkSize;
    }

    public String getApkUrl() {
        return mApkUrl;
    }

    public void setApkUrl(String mApkUrl) {
        this.mApkUrl = mApkUrl;
    }

    public String getMiBackupUrl() {
        return mMiBackupUrl;
    }

    public String getAMPAppId() {
        return mAMPAppId;
    }

    @Override
    public String toString() {
        return "ProductData{" +
                "mAppName='" + mAppName + '\'' +
                ", mAppLogo='" + mAppLogo + '\'' +
                ", mAppDesc='" + mAppDesc + '\'' +
                ", mAppVersionName='" + mAppVersionName + '\'' +
                ", mAppVersionCode=" + mAppVersionCode +
                ", mApkSize=" + mApkSize +
                ", mMiBackupUrl=" + mMiBackupUrl +
                ", mAMPAppId=" + mAMPAppId +
                '}';
    }
}
