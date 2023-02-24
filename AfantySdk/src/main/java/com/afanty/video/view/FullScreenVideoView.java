package com.afanty.video.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afanty.R;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AftImageLoader;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.view.CustomProgressBar;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.AdDataUtils;
import com.afanty.utils.DeviceUtils;
import com.afanty.utils.NetworkUtils;
import com.afanty.vast.VastTracker;
import com.afanty.vast.VastVideoConfig;
import com.afanty.video.MediaStatsData;
import com.afanty.video.PlayerUrlHelper;
import com.afanty.video.vastplayer.RewardCloseDialog;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static com.afanty.vast.VideoTrackingEvent.FIRST_QUARTILE;
import static com.afanty.vast.VideoTrackingEvent.MIDPOINT;
import static com.afanty.vast.VideoTrackingEvent.THIRD_QUARTILE;

public class FullScreenVideoView extends BaseMediaView {
    private static final String TAG = "MediaView.FULL";
    private final Context mContext;
    private final AdStyle mAdStyle;

    private VastVideoConfig mVastVideoConfig;
    private ProgressBar mLoadingProgress;
    private FrameLayout mCompanionContainer;
    private RelativeLayout mBottomCard;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mMessage;
    private CustomProgressBar mCardButton;
    private CustomProgressBar mBottomButton;
    private ImageView mIvVolume;
    private View mSoundDivideLine;
    private SkipOffView mSkipOffView;

    private FullScreenCardAnimationHelper mFullScreenCardAnimationHelper;
    private VideoListener mVideoListener;
    private Bid mBid;

    private CustomProgressBar mEndBtn;
    private CompanionView mCompanionAdView;
    private boolean mIsCompanionError = false;

    private RewardCloseDialog mCloseDialog;
    private boolean isCloseDialogShowed;

    private boolean isVideoCompleted;
    private boolean isOnVideoError;
    private boolean isShowComplete = false;
    private boolean hasRewarded;

    private int mAppOrientation = SCREEN_ORIENTATION_PORTRAIT;

    public FullScreenVideoView(Context context, AdStyle adStyle) {
        super(context, null);
        mContext = context;
        mAdStyle = adStyle;
        initView();
    }

    private void initView() {
        int layoutId = BridgeManager.getResourceId(mContext, "aft_full_vast_layout_ex");
        if (layoutId == 0) {
            layoutId = R.layout.aft_full_vast_layout;
        }
        View view = inflate(mContext, layoutId, null);

        mLoadingProgress = view.findViewById(R.id.loading_progress);

        mCompanionContainer = view.findViewById(R.id.fl_companion_container);

        mBottomCard = view.findViewById(R.id.rl_card);
        mIcon = view.findViewById(R.id.iv_icon);
        mTitle = view.findViewById(R.id.tv_title);
        mMessage = view.findViewById(R.id.tv_message);
        mCardButton = view.findViewById(R.id.btn_cta_card);

        mBottomButton = view.findViewById(R.id.btn_cta_bottom);

        mIvVolume = view.findViewById(R.id.iv_volume);
        mIvVolume.setVisibility(GONE);
        mSoundDivideLine = view.findViewById(R.id.v_sound_divide);

        mSkipOffView = view.findViewById(R.id.sov_skip_off);
        mCoverLayout.removeAllViews();
        mCoverLayout.addView(view);

        initData();

        initListener();
    }

    private void initData() {
        setMuteState(false);
        if (isRewardedAd())
            return;
        if (mVastVideoConfig != null) {
            mCardButton.setVisibility(VISIBLE);
        } else {
            mCardButton.setVisibility(GONE);
        }
    }

    private void initListener() {
        mTextureView.setOnClickListener(v -> {
            handleClick(AdConstants.Vast.SOURCE_TYPE_VIDEO, false, false);
        });
        mIvVolume.setOnClickListener(mSoundClickLister);
        if (mSkipOffView != null) {
            mSkipOffView.setCloseClickListener(v -> {
                handleClose();
            });
        }
    }

    public void setBid(@NonNull Bid bid) {
        mBid = bid;
        VastVideoConfig vastVideoConfig = AdDataUtils.getVastVideoConfig(bid);
        if (vastVideoConfig != null) {
            mVastVideoConfig = vastVideoConfig;
        }

        initController(getVideoUrl());

        initBottomCardView();

        initBottomButton();

        initCompanionAdView();

        if (mSkipOffView != null)
            mSkipOffView.setBid(mBid, mAdStyle);
    }

    public void setRewardVideoListener(VideoListener videoListener) {
        this.mVideoListener = videoListener;
    }

    private String getVideoUrl() {
        String url;
        if (mVastVideoConfig != null) {
            url = TextUtils.isEmpty(mVastVideoConfig.getDiskMediaFileUrl()) ? mVastVideoConfig.getNetworkMediaFileUrl() : mVastVideoConfig.getDiskMediaFileUrl();
        } else {
            String playUrl = PlayerUrlHelper.getVideoPlayUrl(mBid);
            url = TextUtils.isEmpty(playUrl) ? mBid.getVastPlayUrl() : playUrl;
        }
        return url;
    }

    private void initBottomCardView() {
        if (mIcon != null) {
            String iconUrl = getIconUrl();
            if (TextUtils.isEmpty(iconUrl)) {
                mBottomCard.setVisibility(GONE);
                mBottomButton.setVisibility(VISIBLE);
            } else {
                AftImageLoader.getInstance().loadLandingRoundCornerUrl(getContext(), iconUrl, mIcon, R.drawable.aft_icon_bg, getContext().getResources().getDimensionPixelSize(R.dimen.aft_common_dimens_8dp));
                mBottomCard.post(() -> {
                    mFullScreenCardAnimationHelper = new FullScreenCardAnimationHelper()
                            .setCardLayout(mBottomCard)
                            .setStartY(DeviceUtils.getScreenHeightByWindow(getContext()) - mBottomCard.getMeasuredHeight())
                            .setDuration(500);
                    mFullScreenCardAnimationHelper.startCardInAnimation();
                });
                mBottomButton.setVisibility(GONE);
            }
        }

        if (mTitle != null) {
            VastVideoConfig vastVideoConfig = AdDataUtils.getVastVideoConfig(mBid);
            if (vastVideoConfig != null && !TextUtils.isEmpty(vastVideoConfig.getAdTitle())) {
                mTitle.setText(vastVideoConfig.getAdTitle());
            } else if (!TextUtils.isEmpty(mBid.getTitle())) {
                mTitle.setText(mBid.getTitle());
            } else {
                mTitle.setVisibility(INVISIBLE);
            }
        }

        if (mMessage != null) {
            String desc = mBid.getDataByType(AdmBean.TEXT_DESC);
            if (!TextUtils.isEmpty(desc))
                mMessage.setText(desc);
            else {
                mMessage.setVisibility(INVISIBLE);
            }
        }

        String buttonText = mBid.getDataByType(AdmBean.TEXT_BTN);
        if (mCardButton != null) {
            if (!TextUtils.isEmpty(buttonText)) {
                mCardButton.setText(buttonText);
            }
            mCardButton.registerClick(mBid, (openOpt, CTAOpt) -> {
                handleClick(AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, openOpt, CTAOpt);
            });
        }
    }

    private String getIconUrl() {
        VastVideoConfig vastVideoConfig = AdDataUtils.getVastVideoConfig(mBid);
        String iconUrl = "";
        if (vastVideoConfig != null && vastVideoConfig.getVastIconConfig() != null) {
            iconUrl = vastVideoConfig.getVastIconConfig().getVastResource().getResource();
        } else {
            iconUrl = mBid.getUrlByType(AdmBean.IMG_ICON_TYPE);
        }
        return iconUrl;
    }

    private void initBottomButton() {
        String buttonText = mBid.getDataByType(AdmBean.TEXT_BTN);

        if (mBottomButton != null) {
            if (!TextUtils.isEmpty(buttonText)) {
                mBottomButton.setText(buttonText);
            }
            mBottomButton.registerClick(mBid, (openOpt, CTAOpt) -> {
                handleClick(AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, openOpt, CTAOpt);
            });
        }
    }

    private void initCompanionAdView() {
        mCompanionContainer.removeAllViews();

        boolean hasNetwork = NetworkUtils.hasNetWork(mContext);
        boolean isUseVastWeb = (isRewardedAd() || mVastVideoConfig != null) && hasNetwork;
        mCompanionAdView = new CompanionView(mContext);
        mCompanionAdView.setCompanionViewListener(new CompanionView.CompanionViewListener() {
            @Override
            public void onReceivedError() {
                mIsCompanionError = true;
                statsError("603");
            }

            @Override
            public void onClick() {
                handleClick(AdConstants.Vast.SOURCE_TYPE_COMPANIONVIEW, false, false);
            }
        });
        mCompanionAdView.renderCompanionView(isUseVastWeb, mBid, mAppOrientation);
        mCompanionContainer.addView(mCompanionAdView);
    }

    public void setCurrentOrientation(int orientation) {
        if (orientation == SCREEN_ORIENTATION_LANDSCAPE) {
            mAppOrientation = SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            mAppOrientation = SCREEN_ORIENTATION_PORTRAIT;
        }
    }

    private void handleClick(String sourceType, boolean openOpt, boolean CTAOpt) {
        if (mVideoListener != null) {
            mVideoListener.onPerformClick(sourceType, openOpt, CTAOpt);
        }
    }

    public boolean handleClose() {
        if (isVideoCompleted) {
            videoCompleted();
            return false;
        }

        if (!(mContext instanceof Activity) || ((Activity) mContext).isFinishing()) {
            statsSkip();
            statsClose();
            if (mVideoListener != null)
                mVideoListener.onFinish();
            return true;
        }

        if (mSkipOffView != null && mSkipOffView.isSkipTimeReached()) {
            pausePlay();
            onPlayStatusCompleted();
            return true;
        }

        if (mSkipOffView != null && !mSkipOffView.isCloseTimeReached())
            return true;

        if (isRewardedAd()) {
            return handleRewardedClose();
        } else {
            return handleInterstitialClose();
        }
    }

    private synchronized boolean handleRewardedClose() {
        if (mCloseDialog != null && isCloseDialogShowed) {
            mCloseDialog.cancel();
            mCloseDialog = null;
            return true;
        }
        isCloseDialogShowed = true;
        getRewardCloseDialog().show();
        return true;
    }

    private void videoCompleted() {
        if (!isOnVideoError)
            statsClose();

        if (mVideoListener != null)
            mVideoListener.onFinish();
    }

    private RewardCloseDialog getRewardCloseDialog() {
        if (mCloseDialog != null) {
            return mCloseDialog;
        }
        mCloseDialog = new RewardCloseDialog(mContext);
        mCloseDialog.setConfirmButton(() -> {
            isCloseDialogShowed = false;
            mCloseDialog.dismiss();

            statsSkip();
            statsClose();
            if (mVideoListener != null)
                mVideoListener.onFinish();
        }).setCancelButton(() -> {
            isCloseDialogShowed = false;
        }).setCancelable(false);
        return mCloseDialog;
    }

    private boolean handleInterstitialClose() {
        statsSkip();
        statsClose();
        if (mVideoListener != null)
            mVideoListener.onFinish();
        return false;
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
    }

    @Override
    protected MediaStatsData createMediaStatsData() {
        MediaStatsData mediaStatsData = new MediaStatsData();
        if (mVastVideoConfig != null && mVastVideoConfig.getVastCompanionAd(mAppOrientation) != null) {
            List<String> quarterList = new ArrayList<>();
            List<String> middleList = new ArrayList<>();
            List<String> thirdList = new ArrayList<>();
            for (VastTracker tracker : mVastVideoConfig.getFractionalTrackers()) {
                if (tracker == null)
                    continue;
                if (FIRST_QUARTILE.getName().equals(tracker.getEvent())) {
                    quarterList.add(tracker.getContent());
                }
                if (MIDPOINT.getName().equals(tracker.getEvent())) {
                    middleList.add(tracker.getContent());
                }
                if (THIRD_QUARTILE.getName().equals(tracker.getEvent())) {
                    thirdList.add(tracker.getContent());
                }
            }
            mediaStatsData.setQuarterTrackUrls(quarterList);
            mediaStatsData.setHalfTrackUrls(middleList);
            mediaStatsData.setThreeQuarterUrls(thirdList);
            mediaStatsData.setCompleteTrackUrls(getTrackUrlList(mVastVideoConfig.getCompleteTrackers()));
            mediaStatsData.setErrorTrackUrls(getTrackUrlList(mVastVideoConfig.getErrorTrackers()));

            mediaStatsData.setCloseTrackUrls(getTrackUrlList(mVastVideoConfig.getCloseTrackers()));
            mediaStatsData.setCreativeViewTrackUrls(getTrackUrlList(mVastVideoConfig.getVastCompanionAd(mAppOrientation).getCreativeViewTrackers()));
            mediaStatsData.setSkipTrackUrls(getTrackUrlList(mVastVideoConfig.getSkipTrackers()));
            mediaStatsData.setMuteTrackUrls(getTrackUrlList(mVastVideoConfig.getMuteTrackers()));
            mediaStatsData.setUnMuteTrackUrls(getTrackUrlList(mVastVideoConfig.getUnMuteTrackers()));
            mediaStatsData.setImpressionTrackers(getTrackUrlList(mVastVideoConfig.getImpressionTrackers()));

        }
        return mediaStatsData;
    }

    @Override
    protected void onSoundClick(boolean isMute) {
        if (mIvVolume != null)
            mIvVolume.setSelected(isMute);
    }

    private List<String> getTrackUrlList(List<VastTracker> trackers) {
        List<String> trackUrlList = new ArrayList<>();
        for (VastTracker tracker : trackers) {
            if (tracker == null)
                continue;
            trackUrlList.add(tracker.getContent());
        }
        return trackUrlList;
    }

    @Override
    public void onPlayStatusStarted() {
        mLoadingProgress.setVisibility(GONE);
        mIvVolume.setVisibility(VISIBLE);
        mSoundDivideLine.setVisibility(VISIBLE);
        setCheckWindowFocus(true);
    }

    @Override
    public void onPlayStatusPreparing() {
        mLoadingProgress.setVisibility(VISIBLE);
    }

    @Override
    public void onPlayStatusPrepared() {
    }

    @Override
    public void onPlayStatusStopped() {
        mLoadingProgress.setVisibility(GONE);
    }

    @Override
    public void onPlayStatusCompleted() {
        setCheckWindowFocus(false);

        isVideoCompleted = true;
        showCompleteLayout();
        handleRewarded();
    }

    private void showCompleteLayout() {
        isShowComplete = true;
        mIvVolume.setVisibility(GONE);
        mSoundDivideLine.setVisibility(GONE);
        mBottomCard.setVisibility(GONE);
        if (mSkipOffView != null)
            mSkipOffView.videoComplete();

        mBottomButton.setVisibility(VISIBLE);

        if (isCanShowCompanion()) {
            mCompanionAdView.setVisibility(VISIBLE);
            mCompanionContainer.setVisibility(VISIBLE);
            statsCreativeView();
        } else {
            if (!TextUtils.isEmpty(getIconUrl())) {
                mCompanionContainer.removeAllViews();
                mCompanionContainer.setVisibility(VISIBLE);
                showVideoEndCard();
            }
            mCompanionContainer.setVisibility(GONE);
        }
    }

    private void handleRewarded() {
        if (hasRewarded)
            return;
        if (!isRewardedAd())
            return;
        if (mVideoListener == null)
            return;
        mVideoListener.onAdRewarded();
        hasRewarded = true;
    }

    private boolean isCanShowCompanion() {
        if (mVastVideoConfig == null)
            return false;
        if (mVastVideoConfig.getVastCompanionAd(mAppOrientation) == null)
            return false;
        if (mCompanionAdView == null)
            return false;
        return !mIsCompanionError;
    }

    private void showVideoEndCard() {
        int layoutId = BridgeManager.getResourceId(mContext, "aft_full_screen_video_end_layout_ex");
        if (layoutId == 0) {
            layoutId = R.layout.aft_full_screen_video_end_layout;
        }
        View endView = inflate(mContext, layoutId, null);
        ImageView endIcon = endView.findViewById(R.id.iv_end_icon);
        OnClickListener endViewClickListener = v -> handleClick(AdConstants.Vast.SOURCE_TYPE_TAILNONEBUTTON, false, false);
        endIcon.setImageDrawable(mIcon.getDrawable());
        endIcon.setOnClickListener(endViewClickListener);

        TextView endTitle = endView.findViewById(R.id.tv_end_title);
        endTitle.setText(mTitle.getText());
        endTitle.setOnClickListener(endViewClickListener);

        TextView endMessage = endView.findViewById(R.id.tv_end_message);
        endMessage.setText(mMessage.getText());
        endMessage.setOnClickListener(endViewClickListener);

        mEndBtn = endView.findViewById(R.id.tp_end_btn);
        mEndBtn.setText(mBottomButton.getText());

        mEndBtn.registerClick(mBid, (openOpt, CTAOpt) -> handleClick(AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, openOpt, CTAOpt));
        final LayoutParams companionAdLayout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        companionAdLayout.gravity = Gravity.CENTER;
        mCompanionContainer.addView(endView, companionAdLayout);
        mBottomButton.setVisibility(GONE);

    }

    @Override
    public void onPlayStatusError(String reason, Throwable th) {
        isVideoCompleted = true;
        isOnVideoError = true;
        mLoadingProgress.setVisibility(GONE);
        onPlayStatusCompleted();
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    public void onProgressUpdate(int duration, int progress) {
        updateTimeWidget(duration, progress);
    }

    private void updateTimeWidget(int duration, int currentPosition) {
        if (isShowComplete) {
            return;
        }
        if (mSkipOffView != null)
            mSkipOffView.updateRemainTime(duration, currentPosition);
        int rewardedTime = mBid!=null?mBid.getRewardTime():-1;
        if (rewardedTime > 0 && currentPosition > rewardedTime * 1000) {
            handleRewarded();
        }
    }

    private boolean isRewardedAd() {
        return mAdStyle == AdStyle.REWARDED_AD;
    }

    @Override
    public void onMaxProgressUpdate(int duration) {
        if (mSkipOffView != null)
            mSkipOffView.initSkipOffView(getDuration());
    }

    @Override
    public void restart() {
    }

    @Override
    public void start() {
    }

    public void destroy() {
        if (mBottomButton != null)
            mBottomButton.destroy();
        if (mCardButton != null)
            mCardButton.destroy();
        if (mEndBtn != null)
            mEndBtn.destroy();
        if (mCompanionAdView != null) {
            mCompanionAdView.removeAllViews();
            mCompanionAdView = null;
        }
        if (mSkipOffView != null) {
            mSkipOffView.removeAllViews();
            mSkipOffView = null;
        }
        stopPlay();
    }
}
