package com.afanty.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afanty.BuildConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.utils.log.Logger;

import java.util.HashMap;

public class AppUtils {
    private final static String TAG = "AppUtils";
    private final static HashMap<String, String> keyMap = new HashMap<>();
    private static int APP_VERSION_CODE = -1;
    private static String APP_VERSION_NAME = "";

    public static int getSdkVerCode() {
        return BuildConfig.SDK_VERSION_CODE;
    }

    public static String getSdkVerName() {
        return BuildConfig.SDK_VERSION_NAME;
    }

    public static String getAppToken(Context context) {
        return getMetadataValue(context, AdConstants.MetaDataKey.AFT_TOKEN, "default");
    }

    public static String getChannel() {
        return getChannel(ContextUtils.getContext());
    }

    public static String getChannel(Context context) {
        return getMetadataValue(context, AdConstants.MetaDataKey.AFT_CHANNEL, "gp");
    }

    public static String getAppId() {
        return getAppId(ContextUtils.getContext());
    }

    public static String getAppId(Context context) {
        return getMetadataValue(context, "CLOUD_APPID", context.getPackageName());
    }

    public static String getAFTAppID(Context context) {
        String aftKey = getMetadataValue(context, AdConstants.MetaDataKey.AFT_APP_ID);
        Logger.d(TAG, "aftKey = " + aftKey);
        return aftKey;
    }

    public static String getAftAppKey(Context context) {
        String aftKey = getMetadataValue(context, AdConstants.MetaDataKey.AFT_APP_KEY);
        Logger.d(TAG, "aftKey = " + aftKey);
        return aftKey;
    }

    public static boolean isAppAzed(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int getAppVerCode(Context context) {
        if (APP_VERSION_CODE > 0) {
            return APP_VERSION_CODE;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = -1;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return APP_VERSION_CODE = versionCode;
    }

    public static String getAppVerName(Context context) {
        if (!TextUtils.isEmpty(APP_VERSION_NAME)) {
            return APP_VERSION_NAME;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return APP_VERSION_NAME = versionName;
    }

    public static boolean isExMode(Context context) {
        return TextUtils.equals(getChannel(context), "ex");
    }

    public static String getMetadataValue(@NonNull Context context, @NonNull String key) {
        return getMetadataValue(context, key, "");
    }

    public static String getMetadataValue(@NonNull Context context, @NonNull String key, String defaultValue) {
        String metadataValue = "";
        if (TextUtils.isEmpty(key)) {
            return "";
        }
        if (keyMap.containsKey(key)) {
            return keyMap.get(key);
        }

        String packageName = context.getPackageName();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Object value = appInfo.metaData.get(key);
            if (value != null) {
                metadataValue = String.valueOf(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(metadataValue)) {
            keyMap.put(key, metadataValue);
        }

        return TextUtils.isEmpty(metadataValue) ? defaultValue : metadataValue;
    }

    private static String getAppKeyFromConfig(Context context, String sourceKey) {
        return "";
    }

    public static String getInstallPackage(Context context, String packageName) {
        return CommonUtils.getInstallPackage(context, packageName);
    }
}
