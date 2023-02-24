package com.afanty.bridge;

import android.content.Context;

import com.afanty.common.constants.Settings;
import com.afanty.internal.action.ActionUtils;
import com.afanty.models.Bid;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.SettingsSp;

public class BridgeManager {

    public static void increaseClickCount(Bid adData) {
        ActionUtils.increaseClickCount(adData);
    }

    private static void changeDefaultValue() {
        SettingsSp settingsSp = new SettingsSp(ContextUtils.getContext(), Settings.GDPR_SETTINGS);
        if (!settingsSp.contains(Settings.AL_RECORD_ENABLE))
            settingsSp.setBoolean(Settings.AL_RECORD_ENABLE, true);
        if (!settingsSp.contains(Settings.KEY_AD_GDPR_CONSENT))
            settingsSp.setBoolean(Settings.KEY_AD_GDPR_CONSENT, true);

    }

    public static int getResourceId(Context context, String resourceName) {
        try {
            return context.getResources().getIdentifier(resourceName, "layout", context.getPackageName());
        } catch (Exception e) {
        }
        return 0;
    }


    public static void initialize(Context context) {

        doRegisterAppLifeCycleListener(context);

        changeDefaultValue();
    }

    private static void doRegisterAppLifeCycleListener(Context context) {

        initLifecycleListener(context);
    }


    private static void initLifecycleListener(Context context) {
        if (context == null)
            context = ContextUtils.getContext();

        if (context == null)
            return;
    }

}
