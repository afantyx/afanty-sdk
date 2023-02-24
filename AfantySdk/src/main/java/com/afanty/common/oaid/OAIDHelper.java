package com.afanty.common.oaid;

import android.content.Context;
import android.text.TextUtils;

import com.afanty.common.UserInfoHelper;
import com.afanty.common.oaid.impl.HuaweiImpl;
import com.afanty.utils.SettingConfig;

public class OAIDHelper {
    private static volatile String oaid = null;

    public static void register(Context context) {
        if (context == null) {
            return;
        }
        tryGetOAID(context);
    }

    public static String getOAID(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        oaid = SettingConfig.getOAID();
        if (TextUtils.isEmpty(oaid) && supportedOAID(context)) {
            synchronized (OAIDHelper.class) {
                oaid = getOAID();
                if (TextUtils.isEmpty(oaid)) {
                    tryGetOAID(context);
                }
            }
        }
        if (oaid == null) {
            oaid = "";
        }
        return oaid;
    }

    private static boolean supportedOAID(Context context) {
        return new HuaweiImpl(context).supported();
    }

    private static String getOAID() {
        String oaid = OAIDHelper.oaid;
        if (oaid == null) {
            oaid = "";
        }
        return oaid;
    }

    private static void tryGetOAID(Context context) {
        new HuaweiImpl(context).doGet(new IGetter() {
            @Override
            public void onSuccess(String oaid) {
                if (TextUtils.isEmpty(oaid)) {
                    onError(new OAIDException("OAID is empty"));
                    return;
                }
                OAIDHelper.oaid = oaid;
                SettingConfig.setOAID(oaid);
            }

            @Override
            public void onError(Exception error) {
            }
        });
    }
}
