package com.afanty.ads.core;

import android.content.Context;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdLoadHelperStorage {
    private static final Map<String, WeakReference<BaseAdLoadHelper>> mCachedAdLoadHelper = new ConcurrentHashMap<>();

    public static BaseAdLoadHelper getAdLoadHelper(Context context, String tagId) {
        if (mCachedAdLoadHelper.containsKey(tagId) && mCachedAdLoadHelper.get(tagId).get() != null) {
            return mCachedAdLoadHelper.get(tagId).get();
        } else {
            BaseAdLoadHelper baseAdLoadHelper = new AdNetworkModeLoadHelper(context, tagId);
            mCachedAdLoadHelper.put(tagId, new WeakReference<>(baseAdLoadHelper));
            return baseAdLoadHelper;
        }
    }

    public static void removeAdLoadHelper(String placementId) {
        removeAdLoadHelper(placementId, true);
    }

    public static void removeAdLoadHelper(String tagId, boolean needAutoDestroy) {
        if (!TextUtils.isEmpty(tagId)) {
            WeakReference<BaseAdLoadHelper> remove = mCachedAdLoadHelper.remove(tagId);
            if (needAutoDestroy && remove != null && remove.get() != null)
                remove.get().onDestroy();
        }
    }

}
