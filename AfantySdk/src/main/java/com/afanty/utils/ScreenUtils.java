package com.afanty.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class ScreenUtils {

    private static volatile int sStatusBarHeight = 0;
    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight != 0) {
            return sStatusBarHeight;
        }

        if (context instanceof Activity) {
            Rect rect = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            sStatusBarHeight = rect.top;
            if (sStatusBarHeight != 0) {
                return sStatusBarHeight;
            }
        }
        int resourceId = ContextUtils.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            sStatusBarHeight = ContextUtils.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return sStatusBarHeight;
    }
}
