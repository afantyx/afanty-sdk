package com.afanty.internal.action;

import android.view.View;

public interface ImpressionInterface {
    int getImpressionMinPercentageViewed();

    Integer getImpressionMinVisiblePx();

    int getImpressionMinTimeViewed();

    void recordImpression(View view);

    boolean isImpressionRecorded();

    void setImpressionRecorded();
}