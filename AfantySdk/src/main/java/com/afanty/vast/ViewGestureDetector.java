package com.afanty.vast;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;


public class ViewGestureDetector extends GestureDetector {
    @NonNull
    private GestureListener mGestureListener;

    public ViewGestureDetector(@NonNull Context context) {
        this(context, new GestureListener());
    }

    private ViewGestureDetector(Context context, @NonNull GestureListener gestureListener) {
        super(context, gestureListener);

        mGestureListener = gestureListener;

        setIsLongpressEnabled(false);
    }

    void onResetUserClick() {
        mGestureListener.onResetUserClick();
    }

    public boolean isClicked() {
        return mGestureListener.isClicked();
    }

    @Deprecated
        // for testing
    void setGestureListener(@NonNull GestureListener gestureListener) {
        mGestureListener = gestureListener;
    }

    public void setClicked(boolean clicked) {
        mGestureListener.mIsClicked = clicked;
    }

    /**
     * Track user interaction in a separate class
     */
    static class GestureListener extends SimpleOnGestureListener {
        boolean mIsClicked = false;

        void onResetUserClick() {
            mIsClicked = false;
        }

        boolean isClicked() {
            return mIsClicked;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mIsClicked = true;
            return super.onSingleTapUp(e);
        }
    }
}
