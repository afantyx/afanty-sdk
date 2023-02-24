package com.afanty.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.afanty.R;
import com.afanty.ads.AdError;
import com.afanty.config.BidConfigHelper;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.config.AftConfig;
import com.afanty.internal.fullscreen.BaseFullScreenAd;
import com.afanty.internal.fullscreen.FullScreenAdListener;
import com.afanty.internal.fullscreen.VideoFullScreenAd;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.internal.CreativeType;
import com.afanty.models.Bid;
import com.afanty.utils.AdDataUtils;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.CountDownTimer;

import java.lang.ref.WeakReference;

public class FullAdActivity extends FragmentActivity {
    private static final String TAG = "Aft.FullAdActivity";
    private FullScreenAdListener mAdListener;

    private BaseFullScreenAd mFullScreenAd;
    private CountDownTimer mCountDownTimer;
    private FrameLayout mRootLayout;

    private boolean mInterceptBack;
    private Bid mBid;

    public static void startFullScreenActivity(Context context, BaseFullScreenAd fullScreenAd) {
        try {
            ContextUtils.add(AdConstants.ContextUtil.FULL_SCREEN_AD, fullScreenAd);
            Intent intent = new Intent(context, FullAdActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();

        try {
            mFullScreenAd = (BaseFullScreenAd) ContextUtils.remove(AdConstants.ContextUtil.FULL_SCREEN_AD);
            if (mFullScreenAd == null) {
                onShowFailed("UnSupport creative type");
                return;
            }
            mBid = mFullScreenAd.getBid();
            if (mBid == null) {
                onShowFailed("AdData is null.");
                return;
            }
            setAdaptationOrientation();
            setContentView(R.layout.aft_full_activity_layout);

            View adView = mFullScreenAd.initView(this);
            if (adView == null) {
                onShowFailed("FullScreenAd initView failed");
                return;
            }

            mRootLayout = findViewById(R.id.root);
            mRootLayout.addView(adView);

            initCountDown();

            initListener();
            mAdListener = mFullScreenAd.getAdListener();
            if (mAdListener != null)
                mAdListener.onFullScreenAdShow();
            ActionUtils.increaseShowCount(mBid);
        } catch (Exception e) {
            mInterceptBack = false;
            onShowFailed(e.getMessage());
        }
    }

    private void setFullScreen() {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        this.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    private void setAdaptationOrientation() {
        if (mBid.getCreativeType() == CreativeType.TYPE_V_V1P1T1) {
            if (AdDataUtils.getVastVideoConfig(mBid) != null) {
                CommonUtils.setAdaptationRequestedOrientation(this, mFullScreenAd.getLayoutOrientation(getApplicationContext()));
            }
        } else {
            CommonUtils.setAdaptationRequestedOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void initCountDown() {
        if (mFullScreenAd instanceof VideoFullScreenAd) {
            mFullScreenAd.countDownFinish();
        } else {
            mInterceptBack = true;
            long skipTime = BidConfigHelper.VideoConfig.getClose();
            mFullScreenAd.countDownStart(skipTime * 1000L + "");
            mCountDownTimer = new CountDown(skipTime * 1000L, 1000L, this);
            startCountDown();
        }
    }

    private void initListener() {
        if (mFullScreenAd == null)
            return;
        mFullScreenAd.setFinishListener(this::finish);
    }

    @Override
    protected void onDestroy() {
        if (mAdListener != null) {
            mAdListener.onFullScreenAdDismiss();
        }
        cancelCountDown();

        if (mFullScreenAd != null) {
            mFullScreenAd.onDestroy();
            mFullScreenAd.setFinishListener(null);
            mFullScreenAd = null;
        }
        if (mRootLayout != null) {
            mRootLayout.removeAllViews();
            mRootLayout = null;
        }
        super.onDestroy();
    }

    private void startCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    private void cancelCountDown() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

        if (mFullScreenAd != null)
            mFullScreenAd.countDownFinish();

        mInterceptBack = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mInterceptBack) {
            return;
        }

        if (mFullScreenAd != null && mFullScreenAd.onBackPressed())
            return;

        super.onBackPressed();
    }

    private void onShowFailed(String msg) {
        if (mAdListener != null)
            mAdListener.onFullScreenAdShowError(AdError.DIS_CONDITION_ERROR);
        finish();
    }

    private static class CountDown extends CountDownTimer {
        private final WeakReference<FullAdActivity> activityRef;

        CountDown(long millisInFuture, long countDownInterval, FullAdActivity activity) {
            super(millisInFuture, countDownInterval);
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String value = String.valueOf((int) (millisUntilFinished / 1000) + 1);
            FullAdActivity activity = activityRef.get();
            if (activity != null)
                activity.mFullScreenAd.countDownOnTick(value);
        }

        @Override
        public void onFinish() {
            FullAdActivity activity = activityRef.get();
            if (activity != null) {
                activity.mInterceptBack = false;
                activity.mFullScreenAd.countDownFinish();
            }
        }
    }
}
