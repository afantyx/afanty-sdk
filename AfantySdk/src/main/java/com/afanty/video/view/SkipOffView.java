package com.afanty.video.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.afanty.R;
import com.afanty.ads.AdStyle;
import com.afanty.config.BidConfigHelper;
import com.afanty.internal.config.AftConfig;
import com.afanty.models.Bid;
import com.afanty.vast.VastVideoConfig;

import java.util.Locale;

public class SkipOffView extends FrameLayout {
    private static final String TAG = "SkipOffView";
    private LinearLayout mLLTimeRoundBg;
    private TextView mTvTimeRemain;
    private TextView mTvDivider;
    private FrameLayout mFLClose;
    private ImageView mIvClose;
    private TextView mTvSkipRemain;

    private Bid mBid;
    private AdStyle mAdStyle;

    private boolean isShowComplete;
    private boolean isSkipTimeReached;
    private boolean isCloseTimeReached;

    private int mSkipTime;

    private CloseClickListener mCloseClickListener;

    public SkipOffView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SkipOffView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SkipOffView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public boolean isCloseTimeReached() {
        return isCloseTimeReached;
    }

    public boolean isSkipTimeReached() {
        return isSkipTimeReached;
    }

    public void setCloseClickListener(CloseClickListener closeClickListener) {
        this.mCloseClickListener = closeClickListener;
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.aft_full_vast_remain_close_layout, this);
        mLLTimeRoundBg = findViewById(R.id.rl_time_bg);
        mTvTimeRemain = findViewById(R.id.tv_seconds);
        mTvDivider = findViewById(R.id.tv_divider);
        mFLClose = findViewById(R.id.fl_close);
        mIvClose = findViewById(R.id.iv_close);
        mTvSkipRemain = findViewById(R.id.tv_count);

        initListener();
    }

    private void initListener() {
        OnClickListener clickListener = v -> {
            if (mCloseClickListener != null)
                mCloseClickListener.onClick(v);
        };
        mFLClose.setOnClickListener(clickListener);
        mIvClose.setOnClickListener(clickListener);
        mTvTimeRemain.setOnClickListener(clickListener);
    }

    public void setBid(@NonNull Bid bid, AdStyle adStyle) {
        mBid = bid;
        mAdStyle = adStyle;
    }

    public void initSkipOffView(int duration) {
        if (mBid == null)
            return;
        VastVideoConfig mVastVideoConfig = mBid.getVastVideoConfig();
        initSkipRemain(duration, mVastVideoConfig);

        if (mAdStyle == AdStyle.REWARDED_AD) {
            if (isSupportJump()) {
                mTvSkipRemain.setText(String.valueOf(mSkipTime / 1000));
                mTvSkipRemain.setVisibility(VISIBLE);
            }
        } else {
            if (isSupportJump() && !isShowComplete) {
                mTvSkipRemain.setText(String.valueOf(mSkipTime / 1000));
                mTvSkipRemain.setVisibility(VISIBLE);
            }
        }
    }

    private void initSkipRemain(int duration, VastVideoConfig mVastVideoConfig) {
        mSkipTime = getFullAdSkipPoint();
        if (mVastVideoConfig != null) {
            Integer skipOffset = mVastVideoConfig.getSkipOffsetMillis(duration);
            if (skipOffset != null) {
                mSkipTime = skipOffset;
            }
        }
        mSkipTime = Math.min(mSkipTime, duration);
    }

    private boolean isSupportJump() {
        return mBid != null && mBid.isSupportSkip();
    }

    private int getFullAdSkipPoint() {
        int skipPoint = BidConfigHelper.VideoConfig.getSkip();
        if (skipPoint == -1) {
            return Integer.MAX_VALUE;
        }
        return skipPoint * 1000;
    }

    private int getFullAdClosePoint() {
        int closePoint = BidConfigHelper.VideoConfig.getClose();
        if (closePoint == -1) {
            return Integer.MAX_VALUE;
        }
        return closePoint * 1000;
    }

    public void videoComplete() {
        isShowComplete = true;
        if (mAdStyle == AdStyle.REWARDED_AD) {
            mLLTimeRoundBg.setVisibility(VISIBLE);
            mTvSkipRemain.setVisibility(GONE);
            mTvTimeRemain.setText(getContext().getString(R.string.aft_countdown_got_reward));
        } else {
            mTvSkipRemain.setVisibility(GONE);
            mTvTimeRemain.setVisibility(GONE);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.aft_vast_player_reward_time_bg2, null);
            mLLTimeRoundBg.setBackground(drawable);
            mLLTimeRoundBg.setVisibility(VISIBLE);
        }
        mIvClose.setVisibility(VISIBLE);
        autoShowDivider();
    }

    public void updateRemainTime(int duration, int currentPosition) {
        if (mBid == null)
            return;
        isCloseTimeReached = currentPosition > getFullAdClosePoint();
        if (isSupportJump()) {
            isSkipTimeReached = currentPosition > mSkipTime;
            if (isSkipTimeReached) {
                mTvSkipRemain.setVisibility(INVISIBLE);
                mIvClose.setVisibility(VISIBLE);
            } else {
                int remainTime = (mSkipTime - currentPosition) / 1000;
                mTvSkipRemain.setText(String.format(Locale.getDefault(), "%ds", remainTime));
                mTvSkipRemain.setVisibility(View.VISIBLE);
                mIvClose.setVisibility(GONE);
            }
        } else if (isCloseTimeReached && mIvClose.getVisibility() == GONE) {
            mIvClose.setVisibility(VISIBLE);
            mTvSkipRemain.setVisibility(GONE);
        }

        if (mAdStyle == AdStyle.REWARDED_AD) {
            updateRewardedRemainTime(duration, currentPosition);
        } else {
            updateInterstitialRemainTime(duration, currentPosition);
        }
        autoShowDivider();
    }

    private void updateRewardedRemainTime(int duration, int currentPosition) {
        int remainTime = (duration - currentPosition) / 1000;
        if (remainTime > 0) {
            mTvTimeRemain.setText(getContext().getString(R.string.aft_countdown_rewarded, remainTime + ""));
        } else {
            mTvTimeRemain.setText(getContext().getString(R.string.aft_countdown_got_reward));
        }
    }

    private void updateInterstitialRemainTime(int duration, int currentPosition) {
        int remainTime = (duration - currentPosition) / 1000;
        if (remainTime > 0) {
            mTvTimeRemain.setText(getContext().getString(R.string.aft_countdown_skip, remainTime + ""));
        } else {
            mTvTimeRemain.setVisibility(GONE);
            mLLTimeRoundBg.setVisibility(GONE);
        }
    }

    private void autoShowDivider() {
        if (mTvTimeRemain.getVisibility() == VISIBLE && (mIvClose.getVisibility() == VISIBLE || mTvSkipRemain.getVisibility() == VISIBLE)) {
            mTvDivider.setVisibility(VISIBLE);
        } else {
            mTvDivider.setVisibility(GONE);
        }
    }

    public interface CloseClickListener {
        void onClick(View view);
    }
}
