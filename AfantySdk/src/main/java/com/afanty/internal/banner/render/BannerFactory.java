package com.afanty.internal.banner.render;

import com.afanty.internal.banner.AbsBaseBanner;
import com.afanty.internal.internal.CreativeType;

public class BannerFactory {
    private static BannerFactory sInstance;

    private BannerFactory() {
    }

    public static BannerFactory getInstance() {
        if (sInstance == null) {
            synchronized (BannerFactory.class) {
                if (sInstance == null)
                    sInstance = new BannerFactory();
            }
        }
        return sInstance;
    }

    public AbsBaseBanner getBanner(int creativeType) {
        switch (creativeType) {
            case CreativeType.TYPE_PT_P1T2:
            case CreativeType.TYPE_T_P1T1:
                return new NativeBanner();
            case CreativeType.TYPE_P_P1:
                return new SingleImageBanner();
            default:
                return null;
        }
    }

}
