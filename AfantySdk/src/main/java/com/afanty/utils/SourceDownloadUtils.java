package com.afanty.utils;

import android.text.TextUtils;
import android.util.Pair;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.common.download.DownloadListener;
import com.afanty.common.download.DownloadManager;
import com.afanty.config.BasicAftConfig;
import com.afanty.internal.internal.CreativeType;
import com.afanty.internal.internal.ProductData;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.video.PlayerUrlHelper;
import com.afanty.video.VideoDownloadInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.afanty.models.AdmBean.IMG_POSTER_TYPE;

public class SourceDownloadUtils {
    private static final String TAG = "SourceDownloadUtils";

    public static boolean downloadImageAndCheckReady(Bid bid) {
        AdmBean.ImgBean imgBean = bid.getImgBean(IMG_POSTER_TYPE);
        if (imgBean != null && !TextUtils.isEmpty(imgBean.getUrl())) {
            File file = downloadImageWithGlide(imgBean.getUrl(), true);
            return file != null && file.exists() && file.length() >= 1;
        } else {
            return false;
        }
    }

    public static void tryDownloadImages(Bid bid) {
        AdmBean.ImgBean imgBean = bid.getImgBean(IMG_POSTER_TYPE);
        if (imgBean != null && !TextUtils.isEmpty(imgBean.getUrl())) {
            downloadImageWithGlide(imgBean.getUrl());
        }
    }


    public static File downloadImageWithGlide(String url) {
        return BasicSourceDownloadUtils.downloadImageWithGlide(url);
    }

    private static File downloadImageWithGlide(String url, boolean immediate) {
        return BasicSourceDownloadUtils.downloadImageWithGlide(url, immediate);
    }

    public static boolean hasDownloadVideo(String url) {
        return BasicSourceDownloadUtils.hasDownloadVideo(url);
    }

    private static boolean hasDownloadVideo(Bid bid) {
        File f1 = DownloadManager.getCache(PlayerUrlHelper.getVideoDownloadUrl(bid, false));
        File f2 = DownloadManager.getCache(bid.getVastPlayUrl());
        return (f1!=null&&f1.exists())||(f2!=null&&f2.exists());
    }

    public static void tryLoadVideoResource(final Bid bid) {
        tryLoadVideoResource(bid, null, -1);
    }

    public static void tryLoadVideoResource(final Bid bid, final VideoDownloadInterface.VideoDownLoadListener videoDownLoadListener, long timeout) {
        final long startTime = System.currentTimeMillis();
        String url;
        String playUrl = PlayerUrlHelper.getVideoPlayUrl(bid);
        if (!TextUtils.isEmpty(playUrl)) {
            url = playUrl;
        } else {
            url = bid.getVastPlayUrl();
        }
        final String finalUrl = url;
        if (TextUtils.isEmpty(finalUrl)) {
            if (videoDownLoadListener != null)
                videoDownLoadListener.onLoadSuccess(0);
            return;
        }

        if (DownloadManager.hasCache(finalUrl)) {
            if (videoDownLoadListener != null)
                videoDownLoadListener.onLoadSuccess(0);
        } else {
            DownloadManager.start(finalUrl, new DownloadListener() {
                @Override
                public void onStart(String url) {
                }

                @Override
                public void onFail(String url, String reason) {
                    ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
                        @Override
                        public void callBackOnUIThread() {
                            if (videoDownLoadListener!=null)
                                videoDownLoadListener.onLoadError();
                        }
                    });
                }

                @Override
                public void onSuccess(String url, boolean iscache) {
                    ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
                        @Override
                        public void callBackOnUIThread() {
                            if (videoDownLoadListener!=null)
                                videoDownLoadListener.onLoadSuccess(System.currentTimeMillis()-startTime);
                        }
                    });
                }
            });
        }
    }
}
