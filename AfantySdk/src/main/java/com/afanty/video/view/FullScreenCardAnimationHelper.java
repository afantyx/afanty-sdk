package com.afanty.video.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

public class FullScreenCardAnimationHelper {
    private ViewGroup mCardLayout;
    private int mStartX, mStartY;
    private int mStopX, mStopY;
    private long mDuration = 1000L;

    public FullScreenCardAnimationHelper setCardLayout(ViewGroup cardLayout) {
        this.mCardLayout = cardLayout;
        return this;
    }

    public FullScreenCardAnimationHelper setStartX(int startX) {
        this.mStartX = startX;
        return this;
    }

    public FullScreenCardAnimationHelper setStartY(int startY) {
        this.mStartY = startY;
        return this;
    }

    public FullScreenCardAnimationHelper setStopX(int stopX) {
        this.mStopX = stopX;
        return this;
    }

    public FullScreenCardAnimationHelper setStopY(int stopY) {
        this.mStopY = stopY;
        return this;
    }

    public FullScreenCardAnimationHelper setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public void startCardInAnimation() {
        if (mCardLayout == null)
            return;

        mCardLayout.setVisibility(View.INVISIBLE);
        mCardLayout.setTranslationY(mStartY);

        ObjectAnimator animator = ObjectAnimator.ofFloat(mCardLayout, "translationY", mStartY, mStopY);

        AnimatorSet cardInAnim = new AnimatorSet();
        cardInAnim.play(animator);
        cardInAnim.setDuration(mDuration);
        cardInAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mCardLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        cardInAnim.setStartDelay(5000);
        cardInAnim.start();
    }
}
