package com.afanty.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

import com.afanty.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ViewUtils {

    public ViewUtils() {
    }
    public static void setFrameLayoutGravity(View view, int gravity) {
        try {
            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) view.getLayoutParams();
            param.gravity = gravity;
            view.setLayoutParams(param);
        } catch (Exception e) {
        }
    }

    public static void setBackgroundKeepPadding(View target, Drawable drawable) {
        if (target != null) {
            int oldPaddingLeft = ViewCompat.getLayoutDirection(target) == ViewCompat.LAYOUT_DIRECTION_RTL ? ViewCompat.getPaddingStart(target) : target.getPaddingLeft();
            int oldPaddingRight = ViewCompat.getLayoutDirection(target) == ViewCompat.LAYOUT_DIRECTION_RTL ? ViewCompat.getPaddingEnd(target) : target.getPaddingRight();
            int oldPaddingTop = target.getPaddingTop();
            int oldPaddingBottom = target.getPaddingBottom();
            ViewCompat.setBackground(target, drawable);
            int newPaddingLeft = ViewCompat.getLayoutDirection(target) == ViewCompat.LAYOUT_DIRECTION_RTL ? ViewCompat.getPaddingStart(target) : target.getPaddingLeft();
            int newPaddingRight = ViewCompat.getLayoutDirection(target) == ViewCompat.LAYOUT_DIRECTION_RTL ? ViewCompat.getPaddingEnd(target) : target.getPaddingRight();
            int newPaddingTop = target.getPaddingTop();
            int newPaddingBottom = target.getPaddingBottom();
            if (newPaddingLeft != oldPaddingLeft || newPaddingTop != oldPaddingTop || newPaddingRight != oldPaddingRight || newPaddingBottom != oldPaddingBottom) {
                target.setPadding(oldPaddingLeft, oldPaddingTop, oldPaddingRight, oldPaddingBottom);
            }
        }
    }

    public static void setBackgroundKeepPadding(View target, @DrawableRes int resId) {
        setBackgroundKeepPadding(target, ContextCompat.getDrawable(ContextUtils.getContext(), resId));
    }

    public static void setTouchDelegate(final View view, final int left, final int top, final int right, final int bottom) {
        if (view != null && view.getParent() instanceof View) {
            final View parent = (View) view.getParent();
            parent.post(new Runnable() {
                public void run() {
                    Rect bounds = new Rect();
                    view.setEnabled(true);
                    view.getHitRect(bounds);
                    bounds.left -= left;
                    bounds.top -= top;
                    bounds.right += right;
                    bounds.bottom += bottom;
                    parent.setTouchDelegate(new TouchDelegate(bounds, view));
                }
            });
        }
    }

    public static GradientDrawable createRectangleDrawable(int contentColor, int strokeColor, int strokeWidth, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setColor(contentColor);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius((float) radius);
        return drawable;
    }

    public static void setTextLeftDrawables(TextView tv, Drawable drawable, int padding) {
        if (tv != null && drawable != null) {
            tv.setCompoundDrawablePadding(padding);
            if (ViewCompat.getLayoutDirection(tv) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tv, drawable, null, null, null);
                return;
            }

            tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (tv != null) {
            tv.setCompoundDrawablePadding(padding);
            if (ViewCompat.getLayoutDirection(tv) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tv, drawable, null, null, null);
                return;
            }

            tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

    }

    public static boolean activityIsDead(Context context) {
        if (context == null) {
            return true;
        }
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    return true;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed()) {
                    return true;
                }
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static boolean isClickTooFrequently(View v) {
        return isClickTooFrequently(v, 1000L);
    }

    public static boolean isClickTooFrequently(View v, long intervalTime) {
        try {
            Object tag = v.getTag(R.id.aft_b_click_frequently_tag);
            long past = tag == null ? 0L : (Long) tag;
            long now = System.currentTimeMillis();
            long interval = now - past;
            if (Math.abs(interval) < intervalTime) {
                return true;
            }

            v.setTag(R.id.aft_b_click_frequently_tag, now);
        } catch (Exception var10) {
        }

        return false;
    }

}
