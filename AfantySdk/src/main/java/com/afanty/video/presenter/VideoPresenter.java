package com.afanty.video.presenter;

import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.afanty.video.MediaStatsData;
import com.afanty.video.VideoManager;
import com.afanty.video.VideoPlayerInterface;

public class VideoPresenter {
    private static final String TAG = "Ad.VideoPresenter";
    private VideoPlayerInterface mPlayerWrapper;
    private VideoPresenterListener mVideoPresenterListener;
    private VideoStatsListener mVideoStatsListener;
    private MediaStatsData mMediaStatsData;
    private String mPlayUrl;

    private int mWidth;
    private int mHeight;

    private boolean mIsMute = true;
    private int mVideoDuration;

    private static final int VIDEO_STATS_SPACING = 500;

    private long mStartPlayTime = 0;
    private int mStartPlayTimes = 0;
    private VideoStatus mVideoPlayLastStated = VideoStatus.IDLE;

    public enum VideoStatus {
        IDLE, START, QUARTER, HALF, THREEQUARTER, STOP, COMPLETE
    }

    public VideoPresenter(VideoPresenterListener videoPresenterListener) {
        this.mVideoPresenterListener = videoPresenterListener;
        mVideoStatsListener = new VideoStatsWrapper();
    }

    public void setVideoData(@NonNull MediaStatsData mediaStatsData) {
        this.mMediaStatsData = mediaStatsData;
    }

    public void initController() {
        mPlayerWrapper = VideoManager.getInstance().getVideoPlayer();
        if (mPlayerWrapper != null) {
            mPlayerWrapper.releasePlayer();
            mPlayerWrapper.createPlayer();
            mPlayerWrapper.setPlayStatusListener(mPlayStatusListener);
            mPlayerWrapper.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
            mPlayerWrapper.setOnProgressUpdateListener(mProgressUpdateListener);
        }
    }

    public void start(String url, boolean autoPlay, boolean isMute, int startTime) {
        if (mPlayerWrapper == null) {
            try {
                initController();
            } catch (Exception e) {
                return;
            }
        }

        mVideoPresenterListener.start();
        mPlayerWrapper.setAutoPlay(autoPlay);
        mPlayUrl = url;
        mIsMute = isMute;
        mStartPlayTime = System.currentTimeMillis();

        setMuteState(mIsMute);
        mPlayerWrapper.startPlay(url, startTime);

        if (mStartPlayTimes == 0)
            mStartPlayTimes++;
    }

    public void setMuteState(boolean isMute) {
        if (mPlayerWrapper != null)
            mPlayerWrapper.setVolume(isMute ? 0 : 100);
        if (mVideoStatsListener == null)
            return;
        if (isMute) {
            mVideoStatsListener.setMuteStats(mMediaStatsData);
        } else {
            mVideoStatsListener.setUnMuteStats(mMediaStatsData);
        }
    }

    public boolean isPlaying() {
        return mPlayerWrapper != null && mPlayerWrapper.isPlaying();
    }

    public VideoPlayerInterface getPlayer() {
        return mPlayerWrapper;
    }

    public boolean isComplete() {
        return mPlayerWrapper != null && mPlayerWrapper.isComplete();
    }

    public void releasePlayer() {
        if (mPlayerWrapper == null)
            return;
        mPlayerWrapper.releasePlayer();

        mPlayerWrapper.setPlayStatusListener(null);
        mPlayerWrapper.setOnVideoSizeChangedListener(null);
        mPlayerWrapper.setOnProgressUpdateListener(null);

        mPlayerWrapper = null;
    }

    public void reStart() {
        if (mPlayerWrapper == null) {
            start(mPlayUrl, true, mIsMute, 0);
            return;
        }

        mVideoPresenterListener.restart();
        mPlayerWrapper.reStart();
        mStartPlayTimes++;
    }

    public void pausePlay() {
        if (mPlayerWrapper == null)
            return;

        mPlayerWrapper.pausePlay();
    }

    public void stopPlay() {
        if (mPlayerWrapper == null)
            return;

        mPlayerWrapper.stopPlay();

        if (mVideoStatsListener != null)
            mVideoStatsListener.onStopPlay();
    }

    public void resumePlay() {
        if (mPlayerWrapper == null)
            return;

        mPlayerWrapper.resumePlay();

        if (mVideoStatsListener != null)
            mVideoStatsListener.onResumePlay(mMediaStatsData);
    }

    public void seekTo(int msec) {
        if (mPlayerWrapper == null)
            return;

        mPlayerWrapper.seekTo(msec);
    }

    public boolean soundClick() {
        if (mIsMute) {
            setMuteState(mIsMute = false);
        } else {
            setMuteState(mIsMute = true);
        }
        return mIsMute;
    }

    private void statsStart() {
        if (mPlayerWrapper == null)
            return;
        if (mVideoStatsListener != null)
            mVideoStatsListener.onPlayStatsStarted(mMediaStatsData, 0);
    }

    private void statsPlay(int startTime) {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onPlayStatsPlayed(mMediaStatsData, mStartPlayTimes);
    }

    private void statsComplete() {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onPlayStatsCompleted(mMediaStatsData, mPlayerWrapper != null ? mPlayerWrapper.getVideoDuration() : mVideoDuration, mStartPlayTimes);

        VideoManager.getInstance().clearCurPosition(mPlayUrl);
        mVideoDuration = 0;
    }

    public void setDisplay(Surface surface) {
        if (mPlayerWrapper != null) {
            mPlayerWrapper.setDisplay(surface);
        }
    }

    public void setTextureDisplay(TextureView textureView) {
        if (mPlayerWrapper != null) {
            mPlayerWrapper.setDisplay(textureView);
        }
    }

    private void statsProgress(int progress) {
        if (mPlayerWrapper == null)
            return;

        if (mVideoPlayLastStated == VideoStatus.IDLE || mVideoPlayLastStated == VideoStatus.COMPLETE)
            return;

        if (mVideoStatsListener != null)
            mVideoStatsListener.onProgressUpdateStats(progress);

        int duration = mPlayerWrapper.getVideoDuration();
        int quarter = duration / 4;
        int half = duration / 2;
        int threeQuarter = duration / 4 * 3;

        if (progress >= quarter - VIDEO_STATS_SPACING && progress <= quarter + VIDEO_STATS_SPACING && mVideoPlayLastStated == VideoStatus.START) {
            if (mVideoStatsListener != null)
                mVideoStatsListener.onProgressUpdateQuarter(mMediaStatsData, quarter, mStartPlayTimes);
            mVideoPlayLastStated = VideoStatus.QUARTER;
        } else if (progress >= half - VIDEO_STATS_SPACING && progress <= half + VIDEO_STATS_SPACING && mVideoPlayLastStated == VideoStatus.QUARTER) {
            if (mVideoStatsListener != null)
                mVideoStatsListener.onProgressUpdateHalf(mMediaStatsData, half, mStartPlayTimes);
            mVideoPlayLastStated = VideoStatus.HALF;
        } else if (progress >= threeQuarter - VIDEO_STATS_SPACING && progress <= threeQuarter + VIDEO_STATS_SPACING && mVideoPlayLastStated == VideoStatus.HALF) {
            if (mVideoStatsListener != null)
                mVideoStatsListener.onProgressUpdateThreeQuarter(mMediaStatsData, threeQuarter, mStartPlayTimes);
            mVideoPlayLastStated = VideoStatus.THREEQUARTER;
        }
    }

    private void statsVideoResult() {
        if (mPlayerWrapper == null)
            return;

        if (mPlayerWrapper.getCurrentPosition() == 0 || mPlayerWrapper.getVideoDuration() == 0)
            return;

        if (mVideoStatsListener != null)
            mVideoStatsListener.onMediaEventResult(mMediaStatsData, mPlayerWrapper.getVideoDuration(), 0, mPlayerWrapper.getCurrentPosition(), mHeight, mWidth);
    }

    private void statsPause() {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onPause(mMediaStatsData);

    }

    public void statsSkip() {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onSkip(mMediaStatsData);

    }

    public void statsClose() {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onClose(mMediaStatsData);

    }

    public void statsCreateView() {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onCreateView(mMediaStatsData);

    }

    public void statsError(String reason) {
        if (mVideoStatsListener != null)
            mVideoStatsListener.onPlayStatsError(mMediaStatsData, mPlayUrl, System.currentTimeMillis() - mStartPlayTime, reason);

    }

    private VideoPlayerInterface.PlayStatusListener mPlayStatusListener = new VideoPlayerInterface.PlayStatusListener() {

        @Override
        public void onPreparing() {
            mVideoPresenterListener.onPlayStatusPreparing();
        }

        @Override
        public void onPrepared() {
            if (mPlayerWrapper == null)
                return;
            mVideoPresenterListener.onPlayStatusPrepared();
            if (mVideoStatsListener != null)
                mVideoStatsListener.onPlayStatsPrepared(mMediaStatsData, mPlayUrl, System.currentTimeMillis() - mStartPlayTime);
        }

        @Override
        public void onStarted() {
            if (mPlayerWrapper == null)
                return;
            mVideoPresenterListener.onPlayStatusStarted();
            statsStart();
            statsPlay(mPlayerWrapper.getCurrentPosition());
            mVideoPlayLastStated = VideoStatus.START;

            mVideoDuration = mPlayerWrapper.getVideoDuration();
        }

        @Override
        public void onPaused() {
            statsPause();
            mVideoPresenterListener.onPlayStatusPause();
        }

        @Override
        public void onStopped() {
            if (mVideoPlayLastStated != VideoStatus.COMPLETE && mVideoPlayLastStated != VideoStatus.STOP)
                statsVideoResult();
            mVideoPlayLastStated = VideoStatus.STOP;
            mVideoPresenterListener.onPlayStatusStopped();
        }

        @Override
        public void onCompleted() {
            mVideoPresenterListener.onPlayStatusCompleted();
            statsComplete();
            if (mVideoPlayLastStated != VideoStatus.COMPLETE && mVideoPlayLastStated != VideoStatus.STOP)
                statsVideoResult();

            // reset stats status
            mVideoPlayLastStated = VideoStatus.COMPLETE;
            mStartPlayTime = 0;
        }

        @Override
        public void onError(String reason, Throwable th) {

            if (mPlayerWrapper != null) {
                mPlayerWrapper = null;
                mVideoPlayLastStated = VideoStatus.IDLE;
            }

            mVideoPresenterListener.onPlayStatusError(reason, th);

            if (mVideoStatsListener != null)
                mVideoStatsListener.onPlayStatsError(mMediaStatsData, mPlayUrl, System.currentTimeMillis() - mStartPlayTime, reason);
        }
    };

    private VideoPlayerInterface.OnVideoSizeChangedListener mVideoSizeChangedListener = new VideoPlayerInterface.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(int width, int height, int visibleWidth, int visibleHeight) {
            if (width == 0 || height == 0) {
                adjustVideoSize();
                return;
            }

            if (mWidth == width && mHeight == height)
                return;

            mWidth = width;
            mHeight = height;
            doAdjustVideoSize(mWidth, mHeight);
        }
    };

    public void adjustVideoSize() {
        if (mWidth == 0 || mHeight == 0)
            return;
        doAdjustVideoSize(mWidth, mHeight);
    }

    private void doAdjustVideoSize(int width, int height) {
        if (mVideoPresenterListener != null)
            mVideoPresenterListener.doAdjustVideoSize(width, height);
    }

    public int getStartPlayTimes() {
        return mStartPlayTimes;
    }

    private VideoPlayerInterface.OnProgressUpdateListener mProgressUpdateListener = new VideoPlayerInterface.OnProgressUpdateListener() {

        @Override
        public void onBufferingUpdate(int percent) {
            if (mPlayerWrapper == null)
                return;
            mVideoPresenterListener.onBufferingUpdate((percent * mPlayerWrapper.getVideoDuration()) / 100);
        }

        @Override
        public void onProgressUpdate(int progress) {
            if (mPlayerWrapper == null)
                return;
            int duration = mPlayerWrapper.getVideoDuration();
            if (progress > duration && duration > 0)
                progress = duration;
            if (isPlaying()) {
                mVideoPresenterListener.onProgressUpdate(duration, progress);
                statsProgress(progress);
            } else {
                mVideoPresenterListener.onProgressUpdateWhenNotPlay(duration, progress);
            }
        }

        @Override
        public void onMaxProgressUpdate(int duration) {
            if (mVideoPresenterListener != null)
                mVideoPresenterListener.onMaxProgressUpdate(duration);
        }
    };

    public int getDuration() {
        return mPlayerWrapper == null ? 0 : mPlayerWrapper.getVideoDuration();
    }
}
