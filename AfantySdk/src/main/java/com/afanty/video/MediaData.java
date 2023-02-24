package com.afanty.video;

public class MediaData {
    private String mUrl;
    private boolean mAutoPlay;
    private int mDuration;
    private int mPlayPosition;
    private VideoPlayerState mCurrentState;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public boolean isAutoPlay() {
        return mAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getPlayPosition() {
        return mPlayPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.mPlayPosition = playPosition;
    }

    public VideoPlayerState getCurrentState() {
        return mCurrentState;
    }

    public void setCurrentState(VideoPlayerState currentState) {
        this.mCurrentState = currentState;
    }

    public void reset() {
        mUrl = "";
        mAutoPlay = false;
        mDuration = 0;
        mPlayPosition = 0;
        mCurrentState = VideoPlayerState.IDLE;
    }
}