package com.afanty.video.view;

public interface VideoListener {

    void onSurfaceTextureAvailable();

    void onAdRewarded();

    void onFinish();

    void onPerformClick(String sourceType, boolean openOpt, boolean CTAOpt);

    void onComplete();
}