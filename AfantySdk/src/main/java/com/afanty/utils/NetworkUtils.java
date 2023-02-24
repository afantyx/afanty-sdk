package com.afanty.utils;

import static android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.Nullable;


public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    public static boolean isConnected(Context context) {
        Pair<Boolean, Boolean> connect = checkConnected(context);
        return connect.first || connect.second;
    }

    public static Pair<Boolean, Boolean> checkConnected(Context context) {
        boolean isMobileConnected = false;
        boolean isWifiConnected = false;
        try {
            NetworkInfo networkInfo = getNetWorkInfo(context);
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    int netType = networkInfo.getType();
                    if (netType == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConnected = true;
                    } else if (netType == ConnectivityManager.TYPE_WIFI) {
                        isWifiConnected = true;
                    } else {
                        isMobileConnected = true;
                    }
                }
            }
        } catch (Exception e) {
            isMobileConnected = false;
            isWifiConnected = false;
        }
        return new Pair<Boolean, Boolean>(isMobileConnected, isWifiConnected);
    }

    /**
     * Determine if the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            result = isNetworkAvailable(connectivityManager);
        } catch (Exception e) {
        }
        return result;
    }

    private static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
        if (connectivityManager != null && connectivityManager.getAllNetworkInfo() != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            final int length = networkInfos.length;
            for (int i = 0; i < length; i++) {
                if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void gotoAuthNetworkSetting(Context context) {
        WifiManager wifiManager = (WifiManager) ContextUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            boolean isEnable = wifiManager.isWifiEnabled();
            if (isEnable && DeviceUtils.isSimReady(context)) {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    DeviceUtils.setMobileDataEnabled(context, true);
                } else {
                    goto4GSetting(context);
                }
            } else {
                gotoWifiSetting(context);
            }
        } else {
            gotoWifiSetting(context);
        }
    }

    /**
     * Jump to Wifi settings
     */
    public static void gotoWifiSetting(Context context) {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
                intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            else
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * Jump to 4G settings
     *
     * @param context
     */
    private static void goto4GSetting(Context context) {
        try {
            Intent intent = new Intent(ACTION_DATA_ROAMING_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            gotoWifiSetting(context);
        }
    }

    public static int getNetworkType(Context ctx) {
        try {
            NetworkInfo info = getNetWorkInfo(ctx);
            return (info != null) ? info.getType() : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean hasNetWork(Context context) {
        try {
            NetworkInfo networkInfo = getNetWorkInfo(context);
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {

        }
        return false;
    }

    @Nullable
    public static NetworkInfo getNetWorkInfo(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                return connectivity.getActiveNetworkInfo();
            }
        } catch (Exception e) {
        }
        return null;
    }

}
