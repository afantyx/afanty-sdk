package com.afanty.ads;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.afanty.utils.AdRoundTransform;
import com.afanty.utils.ViewUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class AftImageLoader {
    private static final String TAG = "ImageLoader";
    private static final DiskCacheStrategy DEFAULT_CACHE_STRATEGY = DiskCacheStrategy.AUTOMATIC;
    private static final int DEFAULT_THUMB_TIMEOUT = 3000;

    private static AftImageLoader sInstance;

    private AftImageLoader() {
    }

    public static AftImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (AftImageLoader.class) {
                if (sInstance == null)
                    sInstance = new AftImageLoader();
            }
        }
        return sInstance;
    }


    public void loadUri(Context context, String url, ImageView view) {
        loadAdImage(context, url, view, 0, null);
    }

    public void loadUri(Context context, String url, ImageView view, int defaultRes) {
        loadAdImage(context, url, view, defaultRes, null);
    }

    public void loadUri(Context context, String url, ImageView view, OnLoadedListener listener) {
        loadAdImage(context, url, view, 0, listener);
    }

    public void loadUri(Context context, String url, ImageView view, int defaultRes, OnLoadedListener listener) {
        loadAdImage(context, url, view, defaultRes, listener);
    }

    private void loadAdImage(Context context, final String url, final ImageView imageView, int defaultRes, final OnLoadedListener listener) {
        if (TextUtils.isEmpty(url) && defaultRes != 0) {
            imageView.setImageResource(defaultRes);
            return;
        }

        try {
            RequestManager requestManager = getRequestManager(context);
            RequestBuilder requestBuilder;
            if (isGifImgByUrl(url))
                requestBuilder = requestManager.asGif();
            else
                requestBuilder = requestManager.asDrawable();

            if (defaultRes != 0) {
                RequestOptions options = new RequestOptions()
                        .placeholder(defaultRes)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .timeout(DEFAULT_THUMB_TIMEOUT);
                requestBuilder.apply(options);
            }

            requestBuilder.load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (listener != null)
                        listener.onImageLoadResult(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (listener != null)
                        listener.onImageLoadResult(true);
                    return false;
                }
            }).into(imageView);
        } catch (Exception e) {
        }
    }

    public void loadLandingRoundCornerUrl(Context context, String url, ImageView imageView, int defaultResId, int roundDp) {
        loadRoundCornerUrl(context, url, imageView, defaultResId, roundDp, null, true);
    }

    @SuppressLint("CheckResult")
    private void loadRoundCornerUrl(Context context, final String url, ImageView imageView, int defaultResId, int roundDp, RequestListener<Object> listener, boolean needRoundCorner) {
        try {
            RequestOptions options = new RequestOptions().placeholder(defaultResId)
                    .diskCacheStrategy(DEFAULT_CACHE_STRATEGY);
            if (roundDp > 0 && needRoundCorner) {
                options.transform(new AdRoundTransform(roundDp));
            }

            RequestManager requestManager = getRequestManager(context);
            requestManager.addDefaultRequestListener(listener);
            requestManager.load(url).apply(options).into(imageView);
        } catch (Exception e) {
        }
    }

    private static RequestManager getRequestManager(Context context) {
        if (ViewUtils.activityIsDead(context))
            return Glide.with(context.getApplicationContext());
        return Glide.with(context);
    }

    private boolean isGifImgByUrl(String url) {
        if (URLUtil.isNetworkUrl(url)) {
            String filename = URLUtil.guessFileName(url, null, null);
            if (!TextUtils.isEmpty(filename)) url = filename;
        }
        return url.toLowerCase().endsWith("gif");
    }

    public interface OnLoadedListener {
        void onImageLoadResult(boolean isSuccess);
    }
}
