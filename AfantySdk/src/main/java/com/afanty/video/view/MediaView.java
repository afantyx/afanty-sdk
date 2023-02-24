package com.afanty.video.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.R;
import com.afanty.ads.AftImageLoader;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.video.MediaError;
import com.afanty.video.MediaStatsData;
import com.afanty.video.PlayerUrlHelper;

public class MediaView extends BaseMediaView {
    private static final String TAG = "MediaView.FULL.N";
    private ImageView mBaseImg;
    private ProgressBar mLoadingProgress;
    private ProgressBar mSeekProgress;
    private ImageView mSoundImg;

    private LinearLayout mErrorLayout;
    private ImageView mReplayImg;
    private TextView mErrorTxt;
    private Bid mBid;
    private boolean isSoundAlwaysShow;

    private VideoListener mVideoListener;

    public MediaView(@NonNull Context context) {
        super(context);
        initMediaView(context);
    }

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMediaView(context);
    }

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMediaView(context);
    }

    public void setMediaViewListener(VideoListener videoListener) {
        this.mVideoListener = videoListener;
    }

    private void initMediaView(Context context) {
        View rootView = inflate(context, R.layout.aft_media_view_layout, null);
        mBaseImg = rootView.findViewById(R.id.iv_background);
        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mSeekProgress = rootView.findViewById(R.id.seek_progress);
        mSoundImg = rootView.findViewById(R.id.iv_sound);

        mErrorLayout = rootView.findViewById(R.id.ll_error_layout);
        mReplayImg = rootView.findViewById(R.id.iv_replay_btn);
        mErrorTxt = rootView.findViewById(R.id.tv_error_message);

        mReplayImg.setOnClickListener(mErrorReplayClickLister);

        setMuteState(false);
        mSoundImg.setOnClickListener(mSoundClickLister);

        mCoverLayout.removeAllViews();
        mCoverLayout.addView(rootView);
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        super.setScaleType(scaleType);
        mBaseImg.setScaleType(scaleType);
    }

    public void setBid(@NonNull Bid bid) {
        mBid = bid;
        String url = "";
        String playUrl = PlayerUrlHelper.getVideoPlayUrl(bid);
        if (!TextUtils.isEmpty(playUrl)) {
            url = playUrl;
        } else {
            url = bid.getVastPlayUrl();
        }

        initController(url);
        AdmBean.ImgBean poster = bid.getImgBean(AdmBean.IMG_POSTER_TYPE);
        AftImageLoader.getInstance().loadUri(getContext(), poster == null ? "" : poster.getUrl(), mBaseImg);
    }

    @Override
    protected void onPreStart() {

    }

    @Override
    protected void onTextureAvailable() {
        if (mVideoListener != null)
            mVideoListener.onSurfaceTextureAvailable();
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
            mSoundImg.setVisibility(isSoundAlwaysShow ? VISIBLE : GONE);
        }

    }

    @Override
    public void onPlayStatusPreparing() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
        if (mLoadingProgress != null)
            mLoadingProgress.setVisibility(VISIBLE);
        if (mSoundImg != null)
            mSoundImg.setVisibility(isSoundAlwaysShow ? VISIBLE : GONE);

    }

    @Override
    public void onPlayStatusPrepared() {

    }

    @Override
    public void onPlayStatusStopped() {

    }

    @Override
    public void onPlayStatusCompleted() {
        if (mBaseImg != null)
            mBaseImg.setVisibility(VISIBLE);
        if (mSeekProgress != null)
            mSeekProgress.setVisibility(GONE);
        if (mSoundImg != null)
            mSoundImg.setVisibility(isSoundAlwaysShow ? VISIBLE : GONE);

        if (mVideoListener != null)
            mVideoListener.onComplete();
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
            mSoundImg.setVisibility(isSoundAlwaysShow ? VISIBLE : GONE);
        String errorMsg = getResources().getString(R.string.aft_media_player_error_wrong);
        if (MediaError.REASON_ERROR_IO.equals(reason) || MediaError.ERROR_CODE_OPEN_FAILED.equals(reason) || MediaError.REASON_ERROR_NETWORK.equals(reason))
            errorMsg = getResources().getString(R.string.aft_media_player_network_err_msg);
        mErrorTxt.setText(errorMsg);
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
    }

    public void setSoundImg(ImageView img, boolean soundAlwaysShow) {
        if (mSoundImg != null)
            mSoundImg.setVisibility(GONE);
        mSoundImg = img;
        mSoundImg.setOnClickListener(mSoundClickLister);
        isSoundAlwaysShow = soundAlwaysShow;
    }
}
