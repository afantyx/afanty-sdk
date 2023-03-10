// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import android.content.Context;

public class VastManagerFactory {
    protected static VastManagerFactory instance = new VastManagerFactory();

    public static VastManager create(final Context context) {
        return instance.internalCreate(context, true);
    }

    public static VastManager create(final Context context, boolean preCacheVideo) {
        return instance.internalCreate(context, preCacheVideo);
    }

    public VastManager internalCreate(final Context context, boolean preCacheVideo) {
        return new VastManager(context, preCacheVideo);
    }

    @Deprecated // for testing
    public static void setInstance(VastManagerFactory factory) {
        instance = factory;
    }
}
