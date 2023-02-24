package com.afanty.internal.nativead;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.base.RTBWrapper;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.INativeAd;
import com.afanty.ads.base.RTBBaseAd;
import com.afanty.ads.view.PlayerView;
import com.afanty.internal.Ad;
import com.afanty.internal.action.ActionTrigger;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.action.ImpressionInterface;
import com.afanty.internal.action.ImpressionTracker;
import com.afanty.internal.config.AftConfig;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.internal.CreativeType;
import com.afanty.internal.view.CustomProgressBar;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.ContextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class InnerNativeAd extends RTBBaseAd implements INativeAd {
    private static final String TAG = "RTB.InnerNativeAd";
    private NativeAdLoader mNativeAdLoader;
    private Bid mBid = null;
    private static final WeakHashMap<View, WeakReference<InnerNativeAd>> sViewBasedAdCache = new WeakHashMap<>();
    private final List<View> mBoundViews = new ArrayList<>();
    private View mBoundView;
    private ProxyClickListener mViewListener;
    private ImpressionTracker mImpressionTracker;
    private ActionTrigger mActionTrigger;
    private boolean mImpressionRecorded;
    private Handler mHandler;
    private PlayerView mMediaView;

    public InnerNativeAd(Context context, String tagId) {
        super(context, tagId);
    }

    @Override
    protected void innerLoad() {
        mNativeAdLoader = new NativeAdLoader(mContext, mTagId);
        mNativeAdLoader.setAdListener(new AdLoadListener() {
            @Override
            public void onDataLoaded(Bid bid) {
                if (bid != null) {
                    mBid = bid;
                }
                mActionTrigger = new ActionTrigger(mBid, initHandler());
                onAdLoaded(new RTBWrapper(getTagId(), InnerNativeAd.this));
            }

            @Override
            public void onDataError(AdError error) {
                onAdLoadError(error);
            }
        });
        mNativeAdLoader.loadAd();
    }

    public boolean isVideoAd() {
        return hasBid() ? CreativeType.isVideo(mBid) : false;
    }

    @Override
    public AdStyle getAdStyle() {
        return AdStyle.NATIVE;
    }

    @Override
    @Nullable
    public String getTitle() {
        return hasBid() ? mBid.getTitle() : null;
    }

    @Override
    @Nullable
    public String getContent() {
        return hasBid() ? mBid.getDataByType(AdmBean.TEXT_DESC) : null;
    }

    @Override
    @Nullable
    public String getIconUrl() {
        return hasBid() ? mBid.getUrlByType(AdmBean.IMG_ICON_TYPE) : null;
    }

    @Override
    public RTBBaseAd getNativeAd() {
        return this;
    }

    @Override
    @Nullable
    public String getPosterUrl() {
        return hasBid() ? mBid.getUrlByType(AdmBean.IMG_POSTER_TYPE) : null;
    }

    @Override
    @Nullable
    public String getCallToAction() {
        return hasBid() ? mBid.getDataByType(AdmBean.TEXT_BTN) : null;
    }

    @Override
    @Nullable
    public View getAdIconView() {
        return null;
    }

    @Override
    @Nullable
    public View getAdMediaView(Object... object) {
        try {
            mMediaView = new PlayerView(ContextUtils.getContext());
            mMediaView.loadMediaView(this);
        } catch (Exception e) {
        }
        return mMediaView;
    }

    @Override
    @Nullable
    public ViewGroup getCustomAdContainer() {
        return null;
    }

    @Override
    public void prepare(View view, FrameLayout.LayoutParams adOptionParams) {
        prepare(view, null, adOptionParams);
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams adOptionParams) {
        if (clickViewList == null || clickViewList.isEmpty()) {
            registerViewForInteraction(view);
        } else {
            registerViewForInteraction(view, clickViewList);
        }
    }

    @Override
    public void destroy() {
        if (mImpressionTracker != null)
            mImpressionTracker.destroy();
        if (mNativeAdLoader != null)
            mNativeAdLoader.destroy();
    }

    @Override
    public boolean isAdReady() {
        return false;
    }

    /**
     * Custom AdData uses sub-methods
     *
     * @param view The view to be registered as a listener
     */
    public void registerViewForInteraction(View view) {
        List<View> allViews = new ArrayList<>();
        collectChildView(allViews, view);
        registerViewForInteraction(view, allViews);
    }

    private void collectChildView(List<View> allViews, View parent) {
        allViews.add(parent);
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                collectChildView(allViews, viewGroup.getChildAt(i));
        }
    }

    public void registerViewForInteraction(View view, List<View> list) {
        if (creativeOMSession(view, list))
            registerViewAndAddClick(view, list);
    }

    private void registerViewAndAddClick(View view, List<View> list) {
        this.mImpressionTracker = new ImpressionTracker(view.getContext());
        this.mImpressionTracker.addView(view, new ProxyImpressionInterface());
        this.mViewListener = new ProxyClickListener();
        this.mBoundView = view;

        for (View v : list)
            bindClickEvent(v);

        sViewBasedAdCache.put(view, new WeakReference<>(this));
    }

    private void bindClickEvent(View view) {
        if (view == null)
            return;
        this.mBoundViews.add(view);
        view.setOnClickListener(this.mViewListener);
    }

    private boolean creativeOMSession(View view, List<View> list) {
        if (view == null)
            throw new IllegalArgumentException("Must provide a View");

        if (list == null || list.isEmpty())
            throw new IllegalArgumentException("Invalid set of clickable views");

        if (getBid() == null) {
            return false;
        }

        if (this.mBoundView != null) {
            unregisterView();
        }

        if (sViewBasedAdCache.containsKey(view)) {
            InnerNativeAd nativeAd = (InnerNativeAd) ((WeakReference) (sViewBasedAdCache.get(view))).get();
            if (nativeAd != null)
                nativeAd.unregisterView();
        }
        return true;
    }

    private void unregisterView() {
        if (this.mBoundView != null && sViewBasedAdCache.containsKey(this.mBoundView) && ((WeakReference) sViewBasedAdCache.get(this.mBoundView)).get() == this) {
            sViewBasedAdCache.remove(this.mBoundView);
            if (this.mImpressionTracker != null)
                this.mImpressionTracker.removeView(mBoundView);
            unbindClickEvent();
            this.mBoundView = null;
        }

    }

    private void unbindClickEvent() {
        for (View view : mBoundViews)
            view.setOnClickListener(null);

        this.mBoundViews.clear();
        this.mViewListener = null;
    }

    private Handler initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Ad.AD_LOAD_SHOW:
                        notifyAdAction(IAdObserver.AdEvent.AD_ACTION_IMPRESSION);
                        break;
                    case Ad.AD_LOAD_CLICK:
                        notifyAdAction(IAdObserver.AdEvent.AD_ACTION_CLICKED);
                        break;
                }
            }
        };
        return mHandler;
    }

    private boolean hasBid() {
        return mBid != null;
    }

    public Bid getBid() {
        return mBid;
    }


    private class ProxyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            if (mActionTrigger != null) {
                if (buttonPerformClick(v))
                    return;

                mActionTrigger.performClick(v.getContext(), rect);
            }
        }

        private boolean buttonPerformClick(View view) {
            if (hasBid()) {
                String btnText = mBid.getDataByType(AdmBean.TEXT_BTN);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    if (TextUtils.equals(textView.getText(), btnText)) {
                        mActionTrigger.performActionForAdClicked(view.getContext(), AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, -1);
                        return true;
                    }
                }
                if (view instanceof CustomProgressBar) {
                    CustomProgressBar textProgress = (CustomProgressBar) view;
                    if (TextUtils.equals(textProgress.getText(), btnText)) {
                        textProgress.registerClick(mBid, new CustomProgressBar.RegisterTextProgressListener() {
                            @Override
                            public void onNormal(boolean openOpt, boolean CTAOpt) {
                                mActionTrigger.performActionForAdClicked(view.getContext(), AdConstants.Vast.SOURCE_TYPE_CARDBUTTON, ActionUtils.getDownloadOptTrig(openOpt, CTAOpt));
                            }
                        });
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private class ProxyImpressionInterface implements ImpressionInterface {
        @Override
        public int getImpressionMinPercentageViewed() {
            return AftConfig.getImpressionMinPercentageViewed();
        }

        @Override
        public Integer getImpressionMinVisiblePx() {
            return AftConfig.getImpressionMinVisiblePx();
        }

        @Override
        public int getImpressionMinTimeViewed() {
            return AftConfig.getImpressionMinTimeViewed();
        }

        @Override
        public void recordImpression(View view) {
            fireImpression();
        }

        @Override
        public boolean isImpressionRecorded() {
            return mImpressionRecorded;
        }

        @Override
        public void setImpressionRecorded() {
            mImpressionRecorded = true;
        }
    }


    private void fireImpression() {
        ActionUtils.increaseShowCount(mBid);
        if (mBid != null && mBid.isEffectiveShow())
            mHandler.sendMessage(mHandler.obtainMessage(Ad.AD_LOAD_SHOW));
    }


}
