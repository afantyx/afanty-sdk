package com.afanty.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.api.AftAdSdk;
import com.afanty.api.AftAdSettings;
import com.afanty.base.BasicSdkProxy;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.host.AfantyHosts;
import com.afanty.request.OutParamsHelper;
import com.afanty.utils.AppUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.log.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class InitProxy {
    private static final String TAG = "InitProxy";
    private static final AtomicBoolean hasInitialized = new AtomicBoolean(false);
    private static boolean useExoPlayer = true;
    private static long initTime;
    public static long sAppOpenTime;

    public static void init(Context context, AftAdSettings aftAdSettings) throws Exception {
        Logger.i(TAG, "#Init hasInitialized = " + hasInitialized);
        if (hasInitialized.get()) {
            return;
        }

        context = context.getApplicationContext();
        if (!(context instanceof Application))
            throw new Exception("Should init AFTAdSdk in Application or Activity");

        hasInitialized.set(true);
        initTime = System.currentTimeMillis();

        initBasicSdk(context);

        initFuncWithParam(aftAdSettings);

        initCommonValues();

        BridgeManager.initialize(context);

        ThreadManager.getInstance().run(new DelayRunnableWork() {
            @Override
            public void execute() {
                AftAdSdk.canCollectUserInfo();
            }
        });
        // always on the last line
        Logger.out("Init finished.");
    }

    private static void initBasicSdk(Context context) {
        BasicSdkProxy.init(context);

        if (AppUtils.isExMode(ContextUtils.getContext())) {
            BasicSdkProxy.setIsForceGpType(false);
        }

        BasicSdkProxy.setSdkVerCode(7000002);
    }

    private static void initFuncWithParam(AftAdSettings adSettings) {

        initBidHost(adSettings);

        initBidrequest(adSettings);
    }

    private static void initCommonValues() {
        initFromGpValue();
        BasicSdkProxy.initCommonValues();
    }

    public static boolean hasInitialized() {
        return hasInitialized.get();
    }

    private static void initBidHost(AftAdSettings adSettings) {
        String bidHost = adSettings.getBidHost();
        if (!TextUtils.isEmpty(bidHost)) {
            AfantyHosts.setBidHost(bidHost);
        }
    }

    private static void initBidrequest(AftAdSettings aftAdSettings){
        OutParamsHelper.setDeviceInfo(aftAdSettings.getDeviceInfo());
        OutParamsHelper.setAppInfo(aftAdSettings.getApp());
    }

    public static boolean isUseExoPlayer() {
        return useExoPlayer;
    }

    private static void initFromGpValue() {
        ThreadManager.getInstance().run(new DelayRunnableWork() {
            @Override
            public void execute() {
                Context context = ContextUtils.getContext();
                if (context == null) return;
                try {
                    String install = AppUtils.getInstallPackage(context, context.getPackageName());
                } catch (Exception e) {
                }
            }
        });
    }
}
