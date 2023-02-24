package com.afanty.internal.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.ads.AdError;

public class AdView extends FrameLayout {

    public AdView(@NonNull Context context) {
        super(context);
    }

    public AdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public interface BannerAdListener {
        void onBannerLoaded(AdView banner);

        void onBannerFailed(AdView banner, AdError errorCode);

        void onBannerClicked(AdView banner);

        void onImpression(AdView banner);
    }
}
