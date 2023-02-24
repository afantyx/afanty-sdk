package com.afanty.ads.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.afanty.ads.AftImageLoader;
import com.afanty.ads.VideoOptions;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.INativeAd;
import com.afanty.internal.nativead.InnerNativeAd;
import com.afanty.internal.view.InnerPlayerView;
import com.afanty.video.view.MediaViewCallBack;

public class PlayerView extends FrameLayout {
    private static final String TAG = "PlayerView";
    private InnerPlayerView mediaView;
    private IAdObserver.VideoLifecycleCallbacks mLifecycleCallbacks;

    public PlayerView(Context context) {
        super(context);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setVideoLifecycleCallbacks(@NonNull IAdObserver.VideoLifecycleCallbacks lifecycleCallbacks) {
        if (mediaView != null)
            mediaView.setVideoLifecycleCallbacks(lifecycleCallbacks);
        mLifecycleCallbacks = lifecycleCallbacks;
    }

    public void loadMediaView(final INativeAd nativeAd) {
        loadMediaView(nativeAd, null);
    }

    public void loadMediaView(final INativeAd nativeAd, VideoOptions videoOptions) {
        if (nativeAd == null)
            return;
        if (!(nativeAd instanceof InnerNativeAd))
            return;
        removeAllViews();

        final InnerNativeAd innerNativeAd = (InnerNativeAd) nativeAd;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (innerNativeAd.isVideoAd()) {
            mediaView = new InnerPlayerView(getContext());
            mediaView.setBid(innerNativeAd.getBid());
            mediaView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (videoOptions != null)
                mediaView.setVideoOptions(videoOptions);
            if (mLifecycleCallbacks != null)
                mediaView.setVideoLifecycleCallbacks(mLifecycleCallbacks);
            mediaView.setMediaViewListener(new MediaViewCallBack() {
                @Override
                public void onSurfaceTextureAvailable() {
                    mediaView.startPlay();
                }
            });
            addView(mediaView, params);
        } else {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(imageView);
            AftImageLoader.getInstance().loadUri(getContext(), innerNativeAd.getPosterUrl(), imageView);
        }
    }
}
