package com.afanty.utils;

import android.content.res.Resources;

public class DensityUtils {

    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int dip2px(float dip) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }
}
