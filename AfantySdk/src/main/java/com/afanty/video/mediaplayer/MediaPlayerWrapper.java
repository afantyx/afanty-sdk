package com.afanty.video.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;

import com.afanty.common.download.DownloadManager;
import com.afanty.video.MediaData;
import com.afanty.video.MediaError;
import com.afanty.video.VideoPlayerInterface;
import com.afanty.video.VideoPlayerState;


public class MediaPlayerWrapper implements VideoPlayerInterface, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnVideoSizeChangedListener {
    private static String TAG = "Ad.Media";

    private static final int UPDATE_PROGRESS_INTERVAL = 500;
    private static final int HANDLE_PROGRESS = 10;

    private volatile MediaPlayer mMediaPlayer;
    private MediaData mMediaData;
    private MediaHandler mMediaHandler;
    private HandlerThread mHandlerThread;
    private Handler mMainThreadHandler;

    private PlayStatusListener mPlayStatusListener;
    private OnProgressUpdateListener mOnProgressUpdateListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;


    @Override
    public void createPlayer() {
        if (mHandlerThread == null || !mHandlerThread.isAlive() || mMediaHandler == null || mMainThreadHandler == null) {
            if (mHandlerThread != null) {
                mHandlerThread.quit();
            }
            mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mMediaHandler = new MediaHandler((mHandlerThread.getLooper()));
            mMainThreadHandler = new Handler(Looper.getMainLooper());
        }

        doCreatePlayer();
    }

    private void doCreatePlayer() {
        if (mMediaPlayer != null)
            return;


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.reset();

        mMediaData = new MediaData();
        mMediaData.reset();
    }

    @Override
    public void releasePlayer() {
        try {
            clearMediaMessage(HANDLE_PROGRESS);
            if (mMediaData != null) {
                mMediaData.setCurrentState(VideoPlayerState.END);
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void startPlay(String url) {
        startPlay(url, 0);
    }

    @Override
    public void startPlay(String url, int startTime) {
        setDataSource(url, startTime);
        prepareAsync();
    }

    private void setDataSource(String url, int startTime) {
        url = DownloadManager.getCacheUrl(url);
        if (!checkUrl(url))
            return;

        if (mMediaPlayer == null || mMediaData == null) {
            return;
        }

        if (TextUtils.equals(mMediaData.getUrl(), url)) {
            return;
        }

        if (mMediaData.getCurrentState() != VideoPlayerState.IDLE){
            return;
        }

        try {
            mMediaData.setUrl(url);
            mMediaData.setPlayPosition(startTime);
            mMediaPlayer.setDataSource(url);
            mMediaData.setCurrentState(VideoPlayerState.INITIALIZED);
        } catch (Exception e) {
            notifyError(MediaError.REASON_PREPARE_FAILED, e);
        }
    }

    private void prepareAsync() {
        try {
            if (mMediaData == null)
                return;

            if (mMediaData.getCurrentState() != VideoPlayerState.STOPPED && mMediaData.getCurrentState() != VideoPlayerState.INITIALIZED)
                return;

            notifyPreparing();
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            notifyError(MediaError.REASON_PREPARE_FAILED, e);
        }
    }

    @Override
    public void resumePlay() {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }

        mMediaData.setAutoPlay(true);
        switch (mMediaData.getCurrentState()) {
            case IDLE:
                startPlay(mMediaData.getUrl());
                break;
            case PAUSED:
                resumeMedia();
                break;
            case STOPPED:
                mMediaData.setPlayPosition(0);
                prepareAsync();
                break;
            case PLAYBACKCOMPLETED:
                stopPlay();
                mMediaData.setPlayPosition(0);
                prepareAsync();
                break;
            case ERROR:
                reStart();
                break;
            case PREPARED:
                if (!mMediaPlayer.isPlaying()) {
                    onPrepared(mMediaPlayer);
                }
                break;
            case STARTED:
                if (!mMediaPlayer.isPlaying()) {
                    //When buffering
                    resumeMedia();
                } else {
                }
                break;
            default:
                break;
        }
    }

    private void resumeMedia() {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }

        try {
            mMediaPlayer.start();
            notifyStarted();
        } catch (Exception e) {
        }
    }


    @Override
    public void pausePlay() {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }

        if (mMediaData.getCurrentState() == VideoPlayerState.PAUSED) {
            return;
        }

        if (mMediaData.getCurrentState() != VideoPlayerState.STARTED) {
            mMediaData.setAutoPlay(false);
            return;
        }

        try {
            mMediaPlayer.pause();
            notifyPaused();
        } catch (Exception e) {
        }
    }

    @Override
    public void stopPlay() {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }


        if (mMediaData.getCurrentState() != VideoPlayerState.PREPARED && mMediaData.getCurrentState() != VideoPlayerState.STARTED &&
                mMediaData.getCurrentState() != VideoPlayerState.PAUSED && mMediaData.getCurrentState() != VideoPlayerState.PLAYBACKCOMPLETED) {
            return;
        }
        try {
            mMediaPlayer.stop();
            clearMediaMessage(HANDLE_PROGRESS);
            notifyStopped();
        } catch (Exception e) {
        }
    }

    @Override
    public void seekTo(int msec) {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }

        try {
            mMediaData.setPlayPosition(msec);
            mMediaPlayer.seekTo(msec);
        } catch (Exception e) {
        }
    }

    @Override
    public void reStart() {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }

        if (mMediaData.getCurrentState() == VideoPlayerState.ERROR || mMediaData.getCurrentState() == VideoPlayerState.END || mMediaData.getCurrentState() == VideoPlayerState.IDLE) {
            mMediaData.setAutoPlay(true);
            startPlay(mMediaData.getUrl());
        } else if (mMediaData.getCurrentState() == VideoPlayerState.STOPPED) {
            mMediaData.setAutoPlay(true);
            seekTo(0);
            prepareAsync();
        } else if (mMediaData.getCurrentState() == VideoPlayerState.PAUSED) {
            seekTo(0);
            resumeMedia();
        } else if (mMediaData.getCurrentState() == VideoPlayerState.PLAYBACKCOMPLETED) {
            seekTo(0);
            resumeMedia();
        }
    }

    @Override
    public void setDisplay(Surface surface) {
        doSetDisplay(surface);
    }

    @Override
    public void setDisplay(TextureView textureView) {
        doSetDisplay(textureView);
    }

    private void doSetDisplay(Object display) {
        if (mMediaData == null || mMediaPlayer == null) {
            return;
        }


        try {
            if (display instanceof Surface)
                mMediaPlayer.setSurface((Surface) display);
            else if (display instanceof TextureView) {
                Surface surface = new Surface(((TextureView) display).getSurfaceTexture());
                mMediaPlayer.setSurface(surface);
                surface.release();
            } else
                mMediaPlayer.setSurface(null);

        } catch (Exception e) {
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mMediaPlayer == null)
            return;

        volume = volume < 0 ? 0 : (Math.min(volume, 100));
        float volumeFloat = volume * 0.01f;
        mMediaPlayer.setVolume(volumeFloat, volumeFloat);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isComplete() {
        return mMediaData != null && (mMediaData.getCurrentState() == VideoPlayerState.PLAYBACKCOMPLETED || mMediaData.getCurrentState() == VideoPlayerState.END);
    }

    @Override
    public void setAutoPlay(boolean isAutoPlay) {
        if (mMediaData != null) {
            mMediaData.setAutoPlay(isAutoPlay);
        }
    }

    @Override
    public int getVideoDuration() {
        return mMediaData == null ? 0 : mMediaData.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void setPlayStatusListener(PlayStatusListener playStatusListener) {
        mPlayStatusListener = playStatusListener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener videoSizeChangedListener) {
        mOnVideoSizeChangedListener = videoSizeChangedListener;
    }

    @Override
    public void setOnProgressUpdateListener(OnProgressUpdateListener progressUpdateListener) {
        mOnProgressUpdateListener = progressUpdateListener;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, final int percent) {
        if (mMediaPlayer != null && mMediaData != null && mMainThreadHandler != null && mMediaData.getCurrentState() == VideoPlayerState.STARTED) {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnProgressUpdateListener != null)
                        mOnProgressUpdateListener.onBufferingUpdate(percent);
                }
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyComplete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notifyError(MediaError.REASON_ERROR_UNKNOWN, null);
        if (mp != null)
            mp.reset();
        clearMediaMessage(HANDLE_PROGRESS);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (mp == null || mMediaData == null)
            return false;
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // first video frame for rendering
            mMediaData.setDuration(Math.max(mMediaData.getDuration(), mp.getDuration()));
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnProgressUpdateListener != null)
                        mOnProgressUpdateListener.onMaxProgressUpdate(mMediaData.getDuration());
                }
            });
            sendMediaMessage(HANDLE_PROGRESS, null, 0);
            return true;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            notifyPrepared();
            boolean haveStartPosition = mMediaData.getPlayPosition() != 0;
            if (haveStartPosition) {
                mMediaPlayer.seekTo(mMediaData.getPlayPosition());
            }

            if (mMediaData.isAutoPlay()) {
                resumeMedia();
            }
        } catch (Exception e) {
            notifyError(MediaError.REASON_START_ERROR, e);
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, final int width, final int height) {
        if (width == 0 || height == 0) {
            if (mMediaPlayer != null)
                mMediaPlayer.reset();

            notifyError(MediaError.REASON_INVALID_VIDEO_SIZE, null);
            return;
        }

        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mOnVideoSizeChangedListener != null)
                    mOnVideoSizeChangedListener.onVideoSizeChanged(width, height, width, height);
            }
        });
    }

    private class MediaHandler extends Handler {
        public MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_PROGRESS:
                    doUpdateProgress();
                    break;
                default:
                    break;
            }
        }
    }

    private void sendMediaMessage(int what, Object obj, long delay) {
        if (mMediaHandler == null || mHandlerThread == null || !mHandlerThread.isAlive())
            return;

        mMediaHandler.removeMessages(what);
        Message msg = mMediaHandler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        mMediaHandler.sendMessageDelayed(msg, delay);
    }

    private void clearMediaMessage(int what) {
        if (mMediaHandler == null || mHandlerThread == null || !mHandlerThread.isAlive())
            return;

        mMediaHandler.removeMessages(what);
    }

    private void doUpdateProgress() {
        if (mMediaData == null)
            return;

        if (mMediaData.getCurrentState() == VideoPlayerState.PLAYBACKCOMPLETED || mMediaData.getCurrentState() == VideoPlayerState.END) {
            mMediaData.setPlayPosition(mMediaData.getDuration());
            notifyProgressChanged(mMediaData.getPlayPosition());
        } else if (mMediaPlayer != null && (mMediaData.getCurrentState() == VideoPlayerState.STARTED ||
                mMediaData.getCurrentState() == VideoPlayerState.PAUSED || mMediaData.getCurrentState() == VideoPlayerState.STOPPED)) {
            mMediaData.setPlayPosition((int) mMediaPlayer.getCurrentPosition());
            notifyProgressChanged(mMediaData.getPlayPosition());
        }

        sendMediaMessage(HANDLE_PROGRESS, null, UPDATE_PROGRESS_INTERVAL);
    }

    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            notifyError(MediaError.REASON_FILE_PATH, null);
            return false;
        } else if (isNetUrl(url)) {
            return true;
        } else if (isVideoCacheUrl(url)) {
            return true;
        } else {
            return true;
        }
    }

    private boolean isNetUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtmp://");
    }

    private boolean isVideoCacheUrl(String url) {
        return url.startsWith("file://");
    }

    private void notifyProgressChanged(final int timeMs) {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mOnProgressUpdateListener != null)
                    mOnProgressUpdateListener.onProgressUpdate(timeMs);
            }
        });
    }

    private void notifyPreparing() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.PREPARING);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onPreparing();
            }
        });
    }

    private void notifyPrepared() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.PREPARED);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onPrepared();
            }
        });
    }

    private void notifyStarted() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.STARTED);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onStarted();
            }
        });
    }

    private void notifyPaused() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.PAUSED);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onPaused();
            }
        });
    }

    private void notifyStopped() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.STOPPED);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onStopped();
            }
        });
    }

    private void notifyComplete() {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.PLAYBACKCOMPLETED);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onCompleted();
            }
        });
    }

    private void notifyError(final String reason, final Throwable th) {
        if (mMediaData == null || mMainThreadHandler == null)
            return;
        mMediaData.setCurrentState(VideoPlayerState.ERROR);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayStatusListener != null)
                    mPlayStatusListener.onError(reason, th);
            }
        });
    }
}
