package com.afanty.internal.webview.jsinterface;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.afanty.utils.DeviceUtils;

public class AftBridge {
    private final Handler mHandler = new Handler();
    private ActionManager mActionManager;
    private ResultBack mResultBack;
    private Context mContext;

    public AftBridge(Context context, ResultBack resultBack) {
        mActionManager = new ActionManager(context);
        mResultBack = resultBack;
        mContext = context;
    }

    @JavascriptInterface
    public void asyncInvoke(final String portal, final String action, final String callbackName, final String jsonParam) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mActionManager.findAndExec(portal, action, callbackName, jsonParam, mResultBack);
                } catch (Exception e) {
                }
            }
        });
    }

    @JavascriptInterface
    public String syncInvoke(String portal, String action, String jsonParam) {
        return mActionManager.findAndExec(portal, action, null, jsonParam, mResultBack);
    }

    @JavascriptInterface
    public String getGAID() {
        return DeviceUtils.getGAID(mContext);
    }
}
