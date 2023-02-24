package com.afanty.internal.webview;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.webview.jsinterface.JsTagBridge;
import com.afanty.internal.webview.jsinterface.AftBridge;
import com.afanty.internal.webview.jsinterface.ResultBack;

public class AftWebView extends BaseWebView {
    private JsTagBridge mJsTagBridge;
    private AftBridge mAftBridge;
    protected ResultBack mResultBack;
    private boolean isCarousel;
    private int mXActionDown;
    private OnOverScrollListener mOnOverScrollListener;

    public AftWebView(Context context) {
        super(context);
        enableJavascriptCaching();
        disableScrollingAndZoom();

        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mResultBack = new ResultBack() {
            @Override
            public void onResult(final String callbackName, final String response) {
                ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
                    @Override
                    public void callBackOnUIThread() {
                        loadJS(callbackName, response);
                    }
                });
            }
        };
        mJsTagBridge = new JsTagBridge();
        mJsTagBridge.setIJSBrigeListener(new JsTagBridge.IJSBrigeListener() {
            @Override
            public void inCarousel() {
                isCarousel = true;
            }
        });
        mAftBridge = new AftBridge(context, mResultBack);
        addJavascriptInterface(mJsTagBridge, "adJsTagBrowser");
        byte[] uri_byte_path = {115, 104, 97, 114, 101, 105, 116};
        addJavascriptInterface(mAftBridge, new String(uri_byte_path) + "Bridge");

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isCarousel) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mXActionDown = (int) motionEvent.getX();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && Math.abs((int) motionEvent.getX() - mXActionDown) > 5) {
                this.getParent().requestDisallowInterceptTouchEvent(true);
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                this.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }

        return super.onTouchEvent(motionEvent);
    }

    public AftWebView(Context context, boolean enableJsBridge) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);
        disableScrollingAndZoom();

        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        if (enableJsBridge) {
            mResultBack = new ResultBack() {
                @Override
                public void onResult(final String callbackName, final String response) {
                    ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
                        @Override
                        public void callBackOnUIThread() {
                            loadJS(callbackName, response);
                        }
                    });
                }
            };
            mJsTagBridge = new JsTagBridge();
            mAftBridge = new AftBridge(context, mResultBack);
            addJavascriptInterface(mJsTagBridge, "adJsTagBrowser");
            byte[] uri_byte_path = {115, 104, 97, 114, 101, 105, 116};
            addJavascriptInterface(mAftBridge, new String(uri_byte_path) + "Bridge");

        }
    }

    private void disableScrollingAndZoom() {
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);
    }

    public void setJSStatsParams(String adId, String placementId, String creativeId, String formatId) {
        if (mJsTagBridge != null) {
            mJsTagBridge.setJSStatsParams(adId, placementId, creativeId, formatId);
        }
    }

    public void loadJS(String callbackName, Object param) {
        String callJsCode = "";
        if (param != null) {
            callJsCode = String.format("javascript:%s('%s')", callbackName, param.toString());
        } else {
            callJsCode = String.format("javascript:%s()", callbackName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                evaluateJavascript(callJsCode, null);
            } catch (Throwable t) {
                loadUrl(callJsCode);
            }
        } else {
            loadUrl(callJsCode);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (mOnOverScrollListener != null) {
            mOnOverScrollListener.onOverScrolled(this, scrollY == 0 && clampedY);
        }
    }

    public void setOnOverScrollListener(OnOverScrollListener listener) {
        this.mOnOverScrollListener = listener;
    }

    public interface OnOverScrollListener {
        void onOverScrolled(AftWebView v, boolean onTop);
    }
}
