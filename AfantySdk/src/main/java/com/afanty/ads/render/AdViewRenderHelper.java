package com.afanty.ads.render;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.afanty.ads.AftImageLoader;


public class AdViewRenderHelper {

    public static void loadImage(Context context, String url, ImageView view) {
        loadImage(context, url, view, 0);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int defaultResId) {
        if (imageView == null)
            return;
        if (TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            AftImageLoader.getInstance().loadUri(context, url, imageView, defaultResId);
        }
    }
}
