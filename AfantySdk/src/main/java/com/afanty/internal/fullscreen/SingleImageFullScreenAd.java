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
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;

/**
 * Native single image
 */
public class SingleImageFullScreenAd extends BaseFullScreenAd {
    private ImageView mCloseImg;
    private TextView mCountView;

    @Override
    public int getLayoutView() {
        return R.layout.aft_full_screen_layout;
    }

    @Override
    public View initView(Context context) {
        Bid bid = getBid();
        if (bid == null || bid.getAdmBean() == null) {
            return null;
        }
        View view = View.inflate(context, getLayoutView(), null);
        FrameLayout mForegroundLayout = view.findViewById(R.id.fl_foreground);
        mCloseImg = view.findViewById(R.id.iv_close);
        mCountView = view.findViewById(R.id.tv_count);

        setTopMargin(view);

        AdmBean.ImgBean poster = bid.getImgBean(AdmBean.IMG_POSTER_TYPE);
        if (poster != null) {
            setRealAdSize(context, poster == null ? 0 : poster.getH());
        }

        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mForegroundLayout.setLayoutParams(layoutParams);

        mForegroundLayout.setOnClickListener(v -> performActionForInternalClick(v.getContext()));

        addImage(mForegroundLayout, mForegroundLayout.getContext());
        return view;
    }

    private void addImage(FrameLayout adContainer, Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        AftImageLoader.getInstance().loadUri(context, getBid().getUrlByType(AdmBean.IMG_POSTER_TYPE), imageView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        adContainer.addView(imageView, 0, params);
    }

    @Override
    protected Point resolvedAdSize(int height) {
        return new Point(720, 1067);
    }

    @Override
    public void countDownStart(String startTime) {
        mCloseImg.setVisibility(View.GONE);
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
        mCloseImg.setVisibility(View.VISIBLE);
        mCountView.setVisibility(View.GONE);
        mCloseImg.setOnClickListener(v -> {
            if (mFinishListener != null)
                mFinishListener.onClick();
        });
    }

    @Override
    public void onDestroy() {
        if (mCloseImg != null) {
            mCloseImg.setBackground(null);
            mCloseImg = null;
        }
    }
}
