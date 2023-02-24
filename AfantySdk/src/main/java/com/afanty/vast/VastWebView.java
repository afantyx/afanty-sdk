package com.afanty.vast;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.afanty.internal.host.AfantyHosts;
import com.afanty.internal.webview.BaseWebView;
import com.afanty.vast.utils.Preconditions;

/**
 * A WebView customized for Vast video needs.
 */
public class VastWebView extends BaseWebView {
    public interface VastWebViewClickListener {
        void onVastWebViewClick();
    }

    @Nullable
    VastWebViewClickListener mVastWebViewClickListener;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    VastWebView(Context context) {
        super(context);

        disableScrollingAndZoom();
        getSettings().setJavaScriptEnabled(true);

        setBackgroundColor(Color.TRANSPARENT);
        setOnTouchListener(new VastWebViewOnTouchListener());
        setId(View.generateViewId());
    }

    public void loadData(String data) {
        loadDataWithBaseURL(AfantyHosts.getBidHostUrl(), data,
                "text/html", "UTF-8", null);
    }

    public void setVastWebViewClickListener(@NonNull VastWebViewClickListener vastWebViewClickListener) {
        mVastWebViewClickListener = vastWebViewClickListener;
    }

    private void disableScrollingAndZoom() {
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    /**
     * Creates and populates a webview.
     *
     * @param context      the context.
     * @param vastResource A resource describing the contents of the webview
     * @return a fully populated webview
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    public static VastWebView createView(@NonNull final Context context,
                                         @NonNull final VastResource vastResource) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(vastResource);

        VastWebView webView = new VastWebView(context);
        vastResource.initializeWebView(webView);

        return webView;
    }

    /**
     * Custom on touch listener to easily detect clicks on the entire WebView.
     */
    class VastWebViewOnTouchListener implements View.OnTouchListener {
        private boolean mClickStarted;
        private float downX, downY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mClickStarted = true;
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mClickStarted) {
                        return false;
                    }
                    mClickStarted = false;
                    if (Math.abs(event.getX() - downX) <= 20 && Math.abs(event.getY() - downY) <= 20) {
                        if (mVastWebViewClickListener != null) {
                            mVastWebViewClickListener.onVastWebViewClick();
                        }
                    }
            }

            return false;
        }
    }

    @Deprecated
    @NonNull
    VastWebViewClickListener getVastWebViewClickListener() {
        return mVastWebViewClickListener;
    }
}
