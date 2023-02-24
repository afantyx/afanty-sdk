package com.afanty.internal.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.afanty.vast.utils.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class VisibilityTracker {
    private static final String TAG = "VisibilityTracker";
    private static final int VISIBILITY_THROTTLE_MILLIS = 100;

    @VisibleForTesting
    static final int NUM_ACCESSES_BEFORE_TRIMMING = 50;

    @NonNull
    private final ArrayList<View> mTrimmedViews;

    private long mAccessCounter = 0;

    public interface VisibilityTrackerListener {
        void onVisibilityChanged(List<View> visibleViews, List<View> invisibleViews);
    }

    @NonNull
    @VisibleForTesting
    final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    @NonNull
    @VisibleForTesting
    WeakReference<ViewTreeObserver> mWeakViewTreeObserver;

    static class TrackingInfo {
        int mMinViewablePercent;
        int mMaxInvisiblePercent;
        long mAccessOrder;
        View mRootView;

        /**
         * If this number is set, then use this as the minimum amount of the view seen before it is
         * considered visible. This is in real pixels.
         */
        @Nullable
        Integer mMinVisiblePx;
    }

    @NonNull
    private final Map<View, TrackingInfo> mTrackedViews;

    @NonNull
    private final VisibilityChecker mVisibilityChecker;

    @Nullable
    private VisibilityTrackerListener mVisibilityTrackerListener;

    @NonNull
    private final VisibilityRunnable mVisibilityRunnable;

    @NonNull
    private final Handler mVisibilityHandler;

    private boolean mIsVisibilityScheduled;

    public VisibilityTracker(@NonNull final Context context) {
        this(context,
                new WeakHashMap<View, TrackingInfo>(10),
                new VisibilityChecker(),
                new Handler());
    }

    @VisibleForTesting
    VisibilityTracker(@NonNull final Context context,
                      @NonNull final Map<View, TrackingInfo> trackedViews,
                      @NonNull final VisibilityChecker visibilityChecker,
                      @NonNull final Handler visibilityHandler) {
        mTrackedViews = trackedViews;
        mVisibilityChecker = visibilityChecker;
        mVisibilityHandler = visibilityHandler;
        mVisibilityRunnable = new VisibilityRunnable();
        mTrimmedViews = new ArrayList<>(NUM_ACCESSES_BEFORE_TRIMMING);

        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scheduleVisibilityCheck();
                return true;
            }
        };

        mWeakViewTreeObserver = new WeakReference<>(null);
        setViewTreeObserver(context, null);
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

    public void setVisibilityTrackerListener(
            @Nullable final VisibilityTrackerListener visibilityTrackerListener) {
        mVisibilityTrackerListener = visibilityTrackerListener;
    }

    public void addView(@NonNull final View view, final int minPercentageViewed,
                        @Nullable final Integer minVisiblePx) {
        addView(view, view, minPercentageViewed, minVisiblePx);
    }

    public void addView(@NonNull View rootView, @NonNull final View view, final int minPercentageViewed,
                        @Nullable final Integer minVisiblePx) {
        addView(rootView, view, minPercentageViewed, minPercentageViewed, minVisiblePx);
    }

    public void addView(@NonNull View rootView, @NonNull final View view,
                        final int minVisiblePercentageViewed, final int maxInvisiblePercentageViewed,
                        @Nullable final Integer minVisiblePx) {
        setViewTreeObserver(view.getContext(), view);

        TrackingInfo trackingInfo = mTrackedViews.get(view);
        if (trackingInfo == null) {
            trackingInfo = new TrackingInfo();
            mTrackedViews.put(view, trackingInfo);
            scheduleVisibilityCheck();
        }

        int maxInvisiblePercent = Math.min(maxInvisiblePercentageViewed, minVisiblePercentageViewed);

        trackingInfo.mRootView = rootView;
        trackingInfo.mMinViewablePercent = minVisiblePercentageViewed;
        trackingInfo.mMaxInvisiblePercent = maxInvisiblePercent;
        trackingInfo.mAccessOrder = mAccessCounter;
        trackingInfo.mMinVisiblePx = minVisiblePx;

        mAccessCounter++;
        if (mAccessCounter % NUM_ACCESSES_BEFORE_TRIMMING == 0) {
            trimTrackedViews(mAccessCounter - NUM_ACCESSES_BEFORE_TRIMMING);
        }
    }

    private void trimTrackedViews(long minAccessOrder) {
        for (final Map.Entry<View, TrackingInfo> entry : mTrackedViews.entrySet()) {
            if (entry.getValue().mAccessOrder < minAccessOrder) {
                mTrimmedViews.add(entry.getKey());
            }
        }

        for (View view : mTrimmedViews) {
            removeView(view);
        }
        mTrimmedViews.clear();
    }

    public void removeView(@NonNull final View view) {
        mTrackedViews.remove(view);
    }

    public void clear() {
        mTrackedViews.clear();
        mVisibilityHandler.removeMessages(0);
        mIsVisibilityScheduled = false;
    }

    public void destroy() {
        clear();
        final ViewTreeObserver viewTreeObserver = mWeakViewTreeObserver.get();
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener);
        }
        mWeakViewTreeObserver.clear();
        mVisibilityTrackerListener = null;
    }

    public void scheduleVisibilityCheck() {
        if (mIsVisibilityScheduled) {
            return;
        }

        mIsVisibilityScheduled = true;
        mVisibilityHandler.postDelayed(mVisibilityRunnable, VISIBILITY_THROTTLE_MILLIS);
    }

    class VisibilityRunnable implements Runnable {
        @NonNull
        private final ArrayList<View> mVisibleViews;
        @NonNull
        private final ArrayList<View> mInvisibleViews;

        VisibilityRunnable() {
            mInvisibleViews = new ArrayList<>();
            mVisibleViews = new ArrayList<>();
        }

        @Override
        public void run() {
            mIsVisibilityScheduled = false;
            for (final Map.Entry<View, TrackingInfo> entry : mTrackedViews.entrySet()) {
                final View view = entry.getKey();
                final int minPercentageViewed = entry.getValue().mMinViewablePercent;
                final int maxInvisiblePercent = entry.getValue().mMaxInvisiblePercent;
                final Integer minVisiblePx = entry.getValue().mMinVisiblePx;
                final View rootView = entry.getValue().mRootView;

                if (mVisibilityChecker.isVisible(rootView, view, minPercentageViewed, minVisiblePx)) {
                    mVisibleViews.add(view);
                } else if (!mVisibilityChecker.isVisible(rootView, view, maxInvisiblePercent, null)) {
                    mInvisibleViews.add(view);
                }
            }

            if (mVisibilityTrackerListener != null) {
                mVisibilityTrackerListener.onVisibilityChanged(mVisibleViews, mInvisibleViews);
            }

            mVisibleViews.clear();
            mInvisibleViews.clear();
        }
    }

    public static class VisibilityChecker {
        private final Rect mClipRect = new Rect();

        public boolean hasRequiredTimeElapsed(final long startTimeMillis, final int minTimeViewed) {
            return SystemClock.uptimeMillis() - startTimeMillis >= minTimeViewed;
        }

        public boolean isVisible(@Nullable final View rootView, @Nullable final View view,
                                 final int minPercentageViewed, @Nullable final Integer minVisiblePx) {

            if (view == null || view.getVisibility() != View.VISIBLE || rootView == null || rootView.getParent() == null || !view.isShown()) {
                return false;
            }

            if (!view.getGlobalVisibleRect(mClipRect)) {
                return false;
            }

            final long visibleViewArea = (long) mClipRect.height() * mClipRect.width();
            final long totalViewArea = (long) view.getHeight() * view.getWidth();

            if (totalViewArea <= 0) {
                return false;
            }

            if (minVisiblePx != null && minVisiblePx > 0) {
                return visibleViewArea >= minVisiblePx;
            }

            return 100 * visibleViewArea >= minPercentageViewed * totalViewArea;
        }
    }

    private static class Views {
        public static void removeFromParent(@Nullable View view) {
            if (view == null || view.getParent() == null) {
                return;
            }

            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }

        @Nullable
        public static View getTopmostView(@Nullable final Context context, @Nullable final View view) {
            final View rootViewFromActivity = getRootViewFromActivity(context);
            final View rootViewFromView = getRootViewFromView(view);
            return rootViewFromActivity != null
                    ? rootViewFromActivity
                    : rootViewFromView;
        }

        @Nullable
        private static View getRootViewFromActivity(@Nullable final Context context) {
            if (!(context instanceof Activity)) {
                return null;
            }

            return ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        }

        @Nullable
        private static View getRootViewFromView(@Nullable final View view) {
            if (view == null) {
                return null;
            }

            if (!ViewCompat.isAttachedToWindow(view)) {
            }

            final View rootView = view.getRootView();

            if (rootView == null) {
                return null;
            }

            final View rootContentView = rootView.findViewById(android.R.id.content);
            return rootContentView != null
                    ? rootContentView
                    : rootView;
        }
    }

}

