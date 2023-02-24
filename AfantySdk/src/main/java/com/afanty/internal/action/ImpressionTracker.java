package com.afanty.internal.action;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.internal.common.VisibilityTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Impression tracker used to call {@link ImpressionInterface#recordImpression(View)} when a
 * percentage of a native ad has been on screen for a duration of time.
 */
public class ImpressionTracker {
    private static final int PERIOD = 250;

    @NonNull
    private final VisibilityTracker mVisibilityTracker;

    @NonNull
    private final Map<View, ImpressionInterface> mTrackedViews;

    @NonNull
    private final Map<View, TimestampWrapper<ImpressionInterface>> mPollingViews;

    @NonNull
    private final Handler mPollHandler;

    @NonNull
    private final PollingRunnable mPollingRunnable;

    @NonNull
    private final VisibilityTracker.VisibilityChecker mVisibilityChecker;

    @Nullable
    private VisibilityTracker.VisibilityTrackerListener mVisibilityTrackerListener;

    public ImpressionTracker(@NonNull final Context context) {
        this(new WeakHashMap<View, ImpressionInterface>(),
                new WeakHashMap<View, TimestampWrapper<ImpressionInterface>>(),
                new VisibilityTracker.VisibilityChecker(),
                new VisibilityTracker(context),
                new Handler(Looper.getMainLooper()));
    }

    ImpressionTracker(@NonNull final Map<View, ImpressionInterface> trackedViews,
                      @NonNull final Map<View, TimestampWrapper<ImpressionInterface>> pollingViews,
                      @NonNull final VisibilityTracker.VisibilityChecker visibilityChecker,
                      @NonNull final VisibilityTracker visibilityTracker,
                      @NonNull final Handler handler) {
        mTrackedViews = trackedViews;
        mPollingViews = pollingViews;
        mVisibilityChecker = visibilityChecker;
        mVisibilityTracker = visibilityTracker;

        mVisibilityTrackerListener = new VisibilityTracker.VisibilityTrackerListener() {
            @Override
            public void onVisibilityChanged(@NonNull final List<View> visibleViews, @NonNull final List<View> invisibleViews) {
                for (final View view : visibleViews) {
                    final ImpressionInterface impressionInterface = mTrackedViews.get(view);
                    if (impressionInterface == null) {
                        removeView(view);
                        continue;
                    }
                    final TimestampWrapper<ImpressionInterface> polling = mPollingViews.get(view);
                    if (polling != null && impressionInterface.equals(polling.mInstance)) {
                        continue;
                    }
                    mPollingViews.put(view, new TimestampWrapper<>(impressionInterface));
                }

                for (final View view : invisibleViews) {
                    mPollingViews.remove(view);
                }
                scheduleNextPoll();
            }
        };
        mVisibilityTracker.setVisibilityTrackerListener(mVisibilityTrackerListener);

        mPollHandler = handler;
        mPollingRunnable = new PollingRunnable();
    }

    public void addView(final View view, @NonNull final ImpressionInterface impressionInterface) {
        if (mTrackedViews.get(view) == impressionInterface) {
            return;
        }

        removeView(view);

        if (impressionInterface.isImpressionRecorded()) {
            return;
        }

        mTrackedViews.put(view, impressionInterface);
        mVisibilityTracker.addView(view, impressionInterface.getImpressionMinPercentageViewed(),
                impressionInterface.getImpressionMinVisiblePx());
    }

    public void removeView(final View view) {
        mTrackedViews.remove(view);
        removePollingView(view);
        mVisibilityTracker.removeView(view);
    }

    /**
     * Immediately clear all views. Useful for when we re-request ads for an ad placer
     */
    public void clear() {
        mTrackedViews.clear();
        mPollingViews.clear();
        mVisibilityTracker.clear();
        mPollHandler.removeMessages(0);
    }

    public void destroy() {
        clear();
        mVisibilityTracker.destroy();
        mVisibilityTrackerListener = null;
    }

    void scheduleNextPoll() {
        // Only schedule if there are no messages already scheduled.
        if (mPollHandler.hasMessages(0)) {
            return;
        }

        mPollHandler.postDelayed(mPollingRunnable, PERIOD);
    }

    private void removePollingView(final View view) {
        mPollingViews.remove(view);
    }

    @Nullable
    @Deprecated
    VisibilityTracker.VisibilityTrackerListener getVisibilityTrackerListener() {
        return mVisibilityTrackerListener;
    }

    class PollingRunnable implements Runnable {
        @NonNull
        private final ArrayList<View> mRemovedViews;

        PollingRunnable() {
            mRemovedViews = new ArrayList<>();
        }

        @Override
        public void run() {
            for (final Map.Entry<View, TimestampWrapper<ImpressionInterface>> entry : mPollingViews.entrySet()) {
                final View view = entry.getKey();
                final TimestampWrapper<ImpressionInterface> timestampWrapper = entry.getValue();

                if (!mVisibilityChecker.hasRequiredTimeElapsed(
                        timestampWrapper.mCreatedTimestamp,
                        timestampWrapper.mInstance.getImpressionMinTimeViewed())) {
                    continue;
                }

                timestampWrapper.mInstance.recordImpression(view);
                timestampWrapper.mInstance.setImpressionRecorded();

                mRemovedViews.add(view);
            }

            for (View view : mRemovedViews) {
                removeView(view);
            }
            mRemovedViews.clear();

            if (!mPollingViews.isEmpty()) {
                scheduleNextPoll();
            }
        }
    }
}
