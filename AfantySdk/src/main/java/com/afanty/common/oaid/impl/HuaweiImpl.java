package com.afanty.common.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.afanty.common.oaid.IGetter;
import com.afanty.common.oaid.IOAID;
import com.afanty.common.oaid.OAIDException;
import com.afanty.common.oaid.OAIDService;


public class HuaweiImpl implements IOAID {
    private final Context context;
    private String packageName;

    public HuaweiImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        boolean ret = false;
        try {
            PackageManager pm = context.getPackageManager();
            if (pm.getPackageInfo("com.huawei.hwid", 0) != null) {
                packageName = "com.huawei.hwid";
                ret = true;
            } else if (pm.getPackageInfo("com.huawei.hwid.tv", 0) != null) {
                packageName = "com.huawei.hwid.tv";
                ret = true;
            } else {
                packageName = "com.huawei.hms";
                ret = pm.getPackageInfo(packageName, 0) != null;
            }
        } catch (Exception ignore) {
        }
        return ret;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                String oaid = Settings.Global.getString(context.getContentResolver(), "pps_oaid");
                if (!TextUtils.isEmpty(oaid)) {
                    getter.onSuccess(oaid);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        if (TextUtils.isEmpty(packageName) && !supported()) {
            getter.onError(new OAIDException("Huawei Advertising ID not available"));
            return;
        }
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage(packageName);
        OAIDService.bind(context, intent, getter, service -> {
            OpenDeviceIdentifierService anInterface = OpenDeviceIdentifierService.Stub.asInterface(service);
            if (anInterface.isOaidTrackLimited()) {
                throw new OAIDException("User has disabled advertising identifier");
            }
            return anInterface.getOaid();
        });
    }
}
