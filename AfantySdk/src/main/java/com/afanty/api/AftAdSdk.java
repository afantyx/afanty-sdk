package com.afanty.api;

import android.content.Context;

import androidx.annotation.NonNull;
import com.afanty.common.UserInfoHelper;
import com.afanty.core.InitProxy;
import com.afanty.utils.AppUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.SettingConfig;

public class AftAdSdk {
    private static Boolean hasAcceptConsent;

    public static void init(Context context) throws Exception {
        ContextUtils.setContext(context);
        AftAdSettings adSettings = new AftAdSettings.Builder()
                .build();
        InitProxy.init(context, adSettings);
    }

    public static void init(Context context, AftAdSettings aftAdSettings) throws Exception {

        InitProxy.init(context, aftAdSettings);
    }

    public static boolean hasInitialized() {
        return InitProxy.hasInitialized();
    }

    public static boolean canCollectUserInfo() {
        if (!InitProxy.hasInitialized())
            return false;
        if (hasAcceptConsent == null) {
            hasAcceptConsent = UserInfoHelper.canCollectUserInfo();
        }
        return hasAcceptConsent;
    }

    public static AftAdSettings.Builder getDefaultAdSettingsBuilder() {
        return new AftAdSettings.Builder();
    }

    public static void setGDPRStatus(@NonNull Context context, boolean hasAccept) {
        hasAcceptConsent = hasAccept;
        ContextUtils.setContext(context);

        SettingConfig.setEUGdpr(hasAccept);
    }


    public static int getSdkVerCode() {
        return AppUtils.getSdkVerCode();
    }

    public static String getSdkVerName() {
        return AppUtils.getSdkVerName();
    }

}
