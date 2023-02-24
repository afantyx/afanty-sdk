package com.afanty.utils;

import android.text.TextUtils;

import com.afanty.common.download.DownloadManager;
import com.afanty.config.BasicAftConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class BasicSourceDownloadUtils {
    private static final String TAG = "BasicSourceDownloadUtils";

    public static final String AD_STATISTICS_TAG = "ad_statistic";

    public static File downloadImageWithGlide(String url) {
        if (TextUtils.isEmpty(url))
            return null;
        return downloadImageWithGlide(url, false);
    }

    public static File downloadImageWithGlide(String url, boolean immediate) {
        if (TextUtils.isEmpty(url))
            return null;
        try {
            RequestOptions requestOptions = new RequestOptions()
                    .timeout(BasicAftConfig.getDownloadImagesTimeout())
                    .priority(immediate ? Priority.IMMEDIATE : Priority.NORMAL);
            return Glide.with(ContextUtils.getContext()).download(url).apply(requestOptions).submit().get();
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean hasDownloadImageWithGlide(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        try {
            RequestManager requestManager = Glide.with(ContextUtils.getContext());
            File file = requestManager.downloadOnly().load(url).apply(new RequestOptions().onlyRetrieveFromCache(true)).submit().get();
            return file != null && file.exists() && file.length() > 1;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean hasDownloadVideo(String url) {
        return DownloadManager.hasCache(url);
    }

}
