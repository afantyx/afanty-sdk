package com.afanty.video.presenter;

public interface VideoPresenterListener {

    void onPlayStatusStarted();

    void onPlayStatusPreparing();

    void onPlayStatusPrepared();

    void onPlayStatusPause();

    void onPlayStatusStopped();

    void onPlayStatusCompleted();

    void onPlayStatusError(String reason, Throwable th);

    void doAdjustVideoSize(int width, int height);

    void onBufferingUpdate(int percent);

    void onProgressUpdate(int duration, int progress);

    /**
     * For native media view,need getProgress when MediaPlayer not Playing.
     * */
    void onProgressUpdateWhenNotPlay(int duration, int progress);

    void onMaxProgressUpdate(int duration);

    void restart();

    void start();
}

