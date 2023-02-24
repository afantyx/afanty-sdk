package com.afanty.internal.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.R;
import com.afanty.ads.AftImageLoader;
import com.afanty.ads.VideoOptions;
import com.afanty.ads.base.IAdObserver;
import com.afanty.internal.common.VisibilityTracker;
import com.afanty.internal.config.AftConfig;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.ViewUtils;
import com.afanty.video.MediaError;
import com.afanty.video.MediaStatsData;
import com.afanty.video.PlayerUrlHelper;
import com.afanty.video.view.BaseMediaView;
import com.afanty.video.view.VideoListener;

public class InnerPlayerView extends BaseMediaView {
    private static final String TAG = "MediaView.Native";
    private View mRootView;
    private ImageView mBaseImg;
    private ProgressBar mLoadingProgress;
    private ProgressBar mSeekProgress;
    private ImageView mSoundImg;
    private ImageView mStartButton;
    private LinearLayout mErrorLayout;
    private TextView mErrorTxt;
    private View mTopView;

    private Bid mBid;
    private VideoListener mVideoListener;
    private IAdObserver.VideoLifecycleCallbacks mVideoCallbacks;
    private boolean mHasWindowFocus = true;

    private VisibilityTracker.VisibilityChecker mVisibilityChecker;

    public InnerPlayerView(@NonNull Context context) {
        super(context);
        initMediaView(context);
    }

    public InnerPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMediaView(context);
    }

    public InnerPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMediaView(context);
    }

    public void setMediaViewListener(VideoListener videoListener) {
        this.mVideoListener = videoListener;
    }

    public void setVideoLifecycleCallbacks(IAdObserver.VideoLifecycleCallbacks lifecycleCallbacks) {
        mVideoCallbacks = lifecycleCallbacks;
    }

    public void setVideoOptions(VideoOptions videoOptions) {
        if (videoOptions != null) {
            super.setMuteState(videoOptions.getStartMuted());
            if (mSoundImg != null) {
                mSoundImg.setSelected(videoOptions.getStartMuted());
                ViewUtils.setFrameLayoutGravity(mSoundImg, videoOptions.getSoundGravity());
            }
        }
    }

    private void initMediaView(Context context) {
        mVisibilityChecker = new VisibilityTracker.VisibilityChecker();
        mRootView = View.inflate(context, R.layout.aft_native_media_view, null);
        mBaseImg = mRootView.findViewById(R.id.iv_background);
        mLoadingProgress = mRootView.findViewById(R.id.loading_progress);
        mSeekProgress = mRootView.findViewById(R.id.seek_progress);
        mSoundImg = mRootView.findViewById(R.id.iv_sound);

        mStartButton = mRootView.findViewById(R.id.iv_start_button);
        mStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isActiveClick = true;
                startPlay();
            }
        });

        mErrorLayout = mRootView.findViewById(R.id.ll_error_layout);
        mErrorTxt = mRootView.findViewById(R.id.tv_error_message);
        ImageView replayImg = mRootView.findViewById(R.id.iv_replay_btn);
        replayImg.setOnClickListener(mErrorReplayClickLister);

        mSoundImg.setOnClickListener(mSoundClickLister);

        mCoverLayout.removeAllViews();
        mCoverLayout.addView(mRootView);
        mTopView = Views.getTopmostView(getContext(), this);
        setDefaultVideoOptions();
    }

    private void setDefaultVideoOptions() {
        VideoOptions videoOptions = new VideoOptions.Builder().build();
        super.setMuteState(videoOptions.getStartMuted());
        if (mSoundImg != null) {
            mSoundImg.setSelected(videoOptions.getStartMuted());
            ViewUtils.setFrameLayoutGravity(mSoundImg, videoOptions.getSoundGravity());
        }
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        super.setScaleType(scaleType);
        mBaseImg.setScaleType(scaleType);
    }

    public void setBid(@NonNull Bid bid) {
        mBid = bid;

        initController(getVideoPlayUrl());
        AdmBean.ImgBean imgBean = mBid.getImgBean(AdmBean.IMG_POSTER_TYPE);
        AftImageLoader.getInstance().loadUri(getContext(), imgBean == null ? "" : imgBean.getUrl(), mBaseImg);
    }

    @Override
    public void setMuteState(boolean isMute) {
        super.setMuteState(isMute);
        if (mVideoCallbacks != null) {
            mVideoCallbacks.onVideoMute(isMute);
        }
    }

    @Nullable
    private String getVideoPlayUrl() {
        String url;
        String playUrl = PlayerUrlHelper.getVideoPlayUrl(mBid);
        if (!TextUtils.isEmpty(playUrl)) {
            url = playUrl;
        } else {
            url = mBid.getVastPlayUrl();
        }
        return url;
    }

    @Override
    protected void onPreStart() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

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
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mHasWindowFocus = hasWindowFocus;
        if (hasWindowFocus) {
            initController(getVideoPlayUrl());
            startPlay();
        } else {
            if (mVideoPresenter.isPlaying()) {
                mVideoPresenter.pausePlay();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
    }

    @Override
    protected void onTextureAvailable() {
        if (mVideoListener != null)
            mVideoListener.onSurfaceTextureAvailable();
        if (mVideoCallbacks != null)
            mVideoCallbacks.onVideoStart();
    }

    @Override
    protected void setBaseImageVisibly() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
    }

    @Override
    protected MediaStatsData createMediaStatsData() {
        MediaStatsData mediaStatsData = new MediaStatsData();
        return mediaStatsData;
    }

    @Override
    protected void onSoundClick(boolean isMute) {
        if (mSoundImg != null)
            mSoundImg.setSelected(isMute);
    }

    @Override
    public void onPlayStatusStarted() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(GONE);
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(GONE);
        if (mSeekProgress != null)
            mSeekProgress.setVisibility(VISIBLE);
        if (mSoundImg != null) {
            if (!mBid.isShowVideoMute())
                mSoundImg.setVisibility(GONE);
            else
                mSoundImg.setVisibility(VISIBLE);
        }
        if (mErrorLayout != null)
            mErrorLayout.setVisibility(GONE);
        if (mStartButton != null)
            mStartButton.setVisibility(View.GONE);
    }

    @Override
    public void onPlayStatusPreparing() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(VISIBLE);
        if (mSoundImg != null)
            mSoundImg.setVisibility(GONE);
    }

    @Override
    public void onPlayStatusPrepared() {
    }

    @Override
    public void onPlayStatusStopped() {
        if (mStartButton != null)
            mStartButton.setVisibility(View.VISIBLE);
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(GONE);
    }

    @Override
    public void onPlayStatusPause() {
        if (mStartButton != null)
            mStartButton.setVisibility(View.VISIBLE);
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(GONE);
        if (mVideoCallbacks != null)
            mVideoCallbacks.onVideoPause();
    }

    @Override
    public void onPlayStatusCompleted() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
        if (mSeekProgress != null)
            mSeekProgress.setVisibility(GONE);
        if (mSoundImg != null)
            mSoundImg.setVisibility(GONE);
        if (mErrorLayout != null)
            mErrorLayout.setVisibility(VISIBLE);
        if (mErrorTxt != null)
            mErrorTxt.setVisibility(View.GONE);
        if (mVideoCallbacks != null)
            mVideoCallbacks.onVideoEnd();
    }

    @Override
    public void onPlayStatusError(String reason, Throwable th) {
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(GONE);
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
        if (mSeekProgress != null)
            mSeekProgress.setVisibility(GONE);
        if (mErrorLayout != null)
            mErrorLayout.setVisibility(VISIBLE);
        if (mSoundImg != null)
            mSoundImg.setVisibility(GONE);
        String errorMsg = getResources().getString(R.string.aft_media_player_error_wrong);
        if (MediaError.REASON_ERROR_IO.equals(reason) || MediaError.ERROR_CODE_OPEN_FAILED.equals(reason) || MediaError.REASON_ERROR_NETWORK.equals(reason))
            errorMsg = getResources().getString(R.string.aft_media_player_network_err_msg);
        if (mErrorTxt != null) {
            mErrorTxt.setText(errorMsg);
            mErrorTxt.setVisibility(View.VISIBLE);
        }
        if (mStartButton != null)
            mStartButton.setVisibility(View.GONE);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (mSeekProgress != null)
            mSeekProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onProgressUpdate(int duration, int progress) {
        if (mSeekProgress != null)
            mSeekProgress.setProgress(progress);

        checkViewedPercentageToPausePlay();
    }

    private void checkViewedPercentageToPausePlay() {
        int minPercentageViewed = AftConfig.getAftNativeVideoPausePercentage();
        boolean isVisible = mVisibilityChecker.isVisible(mTopView, this, minPercentageViewed, 0);
        if (!isVisible && !isActiveClick && !isComplete()) {
            pausePlay();
            if (mStartButton != null)
                mStartButton.setVisibility(View.VISIBLE);
        }
        resetActiveClickWhenNoVisible();
    }

    @Override
    public void onProgressUpdateWhenNotPlay(int duration, int progress) {
        checkViewedPercentageToResumePlay();
    }

    private void checkViewedPercentageToResumePlay() {
        int minPercentageViewed = AftConfig.getAftNativeVideoResumePercentage();
        boolean isVisible = mVisibilityChecker.isVisible(mTopView, this, minPercentageViewed, 0);
        if (isVisible && !isComplete() && mHasWindowFocus) {
            resumePlay();
            if (mStartButton != null)
                mStartButton.setVisibility(View.GONE);
        }
        resetActiveClickWhenNoVisible();
    }

    private void resetActiveClickWhenNoVisible() {
        boolean isVisible = mVisibilityChecker.isVisible(mTopView, this, 1, 0);
        if (!isVisible) {
            isActiveClick = false;
        }
    }

    @Override
    public void onMaxProgressUpdate(int duration) {
        if (mSeekProgress != null)
            mSeekProgress.setMax(duration);
    }

    @Override
    public void restart() {
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(VISIBLE);
        if (mErrorLayout != null)
            mErrorLayout.setVisibility(GONE);
    }

    @Override
    public void start() {
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(VISIBLE);
        if (mErrorLayout != null)
            mErrorLayout.setVisibility(GONE);
        if (mVideoCallbacks != null)
            mVideoCallbacks.onVideoPlay();
    }

    public void resumePlay() {
        super.resumePlay();
        if (mVideoCallbacks != null)
            mVideoCallbacks.onVideoPlay();
    }
}
