package com.afanty.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class IntentUtils {

    public static boolean startActivityWithTopActivity(@NonNull Context context, @NonNull Intent intent) {
        try {
            if (!(context instanceof Activity))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

}
