package com.afanty.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.NonNull;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.UUID;

public class CommonUtils {

    public static String inputStreamToString(final InputStream is, final boolean sourceIsUTF8) throws IOException {
        InputStreamReader isr = sourceIsUTF8 ? new InputStreamReader(is, Charset.forName("UTF-8")) : new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        return sb.toString().trim();
    }

    /**
     * close cursor, catch and ignore all exceptions.
     *
     * @param cursor the cursor object, may be null
     */
    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * close object quietly, catch and ignore all exceptions.
     *
     * @param object the closeable object like inputstream, outputstream, reader, writer, randomaccessfile.
     */
    public static void close(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Throwable e) {

            }
        }
    }

    @SuppressLint("PrivateApi")
    public static boolean isTranslucentOrFloating(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        boolean isTranslucentOrFloating = false;
        try {
            Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
            Field WindowField = styleableClass.getDeclaredField("Window");
            WindowField.setAccessible(true);
            int[] styleableRes = (int[]) WindowField.get(null);
            final TypedArray typedArray = activity.obtainStyledAttributes(styleableRes);
            Class<?> ActivityInfoClass = ActivityInfo.class;
            Method isTranslucentOrFloatingMethod = ActivityInfoClass.getDeclaredMethod("isTranslucentOrFloating", TypedArray.class);
            isTranslucentOrFloatingMethod.setAccessible(true);
            isTranslucentOrFloating = (boolean) isTranslucentOrFloatingMethod.invoke(null, typedArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    public static int pixelsToIntDips(final float pixels, final Context context) {
        return (int) (pixelsToFloatDips(pixels, context) + 0.5f);
    }

    private static float pixelsToFloatDips(final float pixels, final Context context) {
        return pixels / getDensity(context);
    }

    private static float getDensity(final Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String createUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes)
            builder.append(toHex(b));
        return builder.toString();
    }

    private static String toHex(byte value) {
        int unsignedInt = value < 0 ? 256 + value : value;
        return leftPad(Integer.toHexString(unsignedInt), Byte.SIZE / 4, '0');
    }

    private static String leftPad(String str, int len, char ch) {
        StringBuilder builder = new StringBuilder();
        int start = str == null ? 0 : str.length();
        for (int i = start; i < len; i++)
            builder.append(ch);
        if (str != null)
            builder.append(str);
        return builder.toString();
    }

    public static void setAdaptationRequestedOrientation(Activity activity, int orientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && CommonUtils.isTranslucentOrFloating(activity)) {
            try {
                Class<Activity> activityClass = Activity.class;
                Field mActivityInfoField = activityClass.getDeclaredField("mActivityInfo");
                mActivityInfoField.setAccessible(true);
                ActivityInfo activityInfo = (ActivityInfo) mActivityInfoField.get(activity);
                if (activityInfo != null)
                    activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            activity.setRequestedOrientation(orientation);
        }
    }

    public static int getScreenOrientation(@NonNull final Activity activity) {
        final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        final int deviceOrientation = activity.getResources().getConfiguration().orientation;
        return getScreenOrientationFromRotationAndOrientation(rotation, deviceOrientation);
    }

    private static int getScreenOrientationFromRotationAndOrientation(int rotation, int orientation) {
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            switch (rotation) {
                case Surface.ROTATION_90:
                case Surface.ROTATION_180:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                case Surface.ROTATION_0:
                case Surface.ROTATION_270:
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        } else if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            switch (rotation) {
                case Surface.ROTATION_180:
                case Surface.ROTATION_270:
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                case Surface.ROTATION_0:
                case Surface.ROTATION_90:
                default:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        } else {
            return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
    }

    public static boolean bitMaskContainsFlag(final int bitMask, final int flag) {
        return (bitMask & flag) != 0;
    }


    public static String getWebViewUA() {
        String userAgent = (String) ContextUtils.get("ua");
        if (!TextUtils.isEmpty(userAgent))
            return userAgent;

        userAgent = SettingConfig.getWebUA();
        if (!TextUtils.isEmpty(userAgent)) {
            ContextUtils.add("ua", userAgent);
        }
        return userAgent;
    }

    public static void disableAccessibility(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (!am.isEnabled()) {
                    return;
                }
                Method setState = am.getClass().getDeclaredMethod("setState", int.class);
                setState.setAccessible(true);
                setState.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
            } catch (Throwable t) {

            }
        }
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static DeviceUtils.DEVICETYPE detectDeviceType(Context ctx) {
        double screenSize = 0D;
        try {
            DisplayMetrics displaymetrics = ctx.getApplicationContext().getResources().getDisplayMetrics();
            float width = displaymetrics.widthPixels;
            float height = displaymetrics.heightPixels;
            float xdpi = displaymetrics.densityDpi > displaymetrics.xdpi ? displaymetrics.densityDpi : displaymetrics.xdpi;
            float ydpi = displaymetrics.densityDpi > displaymetrics.ydpi ? displaymetrics.densityDpi : displaymetrics.ydpi;
            float inchW = width / xdpi;
            float inchH = height / ydpi;
            screenSize = Math.sqrt(Math.pow(inchW, 2D) + Math.pow(inchH, 2D));
        } catch (Exception exception) {
            return DeviceUtils.DEVICETYPE.DEVICE_PHONE;
        }
        if (screenSize >= 6.5D)
            return DeviceUtils.DEVICETYPE.DEVICE_PAD;
        return DeviceUtils.DEVICETYPE.DEVICE_PHONE;
    }


    public static String getInstallPackage(Context context, String packageName) {
        try {
            String pkgName = context.getPackageManager().getInstallerPackageName(packageName);
            return pkgName == null ? "unknown" : pkgName;
        } catch (Exception e) {
        }
        return "unknown";
    }
}
