package com.afanty.video;

import android.text.TextUtils;

import com.afanty.core.InitProxy;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.Reflector;
import com.afanty.video.exoplayer.ExoPlayerWrapper;
import com.afanty.video.mediaplayer.MediaPlayerWrapper;

import java.util.HashMap;
import java.util.Map;

public class VideoManager {
    private static final String TAG = "VideoManager";
    private static final String EXO_NECESSARY_CLAZZ = "com.google.android.exoplayer2.ExoPlayer";
    private static volatile VideoManager mVideoManager;

    private static VideoPlayerInterface mPlayerWrapper;

    private final Map<String, Integer> mCurrPlayProgress = new HashMap<String, Integer>();

    public static VideoManager getInstance() {
        if (mVideoManager == null) {
            synchronized (VideoManager.class) {
                if (mVideoManager == null) {
                    mVideoManager = new VideoManager();
                    if (Reflector.hasNecessaryClazz(EXO_NECESSARY_CLAZZ) && InitProxy.isUseExoPlayer()) {
                        mPlayerWrapper = new ExoPlayerWrapper(ContextUtils.getContext());
                    } else {
                        mPlayerWrapper = new MediaPlayerWrapper();
                    }
                }
            }
        }
        return mVideoManager;
    }

    private VideoManager() {
    }

    public VideoPlayerInterface getVideoPlayer() {
        return mPlayerWrapper;
    }

    public synchronized void addCurPosition(String url, int position) {
        if (TextUtils.isEmpty(url))
            return;
        mCurrPlayProgress.put(url, position);
    }

    public synchronized int getCurPosition(String url) {
        if (TextUtils.isEmpty(url))
            return 0;
        if (mCurrPlayProgress.containsKey(url))
            return mCurrPlayProgress.get(url);

        return 0;
    }

    public synchronized void clearCurPosition(String url) {
        mCurrPlayProgress.remove(url);
    }

}
