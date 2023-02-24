package com.afanty.common.download;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;

public class DownloadManager {

    public static void start(String url,DownloadListener listener){
        if (TextUtils.isEmpty(url))
            listener.onFail(url,"url is empty");
        if (!isNetUrl(url))
            listener.onSuccess(url,true);
        Downloader downloader = new Downloader();
        downloader.downnload(url,listener);
    }

    public static boolean hasCache(String url){
        if (TextUtils.isEmpty(url))
            return false;
        if (isNetUrl(url)){
            File f = getCache(url);
            return (f!=null&&f.exists());
        } else {
            File f = getCache(url);
            return (f!=null&&f.exists());
        }

    }

    public static File getCache(String url){
        if (TextUtils.isEmpty(url))
            return null;
        if (!isNetUrl(url))
            return null;
        Downloader downloader = new Downloader();
        return downloader.getCache(url);
    }

    public static long getCacheSize(String url){
        File f = getCache(url);
        if (f!=null && f.exists())
            return f.length();
        return 0;
    }

    public static String getCacheUrl(String url){
        if (TextUtils.isEmpty(url))
            return url;
        if (!isNetUrl(url))
            return url;
        Downloader downloader = new Downloader();
        File cache =  downloader.getCache(url);
        if (cache!=null && cache.exists())
            return cache.toString();
        return url;
    }

    public static boolean isNetUrl(@NonNull String url){
        return url.startsWith("http");
    }
}
