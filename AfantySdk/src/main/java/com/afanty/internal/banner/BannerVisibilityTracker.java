package com.afanty.internal.banner;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.utils.CommonUtils;
import com.afanty.vast.utils.Views;

import java.lang.ref.WeakReference;

import static android.view.ViewTreeObserver.OnPreDrawListener;

public class BannerVisibilityTracker {

    private static final String TAG = "Banner.VisibilityTracker";
    private static final int VISIBILITY_THROTTLE_MILLIS = 100;
    @NonNull
    final OnPreDrawListener mOnPreDrawListener;
    /**
     * Banner view that is being tracked.
     */
    @NonNull
    private final View mTrackedView;
    /**
     * Root view of banner view being tracked.
     */
    @NonNull
    private final View mRootView;
    /**
     * Object to check actual visibility.
     */
    @NonNull
    private final BannerVisibilityChecker mVisibilityChecker;
    /**
     * Runnable to run on each visibility loop.
     */
    @NonNull
    private final BannerVisibilityRunnable mVisibilityRunnable;
    /**
     * Handler for visibility.
     */
    @NonNull
    private final Handler mVisibilityHandler;
    @NonNull
    WeakReference<ViewTreeObserver> mWeakViewTreeObserver;
    /**
     * Callback listener.
     */
    @Nullable
    private BannerVisibilityTrackerListener mBannerVisibilityTrackerListener;
    /**
     * Whether the visibility runnable is scheduled.
     */
    private boolean mIsVisibilityScheduled;
    /**
     * Whether the imp tracker has been fired already.
     */
    private boolean mIsImpTrackerFired;

    public BannerVisibilityTracker(@NonNull final Context context,
                                   @NonNull final View rootView,
                                   @NonNull final View trackedView,
                                   final int minVisibleDips,
                                   final int minVisibleMillis) {
        mRootView = rootView;
        mTrackedView = trackedView;

        mVisibilityChecker = new BannerVisibilityChecker(minVisibleDips, minVisibleMillis);
        mVisibilityHandler = new Handler();
        mVisibilityRunnable = new BannerVisibilityRunnable();

        mOnPreDrawListener = new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scheduleVisibilityCheck();
                return true;
            }
        };

        mWeakViewTreeObserver = new WeakReference<>(null);
        setViewTreeObserver(context, mTrackedView);
    }

    private void setViewTreeObserver(@Nullable final Context context, @Nullable final View view) {
        final ViewTreeObserver originalViewTreeObserver = mWeakViewTreeObserver.get();
        if (originalViewTreeObserver != null && originalViewTreeObserver.isAlive()) {
            return;
        }

        final View rootView = Views.getTopmostView(context, view);
        if (rootView == null) {
            return;
        }

        final ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        if (!viewTreeObserver.isAlive()) {
            return;
        }

        mWeakViewTreeObserver = new WeakReference<>(viewTreeObserver);
        viewTreeObserver.addOnPreDrawListener(mOnPreDrawListener);
    }

    @Nullable
    @Deprecated
    BannerVisibilityTrackerListener getBannerVisibilityTrackerListener() {
        return mBannerVisibilityTrackerListener;
    }

    public void setBannerVisibilityTrackerListener(
            @Nullable final BannerVisibilityTrackerListener bannerVisibilityTrackerListener) {
        mBannerVisibilityTrackerListener = bannerVisibilityTrackerListener;
    }

    /**
     * Destroy the visibility tracker, preventing it from future use.
     */
    void destroy() {
        mVisibilityHandler.removeMessages(0);
        mIsVisibilityScheduled = false;
        final ViewTreeObserver viewTreeObserver = mWeakViewTreeObserver.get();
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener);
        }
        mWeakViewTreeObserver.clear();
        mBannerVisibilityTrackerListener = null;
    }

    void scheduleVisibilityCheck() {
        if (mIsVisibilityScheduled) {
            return;
        }

        mIsVisibilityScheduled = true;
        mVisibilityHandler.postDelayed(mVisibilityRunnable, VISIBILITY_THROTTLE_MILLIS);
    }

    @NonNull
    @Deprecated
    BannerVisibilityChecker getBannerVisibilityChecker() {
        return mVisibilityChecker;
    }

    @NonNull
    @Deprecated
    Handler getVisibilityHandler() {
        return mVisibilityHandler;
    }

    @Deprecated
    boolean isVisibilityScheduled() {
        return mIsVisibilityScheduled;
    }

    @Deprecated
    boolean isImpTrackerFired() {
        return mIsImpTrackerFired;
    }

    /**
     * Callback when visibility conditions are satisfied.
     */
    public interface BannerVisibilityTrackerListener {
        void onVisibilityChanged();
    }

    static class BannerVisibilityChecker {
        private final Rect mClipRect = new Rect();
        private final int mMinVisibleDips;
        private final int mMinVisibleMillis;
        private long mStartTimeMillis = Long.MIN_VALUE;

        BannerVisibilityChecker(final int minVisibleDips, final int minVisibleMillis) {
            mMinVisibleDips = minVisibleDips;
            mMinVisibleMillis = minVisibleMillis;
        }

        boolean hasBeenVisibleYet() {
            return mStartTimeMillis != Long.MIN_VALUE;
        }

        void setStartTimeMillis() {
            mStartTimeMillis = SystemClock.uptimeMillis();
        }

        /**
         * Whether the visible time has elapsed from the start time.
         */
        boolean hasRequiredTimeElapsed() {
            if (!hasBeenVisibleYet()) {
                return false;
            }

            return SystemClock.uptimeMillis() - mStartTimeMillis >= mMinVisibleMillis;
        }

        /**
         * Whether the visible dips count requirement is met.
         */
        boolean isVisible(@Nullable final View rootView, @Nullable final View view) {
            if (view == null || view.getVisibility() != View.VISIBLE || rootView.getParent() == null) {
                return false;
            }

            if (view.getWidth() <= 0 || view.getHeight() <= 0) {
                return false;
            }

            if (!view.getGlobalVisibleRect(mClipRect)) {
                return false;
            }

            final int widthInDips = CommonUtils.pixelsToIntDips((float) mClipRect.width(),
                    view.getContext());
            final int heightInDips = CommonUtils.pixelsToIntDips((float) mClipRect.height(),
                    view.getContext());
            final long visibleViewAreaInDips = widthInDips * heightInDips;

            return visibleViewAreaInDips >= mMinVisibleDips;
        }
    }

    class BannerVisibilityRunnable implements Runnable {
        @Override
        public void run() {
            if (mIsImpTrackerFired) {
                return;
            }

            mIsVisibilityScheduled = false;

            if (mVisibilityChecker.isVisible(mRootView, mTrackedView)) {
                if (!mVisibilityChecker.hasBeenVisibleYet()) {
                    mVisibilityChecker.setStartTimeMillis();
                }

                if (mVisibilityChecker.hasRequiredTimeElapsed()) {
                    if (mBannerVisibilityTrackerListener != null) {
                        mBannerVisibilityTrackerListener.onVisibilityChanged();
                        mIsImpTrackerFired = true;
                    }
                }
            }

            if (!mIsImpTrackerFired) {
                scheduleVisibilityCheck();
            }
        }
    }
}
