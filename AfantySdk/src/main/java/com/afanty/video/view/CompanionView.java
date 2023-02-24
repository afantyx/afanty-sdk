package com.afanty.video.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.afanty.R;
import com.afanty.ads.AftImageLoader;
import com.afanty.models.Bid;
import com.afanty.utils.AdDataUtils;
import com.afanty.vast.VastCompanionAdConfig;
import com.afanty.vast.VastVideoConfig;
import com.afanty.vast.VastWebView;
import com.afanty.vast.utils.Dips;

public class CompanionView extends FrameLayout {
    private static final String TAG = "WebCompanionView";
    private CompanionViewListener mListener;

    public CompanionView(@NonNull Context context) {
        super(context);
    }

    public CompanionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompanionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCompanionViewListener(CompanionViewListener listener) {
        this.mListener = listener;
    }

    public void renderCompanionView(boolean isUseVastWeb, Bid adData, int orientation) {
        if (isUseVastWeb) {
            VastVideoConfig vastVideoConfig = AdDataUtils.getVastVideoConfig(adData);
            View vastCompanionAdView = createCompanionView(vastVideoConfig, orientation);
            if (vastCompanionAdView != null) {
                addView(vastCompanionAdView);
            } else {
                addImageView(adData);
            }
        } else {
            addImageView(adData);
        }
    }

    private void addImageView(Bid adData) {
        final ImageView baseImg = new ImageView(getContext());
        baseImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        AftImageLoader.getInstance().loadUri(getContext(), adData.getImageUrl(), baseImg, R.color.aft_black);
        final LayoutParams companionAdLayout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        companionAdLayout.gravity = Gravity.CENTER;
        baseImg.setLayoutParams(companionAdLayout);
        addView(baseImg);
        baseImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick();
            }
        });
    }

    private View createCompanionView(VastVideoConfig mVastVideoConfig, int orientation) {
        if (mVastVideoConfig == null)
            return null;
        VastCompanionAdConfig vastCompanionAdConfig = mVastVideoConfig.getVastCompanionAd(
                orientation == Configuration.ORIENTATION_PORTRAIT ? Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE);
        if (vastCompanionAdConfig == null) {
            return null;
        }
        VastWebView companionView = createWebCompanionView(getContext(), vastCompanionAdConfig);
        if (companionView == null)
            return null;
        final FrameLayout.LayoutParams companionAdLayout = new FrameLayout.LayoutParams(
                Dips.dipsToIntPixels(vastCompanionAdConfig.getWidth(), getContext()),
                Dips.dipsToIntPixels(vastCompanionAdConfig.getHeight(), getContext())
        );
        companionAdLayout.gravity = Gravity.CENTER;
        companionView.setLayoutParams(companionAdLayout);
        return companionView;
    }

    private VastWebView createWebCompanionView(@NonNull final Context context, final VastCompanionAdConfig vastCompanionAdConfig) {
        VastWebView companionView = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            companionView = VastWebView.createView(context, vastCompanionAdConfig.getVastResource());
            // STATIC
            companionView.setVastWebViewClickListener(new VastWebView.VastWebViewClickListener() {
                @Override
                public void onVastWebViewClick() {
                    // track companion click
                    if (mListener != null)
                        mListener.onClick();
                }
            });
            // IFRAME / HTML
            companionView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (mListener != null)
                        mListener.onClick();
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if (request.isForMainFrame()) {
                        if (mListener != null)
                            mListener.onReceivedError();
                    }
                    super.onReceivedError(view, request, error);
                }
            });
        }
        return companionView;
    }

    public interface CompanionViewListener {
        void onReceivedError();

        void onClick();
    }
}
