package com.afanty.vast.download;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afanty.common.download.DownloadListener;
import com.afanty.common.download.DownloadManager;

import java.net.URL;

public class VastDownloadManager {
    private static final String TAG = "VastDownloadManager";

    private Context mContext;

    private String mDownloadUrl;
    private String mDefaultEnds;

    public VastDownloadManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    public VastDownloadManager(@NonNull Context context, String url) {
        if (null != context) {
            this.mContext = context.getApplicationContext();
        }

        this.mDownloadUrl = url;
    }

    public void setDownLoadUrl(String url) {
        this.mDownloadUrl = url;
    }

    public void setDefaultEnds(String defaultEnds) {
        this.mDefaultEnds = defaultEnds;
    }

    public void startTask(final VastDownloadListener listener, final String adId, final String placementId, long expire) {
        if (null == listener) {
            return;
        }

        if (null == this.mContext || TextUtils.isEmpty(this.mDownloadUrl)) {
            listener.onDownloadFailed("", VastDownloadError.ERROR_PARAMS);
            return;
        }

        if (DownloadManager.hasCache(this.mDownloadUrl)) {
            listener.onDownloadSuccess(this.mDownloadUrl, DownloadManager.getCacheUrl(this.mDownloadUrl), 0);
            return;
        }
        final long startTime = System.currentTimeMillis();
        DownloadManager.start(this.mDownloadUrl, new DownloadListener() {
            @Override
            public void onStart(String url) {

            }

            @Override
            public void onFail(String url, String reason) {
                listener.onDownloadFailed(url, new VastDownloadError(6000, reason));
            }

            @Override
            public void onSuccess(String url, boolean iscache) {
                listener.onDownloadSuccess(url, DownloadManager.getCacheUrl(url), DownloadManager.getCacheSize(url));
            }
        });
    }
}
