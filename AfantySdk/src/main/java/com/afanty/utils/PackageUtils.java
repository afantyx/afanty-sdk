package com.afanty.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.afanty.internal.internal.AdConstants;

public class PackageUtils {

    public static final int APP_STATUS_INSTALLED = 1;
    public static final int APP_STATUS_NEED_UPGRADE = 2;

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            if (context == null) {
                context = ContextUtils.getContext();
            }
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getAppStatus(Context context, String packageName, int versionCode) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            if (info.versionCode >= versionCode)
                return AdConstants.AppInstall.APP_STATUS_INSTALLED;
            else
                return AdConstants.AppInstall.APP_STATUS_NEED_UPGRADE;
        } catch (PackageManager.NameNotFoundException e) {
            return AdConstants.AppInstall.APP_STATUS_UNINSTALL;
        }
    }

}
