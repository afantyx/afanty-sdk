package com.afanty.video.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.R;
import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.internal.internal.CreativeType;
import com.afanty.models.Bid;
import com.afanty.video.MediaStatsData;
import com.afanty.video.VideoManager;
import com.afanty.video.presenter.VideoPresenter;
import com.afanty.video.presenter.VideoPresenterListener;

public abstract class BaseMediaView extends FrameLayout implements VideoPresenterListener {
    private static final String TAG = "MediaView.Base";
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.MATRIX;

    protected TextureView mTextureView;
    protected FrameLayout mCoverLayout;
    private boolean mIsPause = false;
    private boolean mIsRelease = true;
    private boolean mIsMute = true;
    private String mPlayUrl = "";

    protected VideoPresenter mVideoPresenter;
    protected boolean mCheckWindowFocus = true;
    protected boolean isActiveClick;

    public BaseMediaView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public BaseMediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BaseMediaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected abstract void onPreStart();

    protected abstract void onTextureAvailable();

    protected abstract void setBaseImageVisibly();

    protected abstract MediaStatsData createMediaStatsData();

    protected abstract void onSoundClick(boolean isMute);

    protected void initView(final Context context) {
        setClipChildren(false);
        View.inflate(context, R.layout.aft_base_media_view_layout, this);

        mTextureView = findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        mCoverLayout = findViewById(R.id.fl_cover);
    }

    protected void initController(String url) {
        mVideoPresenter = new VideoPresenter(this);
        mVideoPresenter.setVideoData(createMediaStatsData());
        mPlayUrl = url;
        VideoManager.getInstance().clearCurPosition(mPlayUrl);
        mVideoPresenter.initController();
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (mVideoPresenter != null && mTextureView != null && mTextureView.isAvailable()) {
                try {
                    mVideoPresenter.setTextureDisplay(mTextureView);
                    onTextureAvailable();
                } catch (Exception e) {
                }
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mVideoPresenter.setTextureDisplay(null);
            stopAndReleasePlayer();
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    @Override
    public void doAdjustVideoSize(int width, int height) {
        float screenWidth = this.getWidth();
        float screenHeight = this.getHeight();

        float wRatio = (float) width / screenWidth;
        float hRatio = (float) height / screenHeight;
        float ratio = Math.max(wRatio, hRatio);

        int surfaceWidth = (int) Math.ceil((float) width / ratio);
        int surfaceHeight = (int) Math.ceil((float) height / ratio);
        if (surfaceWidth * surfaceHeight == 0) {
            surfaceHeight = (int) screenHeight;
            surfaceWidth = (int) screenWidth;
        } else if ((float) width / (float) height == 660f / 346f) {
            surfaceWidth++;
        } else if ((float) width / (float) height == 852f / 480f) {
            surfaceWidth += 3;
        }

        if (mTextureView != null) {
            if (mScaleType == ImageView.ScaleType.CENTER_CROP) {
                float centerCropRatio = Math.min(wRatio, hRatio);
                float scale = ratio / centerCropRatio;

                Matrix matrix = new Matrix();
                if (ratio == wRatio) {
                    matrix.postScale(scale, 1);
                    matrix.postTranslate(((float) screenWidth - ((float) screenWidth) * scale) / 2f, 0);
                } else {
                    matrix.postScale(1, scale);
                    matrix.postTranslate(0, ((float) screenHeight - ((float) screenHeight) * scale) / 2f);
                }
                mTextureView.setTransform(matrix);
                mTextureView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else if (mScaleType == ImageView.ScaleType.FIT_CENTER) {
                float sx = (float) screenWidth / (float) width;
                float sy = (float) screenHeight / (float) height;

                Matrix matrix = new Matrix();
                matrix.preTranslate((screenWidth - width) / 2, (screenHeight - height) / 2);
                matrix.preScale(width / screenWidth, height / screenHeight);

                if (sx >= sy) {
                    matrix.postScale(sy, sy, getWidth() / 2f, getHeight() / 2f);
                } else {
                    matrix.postScale(sx, sx, getWidth() / 2f, getHeight() / 2f);
                }

                mTextureView.setTransform(matrix);
                mTextureView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            } else
                mTextureView.setLayoutParams(new LayoutParams(surfaceWidth, surfaceHeight, Gravity.CENTER));
        }
    }

    private void doStartPlay() {
        setBaseImageVisibly();

        if (!mIsRelease) {
            mVideoPresenter.releasePlayer();
        }

        mIsRelease = false;
        onPreStart();
        mVideoPresenter.start(mPlayUrl, true, mIsMute, VideoManager.getInstance().getCurPosition(mPlayUrl));

        if (mTextureView.isAvailable())
            mVideoPresenter.setTextureDisplay(mTextureView);
    }

    private void stopAndReleasePlayer() {
        setBaseImageVisibly();

        if (mVideoPresenter == null)
            return;

        if (mVideoPresenter.getPlayer() != null)
            VideoManager.getInstance().addCurPosition(mPlayUrl, mVideoPresenter.getPlayer().getCurrentPosition());

        mVideoPresenter.stopPlay();
        mVideoPresenter.releasePlayer();
        mIsRelease = true;
    }

    public void startPlay() {
        if (TextUtils.isEmpty(mPlayUrl))
            return;
        try {
            doStartPlay();
        } catch (Exception e) {
        }
    }

    public void stopPlay() {
        mIsPause = false;
        stopAndReleasePlayer();
    }

    protected void pausePlay() {
        if (mVideoPresenter != null)
            mVideoPresenter.pausePlay();
    }

    protected void resumePlay() {
        if (mVideoPresenter != null)
            mVideoPresenter.resumePlay();
    }

    protected void statsSkip() {
        if (mVideoPresenter != null)
            mVideoPresenter.statsSkip();
    }

    protected void statsClose() {
        if (mVideoPresenter != null)
            mVideoPresenter.statsClose();
    }

    protected void statsCreativeView() {
        if (mVideoPresenter != null)
            mVideoPresenter.statsCreateView();
    }

    protected void statsError(String reason) {
        if (mVideoPresenter != null)
            mVideoPresenter.statsError(reason);
    }

    public void setMuteState(boolean isMute) {
        mIsMute = isMute;
    }

    public boolean getMuteState() {
        return mIsMute;
    }

    public void setCheckWindowFocus(boolean checkWindowFocus) {
        this.mCheckWindowFocus = checkWindowFocus;
    }

    public void checkAutoPlay(Bid adData) {
        if (!CreativeType.isVideo(adData))
            return;

        boolean isSupportAutoPlay = supportAutoPlay(adData);
        if (isSupportAutoPlay) {
            startPlay();
            mIsPause = false;
        }
    }

    private boolean supportAutoPlay(Bid adData) {
        return false;
    }

    protected OnClickListener mSoundClickLister = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mVideoPresenter != null) {
                mIsMute = mVideoPresenter.soundClick();
                onSoundClick(mIsMute);
            }
        }
    };

    protected OnClickListener mErrorReplayClickLister = new OnClickListener() {
        @Override
        public void onClick(View v) {
            isActiveClick = true;
            startPlay();
        }
    };


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            if (mVideoPresenter != null && !mVideoPresenter.isComplete()) {
                mVideoPresenter.pausePlay();
                mIsPause = true;
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (!mCheckWindowFocus)
            return;

        if (hasWindowFocus) {
            ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork(200) {//Fix bug, wait last media release
                @Override
                public void callBackOnUIThread() {
                    if (mIsPause && mVideoPresenter != null && !mVideoPresenter.isComplete()) {
                        mVideoPresenter.resumePlay();
                    } else {
                        doStartPlay();
                    }
                    mIsPause = false;
                }
            });
        } else {
            if (mVideoPresenter != null && !mVideoPresenter.isComplete()) {
                mVideoPresenter.pausePlay();
                mIsPause = true;
            } else {
                stopPlay();
            }
        }
    }

    @Override
    public void onProgressUpdateWhenNotPlay(int duration, int progress) {

    }

    @Override
    public void onPlayStatusPause() {
    }

    protected int getDuration() {
        if (mVideoPresenter != null)
            return mVideoPresenter.getDuration();
        return 0;
    }

    protected boolean isComplete(){
        if (mVideoPresenter != null)
            return mVideoPresenter.isComplete();
        return false;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
    }
}
