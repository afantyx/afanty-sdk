package com.afanty.base;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.common.oaid.OAIDHelper;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.SettingConfig;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicSdkProxy {
    private static final String TAG = "SDK.INIT";
    private static Boolean isForceGpType;
    private static final String KEY_INIT_TIME = "sdk_init_time";
    private static int SDK_VERSION_CODE = -1;

    private static final AtomicBoolean hasInitialized = new AtomicBoolean(false);
    private static boolean isPromotionUser;

    public static void init(Context context) {
        ContextUtils.setContext(context);

        hasInitialized.set(true);
    }

    public static void initCommonValues() {
        initWebViewUA();
        OAIDHelper.register(ContextUtils.getContext());
    }


    private static void initWebViewUA() {
        String mUserAgent = CommonUtils.getWebViewUA();
        if (!TextUtils.isEmpty(mUserAgent)) {
            return;
        }

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void callBackOnUIThread() {
                long start = System.currentTimeMillis();
                String userAgent = null;
                Context context = ContextUtils.getContext();
                try {
                    Constructor constructor = WebSettings.class.getDeclaredConstructor(new Class[]{Context.class, WebView.class});
                    constructor.setAccessible(true);
                    userAgent = ((WebSettings) constructor.newInstance(new Object[]{ContextUtils.getContext(), null})).getUserAgentString();
                    constructor.setAccessible(false);
                } catch (Throwable ex1) {
                    try {
                        userAgent = new WebView(ContextUtils.getContext()).getSettings().getUserAgentString();
                    } catch (Throwable ex2) {
                    }
                } finally {
                    CommonUtils.disableAccessibility(context);
                }
                if (!TextUtils.isEmpty(userAgent)) {
                    ContextUtils.add("ua", userAgent);
                    SettingConfig.setWebUA(userAgent);
                }
            }
        });
    }

    public static boolean hasInitialized() {
        return hasInitialized.get();
    }

    public static void setIsForceGpType(boolean isForceGpType) {
        if (BasicSdkProxy.isForceGpType == null)
            BasicSdkProxy.isForceGpType = isForceGpType;
    }

    public static void setSdkVerCode(int sdkVerCode) {
        SDK_VERSION_CODE = sdkVerCode;
    }
}
