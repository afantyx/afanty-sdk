package com.afanty.internal.fullscreen;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afanty.R;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AftImageLoader;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.config.AftConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.internal.CreativeType;
import com.afanty.internal.view.CustomProgressBar;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.BlurUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.video.view.MediaView;
import com.afanty.video.view.MediaViewCallBack;

/**
 * Native Full Screen
 */
public class NativeFullScreenAd extends BaseFullScreenAd {
    private View mFullLayout;
    private FrameLayout mForegroundLayout;
    private ImageView mBackgroundView;
    private ImageView mCloseImg;
    private TextView mCountView;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubTitle;
    private CustomProgressBar mDownloadBtn;
    private View mDivider;
    private ImageView mVolume;

    @Override
    public int getLayoutView() {
        int layoutId = BridgeManager.getResourceId(ContextUtils.getContext(), "aft_full_screen_native_layout_ex");
        if (layoutId == 0) {
            layoutId = R.layout.aft_full_screen_native_layout;
        }
        return layoutId;
    }

    @Override
    public View initView(Context context) {
        Bid bid = getBid();
        if (bid == null || bid.getAdmBean() == null) {
            return null;
        }

        View view = View.inflate(context, getLayoutView(), null);

        mFullLayout = view.findViewById(R.id.ll_bg);
        mForegroundLayout = view.findViewById(R.id.fl_foreground);
        mBackgroundView = view.findViewById(R.id.iv_background);
        mIcon = view.findViewById(R.id.iv_icon);
        mTitle = view.findViewById(R.id.tv_title);
        mSubTitle = view.findViewById(R.id.tv_sub_title);
        mCloseImg = view.findViewById(R.id.iv_close);
        mCountView = view.findViewById(R.id.tv_count);
        mDownloadBtn = view.findViewById(R.id.tp_button);
        mDivider = view.findViewById(R.id.divider);
        mVolume = view.findViewById(R.id.iv_volume);

        mBackgroundView.setDrawingCacheEnabled(true);

        setTopMargin(view);

        AdmBean.ImgBean poster = bid.getImgBean(AdmBean.IMG_POSTER_TYPE);
        setRealAdSize(context, poster == null ? 0 : poster.getH());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getAdSize().x, getAdSize().y);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mForegroundLayout.setLayoutParams(layoutParams);

        mForegroundLayout.setVisibility(View.INVISIBLE);
        mBackgroundView.setVisibility(View.INVISIBLE);
        mForegroundLayout.removeAllViews();

        initData(context, bid);

        initListener(context);

        return view;
    }

    private void initData(Context context, Bid bid) {
        boolean isVideoAd = CreativeType.isVideo(bid);
        mTitle.setText(bid.getTitle());
        mSubTitle.setText(bid.getDataByType(AdmBean.TEXT_DESC));
        mDownloadBtn.setText(bid.getDataByType(AdmBean.TEXT_BTN));
        AdmBean.ImgBean imgBean = bid.getImgBean(AdmBean.IMG_ICON_TYPE);
        AftImageLoader.getInstance().loadUri(context, imgBean == null ? "" : imgBean.getUrl(), mIcon);

        if (isVideoAd) {
            addVideo(mForegroundLayout, mForegroundLayout.getContext());
        } else {
            addImage(mForegroundLayout, mForegroundLayout.getContext());
        }
        AdmBean.ImgBean poster = bid.getImgBean(AdmBean.IMG_POSTER_TYPE);
        if (mBackgroundView == null)
            return;
        AftImageLoader.getInstance().loadUri(context, poster == null ? "" : poster.getUrl(), mBackgroundView, isSuccess -> {
            if (isSuccess) {
                if (mBackgroundView != null) {
                    mBackgroundView.post(() -> BlurUtils.blurView(mBackgroundView, blurResult -> {
                        if (mBackgroundView != null) {
                            mBackgroundView.setImageBitmap(blurResult);
                            mBackgroundView.setVisibility(View.VISIBLE);
                        }
                    }));
                }
            }
            mForegroundLayout.setVisibility(View.VISIBLE);
        });
    }

    private void addImage(FrameLayout adContainer, Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        AdmBean.ImgBean imgBean = getBid().getImgBean(AdmBean.IMG_POSTER_TYPE);
        AftImageLoader.getInstance().loadUri(context.getApplicationContext(), imgBean == null ? "" : imgBean.getUrl(), imageView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adContainer.addView(imageView, 0, params);
    }

    private void addVideo(FrameLayout adContainer, Context context) {
        final MediaView mediaView = new MediaView(context);
        mDivider.setVisibility(View.VISIBLE);
        mVolume.setVisibility(View.VISIBLE);
        mediaView.setBid(getBid());
        mediaView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mediaView.setCheckWindowFocus(false);
        mediaView.setMediaViewListener(new MediaViewCallBack() {
            @Override
            public void onSurfaceTextureAvailable() {
                mediaView.startPlay();
                mediaView.setCheckWindowFocus(true);
            }

            @Override
            public void onComplete() {
                mDivider.setVisibility(View.GONE);
                mVolume.setVisibility(View.GONE);
            }
        });
        mediaView.setSoundImg(mVolume, true);

        adContainer.addView(mediaView);
    }

    private void initListener(Context context) {
        View.OnClickListener clickListener = v -> performActionForInternalClick(context);
        mIcon.setOnClickListener(clickListener);
        mTitle.setOnClickListener(clickListener);
        mSubTitle.setOnClickListener(clickListener);
        mForegroundLayout.setOnClickListener(clickListener);
        mBackgroundView.setOnClickListener(clickListener);
        mDownloadBtn.registerClick(getBid(), (openOpt, CTAOpt) -> {
            int downloadOptTrig = ActionUtils.getDownloadOptTrig(openOpt, CTAOpt);
            performActionForAdClicked(context, AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, downloadOptTrig);
        });

        final int CLICK_ALL_AREAS = 0;
        if (AftConfig.getInterstitialClickArea() == CLICK_ALL_AREAS) {
            mFullLayout.setOnClickListener(clickListener);
        }
    }

    @Override
    protected Point resolvedAdSize(int height) {
        if (height == 346 / 2 || height == 346)
            return new Point(660, 346);
        else
            return new Point(660, 371);
    }

    @Override
    public void countDownStart(String startTime) {
        if (mCloseImg != null) {
            mCloseImg.setVisibility(View.GONE);
        }
        mCountView.setVisibility(View.VISIBLE);
        mCountView.setText(startTime);
    }

    @Override
    public void countDownOnTick(String value) {
        Context context = mCountView.getContext();
        String countText = mAdStyle == AdStyle.REWARDED_AD ?
                context.getString(R.string.aft_countdown_rewarded, value) : context.getString(R.string.aft_countdown_skip, value);
        mCountView.setText(countText);
    }

    @Override
    public void countDownFinish() {
        mCountView.setVisibility(View.GONE);
        if (mCloseImg != null) {
            mCloseImg.setVisibility(View.VISIBLE);
            mCloseImg.setOnClickListener(v -> {
                if (mFinishListener != null)
                    mFinishListener.onClick();
            });
        }
    }

    @Override
    public void onDestroy() {
        if (mDownloadBtn != null)
            mDownloadBtn.destroy();
        if (mBackgroundView != null) {
            mBackgroundView.setBackground(null);
            mBackgroundView = null;
        }
        if (mCloseImg != null) {
            mCloseImg.setBackground(null);
            mCloseImg = null;
        }
    }
}
