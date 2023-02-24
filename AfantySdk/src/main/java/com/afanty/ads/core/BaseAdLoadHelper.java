package com.afanty.ads.core;

import android.content.Context;
import android.util.Pair;

import com.afanty.ads.AdError;
import com.afanty.ads.AdStyle;
import com.afanty.ads.AdSize;
import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.ads.base.IAdObserver;
import com.afanty.ads.base.RTBBaseAd;
import com.afanty.ads.base.RTBWrapper;
import com.afanty.common.ClassLoaderManager;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.NetworkUtils;
import com.afanty.utils.Reflector;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseAdLoadHelper {
    private static final String TAG = "RTB.BaseAdLoadHelper";
    public final Context mContext;
    private String mTagId;
    private IAdObserver.AdLoadInnerListener mObserverAdLoadInnerListener;
    private IAdObserver.AdLoadInnerListener mAdLoadInnerListener;
    private AdStyle mAdStyle;
    protected AdSize mAdSize;
    private final AtomicBoolean hasNotifiedObserver = new AtomicBoolean(false);
    private final AtomicBoolean isWaitingToDestroy = new AtomicBoolean(false);

    public BaseAdLoadHelper(Context mContext, String tagId) {
        this.mContext = mContext;
        this.mTagId = tagId;
    }

    public BaseAdLoadHelper setAdListener(IAdObserver.AdLoadInnerListener adLoadInnerListener) {
        this.mObserverAdLoadInnerListener = adLoadInnerListener;
        return this;
    }

    public BaseAdLoadHelper setAdStyle(AdStyle adStyle) {
        this.mAdStyle = adStyle;
        return this;
    }

    public BaseAdLoadHelper setAdSize(AdSize adSize) {
        this.mAdSize = adSize;
        return this;
    }

    private void initListeners() {
        if (mAdLoadInnerListener != null)
            return;
        hasNotifiedObserver.set(false);
        mAdLoadInnerListener = new IAdObserver.AdLoadInnerListener() {
            @Override
            public void onAdLoaded(RTBWrapper adWrapper) {
                notifyObserver(adWrapper, null);
            }

            @Override
            public void onAdLoadError(AdError adError) {
                notifyObserver(null, adError);
            }

            @Override
            public void onAdLoaded(String tagId, RTBWrapper adWrapper) {
                super.onAdLoaded(tagId, adWrapper);
            }

            @Override
            public void onAdLoadError(String tagId, AdError adError) {
                super.onAdLoadError(tagId, adError);
            }

        };
    }

    private void notifyObserver(final RTBWrapper rtbBaseAd, AdError adError) {
        if (mObserverAdLoadInnerListener == null || hasNotifiedObserver.get())
            return;
        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void callBackOnUIThread() {
                if (mObserverAdLoadInnerListener == null || !hasNotifiedObserver.compareAndSet(false, true))
                    return;
                try {
                    if (rtbBaseAd != null)
                        mObserverAdLoadInnerListener.onAdLoaded(rtbBaseAd);
                    else
                        mObserverAdLoadInnerListener.onAdLoadError(adError);
                } catch (Throwable throwable) {
                } finally {
                    if (isWaitingToDestroy.get())
                        onDestroy();
                }
            }
        });
        onDestroy();
    }

    public void doStartLoad() {
        initListeners();
        Pair<Boolean, Boolean> pair = NetworkUtils.checkConnected(ContextUtils.getContext());
        if (!pair.first && !pair.second) {
            onAdLoadError(AdError.NETWORK_ERROR);
            return;
        }
        try {
            _startLoad();
        } catch (AdError e) {
            onAdLoadError(e);
        }
    }

    private void onAdLoadError(AdError adError) {
        if (mAdLoadInnerListener != null) {
            mAdLoadInnerListener.onAdLoadError(adError);
        }
    }

    private void _startLoad() throws AdError {
        final String loaderClassName = ClassLoaderManager.getMediationLoaderClazz(mAdStyle);
        final RTBBaseAd mediationLoader = createMediationLoader(mContext, loaderClassName, mTagId);
        if (mediationLoader == null) {
            return;
        }
        mediationLoader.setAdLoadListener(mAdLoadInnerListener);
        ThreadManager.getInstance().run(new DelayRunnableWork() {
            @Override
            public void execute() {
                mediationLoader.setAdSize(mAdSize);
                mediationLoader.load();
            }
        }, ThreadManager.TYPE_NETWORK_REQUEST);
    }

    protected RTBBaseAd createMediationLoader(Context context, String loaderClassName, String tagId) {
        try {
            return (RTBBaseAd) Reflector.createInstanceOfClassByClazzName(loaderClassName,
                    new Object[]{context, tagId}, Context.class, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void onDestroy() {
        if (!hasNotifiedObserver.get()) {
            isWaitingToDestroy.set(true);
            return;
        }
        mAdLoadInnerListener = null;
        mObserverAdLoadInnerListener = null;
        resetFlags();
    }

    protected void resetFlags() {
        hasNotifiedObserver.set(false);
        isWaitingToDestroy.set(false);
    }

}
