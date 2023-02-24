package com.afanty.internal.webview;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import com.afanty.utils.CommonUtils;

public class WebViewCache {

    private static volatile WebViewCache mInstance = null;

    private WebView mSingleWebView;

    private WebViewCache() {
    }

    public static synchronized WebViewCache getInstance() {
        if (mInstance == null) {
            synchronized (WebViewCache.class) {
                if (mInstance == null)
                    mInstance = new WebViewCache();
            }
        }
        return mInstance;
    }

    public WebView getSingleWebView(Context context) {
        if (mSingleWebView == null) {
            mSingleWebView = new WebView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                try {
                    mSingleWebView.removeJavascriptInterface("searchBoxJavaBridge_");
                    mSingleWebView.removeJavascriptInterface("accessibility");
                    mSingleWebView.removeJavascriptInterface("accessibilityTraversal");
                } catch (Exception e) {
                }
            }
            CommonUtils.disableAccessibility(context);
        }
        mSingleWebView.stopLoading();
        return mSingleWebView;
    }

}
