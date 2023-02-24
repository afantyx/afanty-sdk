package com.afanty.video;

public interface VideoDownloadInterface {
    void preLoadVideo(String var1, VideoDownloadInterface.VideoDownLoadListener var2);

    void startLoadVideo(String var1, VideoDownloadInterface.VideoDownLoadListener var2);

    boolean isCachedCompleted(String var1);

    void removeCache(String var1);

    public interface VideoDownLoadListener {
        void onLoadSuccess(long var1);

        void onLoadError();
    }
}
