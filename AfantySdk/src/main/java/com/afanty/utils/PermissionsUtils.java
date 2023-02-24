package com.afanty.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {
    private static final String TAG = "PermissionsUtils";
    public static String[] STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} :
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * 显示权限弹窗
     */
    public static void showPermissionDialog(final Activity activity, String[] permissions, final PermissionsUtils.PermissionRequestCallback requestCallback) {
        PermissionsUtils.requestPermissionsIfNecessaryForResult(activity, permissions, new PermissionsUtils.PermissionRequestCallback() {

            @Override
            public void onGranted() {
                onReentry(activity, true, false, requestCallback);

                if (requestCallback != null) {
                    requestCallback.onGranted();
                }
            }

            @Override
            public void onDenied(@Nullable String[] permissions) {
                if (requestCallback != null) {
                    requestCallback.onDenied(permissions);
                }
            }
        }, 1);
    }

    public static void requestPermissionsIfNecessaryForResult(@Nullable Activity activity, @NonNull String[] permissions, @Nullable PermissionsUtils.PermissionRequestCallback callback, int requestCode) {
        if (isBeforeM()) {
            if (callback != null) {
                callback.onGranted();
            }
        } else {
            if (activity == null) {
                return;
            }

            List<String> permissionsList = new ArrayList();

            for (int i = 0; i < permissions.length; ++i) {
                String permission = permissions[i];
                if (!hasPermission(activity, permission)) {
                    permissionsList.add(permission);
                }
            }

            if (permissionsList.size() > 0) {
                if (activity instanceof PermissionsUtils.IPermissionRequestListener) {
                    ((PermissionsUtils.IPermissionRequestListener) activity).setPermissionRequestListener(callback);
                }

                String[] permsToRequest = permissionsList.toArray(new String[permissionsList.size()]);

                try {
                    ActivityCompat.requestPermissions(activity, permsToRequest, requestCode);
                } catch (ActivityNotFoundException var7) {
                }
            } else if (callback != null) {
                callback.onGranted();
            }
        }

    }

    /**
     * Availability
     */
    public static boolean hasPermission(@Nullable Context context, @NonNull String permission) {
        return isBeforeM() || (context != null && ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Availability of storage rights
     */
    public static boolean hasStoragePermission(Context context) {
        return Build.VERSION.SDK_INT >= 30 ? Environment.isExternalStorageManager()
                : hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private static boolean isBeforeM() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    private static void onReentry(Activity activity, Boolean isFromPermission, Boolean isCheckStoragePermission, final PermissionsUtils.PermissionRequestCallback requestCallback) {
        if (isCheckStoragePermission && !PermissionsUtils.hasStoragePermission(activity)) {
            showPermissionDialog(activity, PermissionsUtils.STORAGE_PERMISSION, requestCallback);
        }
    }

    public interface IPermissionRequestListener {
        void setPermissionRequestListener(PermissionsUtils.PermissionRequestCallback var1);
    }

    public abstract static class PermissionRequestCallback {
        public PermissionRequestCallback() {
        }

        @MainThread
        public abstract void onGranted();

        @MainThread
        public abstract void onDenied(@Nullable String[] var1);
    }

}
