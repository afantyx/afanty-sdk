package com.afanty.ads.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

public interface INativeAd {
    RTBBaseAd getNativeAd();

    String getPosterUrl();

    String getIconUrl();

    String getTitle();

    String getContent();

    String getCallToAction();

    View getAdIconView();

    View getAdMediaView(Object... object);

    ViewGroup getCustomAdContainer();

    void prepare(final View view, final FrameLayout.LayoutParams adOptionParams);

    void prepare(final View view, List<View> clickViewList, final FrameLayout.LayoutParams adOptionParams);

    void destroy();
}
