package com.afanty.internal.fullscreen;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.afanty.R;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.internal.AdConstants;
import com.afanty.models.Bid;
import com.afanty.video.view.FullScreenVideoView;
import com.afanty.video.view.MediaViewCallBack;

/**
 * Full screen with video (both vast and native)
 */
public class VideoFullScreenAd extends BaseFullScreenAd {
    private static final String TAG = "FullScreen.Video";
    private FullScreenVideoView mFullScreenVideoView;

    @Override
    public int getLayoutView() {
        return R.layout.aft_full_screen_vast_video;
    }

    @Override
    public View initView(final Context context) {
        Bid bid = getBid();
        if (bid == null || bid.getAdmBean() == null) {
            return null;
        }
        View view = View.inflate(context, getLayoutView(), null);
        mFullScreenVideoView = new FullScreenVideoView(context, mAdStyle);
        mFullScreenVideoView.setBid(bid);
        mFullScreenVideoView.setCheckWindowFocus(false);
        mFullScreenVideoView.setRewardVideoListener(new MediaViewCallBack() {
            @Override
            public void onSurfaceTextureAvailable() {
                mFullScreenVideoView.startPlay();
            }

            @Override
            public void onFinish() {
                if (mFinishListener != null)
                    mFinishListener.onClick();
            }

            @Override
            public void onAdRewarded() {
                if (getAdListener() != null)
                    getAdListener().onFullScreenAdRewarded();
            }

            @Override
            public void onPerformClick(String sourceType, boolean openOpt, boolean azOpt) {
                if (AdConstants.Vast.SOURCE_TYPE_CARDBUTTON.equals(sourceType)) {
                    performActionForAdClicked(context, sourceType, ActionUtils.getDownloadOptTrig(openOpt, azOpt));
                } else
                    performActionForInternalClick(context, sourceType);
            }
        });

        setTopMargin(view);

        FrameLayout adContainer = view.findViewById(R.id.ad_container);
        adContainer.removeAllViews();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adContainer.addView(mFullScreenVideoView, params);

        return view;
    }

    @Override
    protected Point resolvedAdSize(int height) {
        return null;
    }

    @Override
    public void countDownStart(String startTime) {
    }

    @Override
    public void countDownOnTick(String value) {
    }

    @Override
    public void countDownFinish() {
    }

    @Override
    public boolean onBackPressed() {
        if (mFullScreenVideoView != null) {
            return mFullScreenVideoView.handleClose();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (mFullScreenVideoView != null) {
            mFullScreenVideoView.destroy();
            mFullScreenVideoView = null;
        }
    }
}
