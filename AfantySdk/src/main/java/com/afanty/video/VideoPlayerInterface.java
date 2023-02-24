package com.afanty.video;

import android.view.Surface;
import android.view.TextureView;

public interface VideoPlayerInterface {
    void createPlayer();

    void releasePlayer();

    void startPlay(String url);

    void startPlay(String url, int startTime);

    void resumePlay();

    void pausePlay();

    void stopPlay();

    void seekTo(int msec);

    void reStart();

    void setDisplay(Surface surface);

    void setDisplay(TextureView textureView);

    void setVolume(int volume);

    boolean isPlaying();

    boolean isComplete();

    void setAutoPlay(boolean isAutoPlay);

    int getVideoDuration();

    int getCurrentPosition();

    void setPlayStatusListener(VideoPlayerInterface.PlayStatusListener playStatusListener);

    void setOnVideoSizeChangedListener(VideoPlayerInterface.OnVideoSizeChangedListener onVideoSizeChangedListener);

    void setOnProgressUpdateListener(VideoPlayerInterface.OnProgressUpdateListener onProgressUpdateListener);

    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(int width, int height, int visibleWidth, int visibleHeight);
    }

    public interface OnProgressUpdateListener {
        void onBufferingUpdate(int percent);

        void onProgressUpdate(int timeMs);

        void onMaxProgressUpdate(int duration);
    }

    public interface PlayStatusListener {
        void onStarted();

        void onPreparing();

        void onPrepared();

        void onPaused();

        void onStopped();

        void onCompleted();

        void onError(String reason, Throwable th);
    }
}
