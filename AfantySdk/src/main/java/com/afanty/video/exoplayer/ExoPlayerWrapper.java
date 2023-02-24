package com.afanty.video.exoplayer;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_OFF;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;

import com.afanty.common.download.DownloadManager;
import com.afanty.video.MediaError;
import com.afanty.video.VideoPlayerInterface;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.video.VideoSize;

public class ExoPlayerWrapper implements VideoPlayerInterface, Player.Listener {
    private static String TAG = "Ad.ExoPlayerWrapper";

    private SimpleExoPlayer mExoPlayer;
    private Context mContext;
    private MediaHandler mMediaHandler;
    private HandlerThread mHandlerThread;
    private Handler mMainThreadHandler;
    private PlayStatusListener mPlayStatusListener;
    private OnProgressUpdateListener mOnProgressUpdateListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private boolean isAutoPlay;
    private boolean isComplete;
    private String mPlayUrl;

    private static final int UPDATE_PROGRESS_INTERVAL = 500;
    private static final int HANDLE_PROGRESS = 10;

    public ExoPlayerWrapper(Context context) {
        mContext = context.getApplicationContext();
    }

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

        if (mExoPlayer != null)
            return;


        mExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        mExoPlayer.setRepeatMode(REPEAT_MODE_OFF);
        mExoPlayer.addListener(this);
        mExoPlayer.addAnalyticsListener(new EventLogger(null));
    }

    @Override
    public void releasePlayer() {
        try {
            clearMediaMessage(HANDLE_PROGRESS);

            if (mExoPlayer != null) {
                mExoPlayer.release();
                mExoPlayer = null;
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
        url = DownloadManager.getCacheUrl(url);
        if (!checkUrl(url))
            return;

        if (mExoPlayer == null) {
            return;
        }

        mPlayUrl = url;
        try {
            MediaItem mediaItem = MediaItem.fromUri(url);
            mExoPlayer.setMediaItem(mediaItem);
            mExoPlayer.prepare();
            if (isAutoPlay) {
                mExoPlayer.play();
            }
        } catch (Exception e) {
            notifyError(MediaError.REASON_PREPARE_FAILED, e);
        }
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

    @Override
    public void resumePlay() {
        if (mExoPlayer == null) {
            return;
        }

        isAutoPlay = true;
        mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pausePlay() {
        if (mExoPlayer == null) {
            return;
        }

        mExoPlayer.pause();
        notifyPaused();
    }

    @Override
    public void stopPlay() {
        if (mExoPlayer == null) {
            return;
        }


        try {
            mExoPlayer.stop();
            clearMediaMessage(HANDLE_PROGRESS);
            notifyStopped();
        } catch (Exception e) {
        }
    }

    @Override
    public void seekTo(int msec) {
        if (mExoPlayer == null) {
            return;
        }

        try {
            mExoPlayer.seekTo(msec);
        } catch (Exception e) {
        }
    }

    @Override
    public void reStart() {
        if (mExoPlayer == null) {
            return;
        }

        isAutoPlay = true;
        startPlay(mPlayUrl);
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
        if (mExoPlayer == null) {
            return;
        }


        try {
            if (display instanceof Surface)
                mExoPlayer.setVideoSurface(((Surface) display));
            else if (display instanceof TextureView) {
                Surface surface = new Surface(((TextureView) display).getSurfaceTexture());
                mExoPlayer.setVideoSurface(surface);
            } else
                mExoPlayer.clearVideoSurface();

        } catch (Exception e) {
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mExoPlayer == null)
            return;

        volume = volume < 0 ? 0 : (Math.min(volume, 100));
        float volumeFloat = volume * 0.01f;
        mExoPlayer.setVolume(volumeFloat);
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.isPlaying();
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    @Override
    public int getVideoDuration() {
        return mExoPlayer != null ? (int) mExoPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mExoPlayer != null ? (int) mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void setPlayStatusListener(PlayStatusListener playStatusListener) {
        this.mPlayStatusListener = playStatusListener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        mOnProgressUpdateListener = onProgressUpdateListener;
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

    @Override
    public void onPlaybackStateChanged(@Player.State int state) {
        if (mExoPlayer == null || mMainThreadHandler == null)
            return;
        if (state == Player.STATE_BUFFERING) {
            mMainThreadHandler.post(() -> {
                if (mOnProgressUpdateListener != null && mExoPlayer != null)
                    mOnProgressUpdateListener.onBufferingUpdate(mExoPlayer.getBufferedPercentage());
            });
            isComplete = false;
        } else if (state == Player.STATE_READY) {
            mMainThreadHandler.post(() -> {
                if (mOnProgressUpdateListener != null)
                    mOnProgressUpdateListener.onMaxProgressUpdate(getVideoDuration());
            });
            notifyStarted();
            sendMediaMessage(HANDLE_PROGRESS, null, 0);
            isComplete = false;
        } else if (state == Player.STATE_ENDED) {
            notifyComplete();
            isComplete = true;
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
            reStart();
        } else {
            String errorMessage = error.getMessage();
            notifyError(errorMessage, error);
            if (mExoPlayer != null) {
                mExoPlayer = null;
            }
        }
        clearMediaMessage(HANDLE_PROGRESS);
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        if (videoSize.width == 0 || videoSize.height == 0) {
            releasePlayer();

            notifyError(MediaError.REASON_INVALID_VIDEO_SIZE, null);
            return;
        }

        mMainThreadHandler.post(() -> {
            if (mOnVideoSizeChangedListener != null)
                mOnVideoSizeChangedListener.onVideoSizeChanged(videoSize.width, videoSize.height, videoSize.width, videoSize.height);
        });
    }

    private void doUpdateProgress() {
        if (mExoPlayer == null || mMainThreadHandler == null)
            return;

        mMainThreadHandler.post(() -> {
            if (mExoPlayer != null)
                notifyProgressChanged((int) mExoPlayer.getCurrentPosition());
        });

        sendMediaMessage(HANDLE_PROGRESS, null, UPDATE_PROGRESS_INTERVAL);
    }

    private void notifyProgressChanged(int timeMs) {
        if (mOnProgressUpdateListener != null)
            mOnProgressUpdateListener.onProgressUpdate(timeMs);
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

    private void notifyStarted() {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(() -> {
            if (mPlayStatusListener != null)
                mPlayStatusListener.onStarted();
        });
    }

    private void notifyPaused() {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(() -> {
            if (mPlayStatusListener != null)
                mPlayStatusListener.onPaused();
        });
    }

    private void notifyStopped() {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(() -> {
            if (mPlayStatusListener != null)
                mPlayStatusListener.onStopped();
        });
    }

    private void notifyComplete() {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(() -> {
            if (mPlayStatusListener != null)
                mPlayStatusListener.onCompleted();
        });
    }

    private void notifyError(final String reason, final Throwable th) {
        if (mMainThreadHandler == null)
            return;
        mMainThreadHandler.post(() -> {
            if (mPlayStatusListener != null)
                mPlayStatusListener.onError(reason, th);
        });
    }
}
