package com.afanty.internal.fullscreen;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.afanty.R;
import com.afanty.ads.AdStyle;
import com.afanty.internal.Ad;
import com.afanty.internal.action.ActionTrigger;
import com.afanty.models.Bid;
import com.afanty.utils.ScreenUtils;

public abstract class BaseFullScreenAd {
    private FullScreenAdListener mAdListener;
    protected FinishListener mFinishListener;
    protected AdStyle mAdStyle;
    private Bid mBid;

    private Point mAdSize;
    private ActionTrigger mActionTrigger;

    public void setAdStyle(AdStyle adStyle) {
        this.mAdStyle = adStyle;
    }

    public void setBidAndListener(Bid bid, FullScreenAdListener listener) {
        this.mBid = bid;
        this.mAdListener = listener;
        initTrigger();
    }

    public void setFinishListener(FinishListener finishListener) {
        this.mFinishListener = finishListener;
    }

    @Nullable
    public FullScreenAdListener getAdListener() {
        return mAdListener;
    }

    public Bid getBid() {
        return mBid;
    }

    public abstract int getLayoutView();

    public abstract View initView(Context context);

    protected abstract Point resolvedAdSize(int height);

    public abstract void countDownStart(String startTime);

    public abstract void countDownOnTick(String value);

    public abstract void countDownFinish();

    public boolean onBackPressed() {
        return false;
    }

    public abstract void onDestroy();

    protected void setTopMargin(View root) {
        View marginView = root.findViewById(R.id.top_margin);
        if (marginView != null) {
            ViewGroup.LayoutParams lp = marginView.getLayoutParams();
            lp.height = ScreenUtils.getStatusBarHeight(root.getContext());
            marginView.setLayoutParams(lp);
        }
    }

    protected void setRealAdSize(Context context, int height) {
        final Point adSize = resolvedAdSize(height);
        final float scale = (float) adSize.y / (float) adSize.x;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();

        mAdSize = new Point(screenWidth, (int) (screenWidth * scale));
    }

    protected Point getAdSize() {
        if (mAdSize != null)
            return mAdSize;

        return new Point();
    }

    public int getLayoutOrientation(Context context) {
        int resultOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        int currentOrientation;
        currentOrientation = context.getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            resultOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        return resultOrientation;
    }

    private void initTrigger() {
        mActionTrigger = new ActionTrigger(mBid, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (getAdListener() == null)
                    return;

                if (msg.what == Ad.AD_LOAD_CLICK) {
                    getAdListener().onFullScreenAdClicked();
                }
            }
        });
    }

    public void performActionForInternalClick(Context context) {
        if (mActionTrigger != null)
            mActionTrigger.performClick(context.getApplicationContext(), null);
    }

    public void performActionForInternalClick(Context context, String sourceType) {
        if (mActionTrigger != null)
            mActionTrigger.performClick(context.getApplicationContext(), null, sourceType);
    }


    public void performActionForAdClicked(Context context, final String sourceType, final int downloadOptTrig) {
        if (mActionTrigger != null)
            mActionTrigger.performActionForAdClicked(context.getApplicationContext(), sourceType, downloadOptTrig);
    }

    public interface FinishListener {
        void onClick();
    }
}
