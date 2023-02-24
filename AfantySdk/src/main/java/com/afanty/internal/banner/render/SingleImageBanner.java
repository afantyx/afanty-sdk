package com.afanty.internal.banner.render;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afanty.R;
import com.afanty.ads.AdError;
import com.afanty.ads.AdSize;
import com.afanty.ads.AftImageLoader;
import com.afanty.internal.banner.AbsBaseBanner;
import com.afanty.internal.banner.AdView;
import com.afanty.internal.banner.BannerLoaderInterface;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;
import com.afanty.utils.DensityUtils;

public class SingleImageBanner extends AbsBaseBanner {
    private static final String TAG = "Banner.SingleImage";

    @Override
    public void loadBanner(Context context, AdSize adSize, AdView adView, Bid bid, BannerLoaderInterface bannerLoaderInterface) {
        setBidAndListener(bid, bannerLoaderInterface);
        if (bid == null) {
            bannerLoaderInterface.onAdBannerFailed(AdError.DIS_CONDITION_ERROR);
            return;
        }

        if (isAdSizeConform(adSize, bid)) {
            adView.removeAllViews();

            ImageView imageView = new ImageView(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DensityUtils.dip2px(adSize.getWidth()),
                    DensityUtils.dip2px(adSize.getHeight()));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(layoutParams);
            adView.setLayoutParams(layoutParams);
            AftImageLoader.getInstance().loadUri(context, bid.getUrlByType(AdmBean.IMG_POSTER_TYPE), imageView);
            imageView.setOnClickListener(v -> performActionForInternalClick(v.getContext()));
            adView.addView(imageView, 0);

            ImageView adLogoView = new ImageView(context);
            FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(DensityUtils.dip2px(18), DensityUtils.dip2px(12));
            logoParams.gravity = 53;
            adLogoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            adLogoView.setLayoutParams(logoParams);
            adLogoView.setImageResource(R.drawable.aft_ad_logo);
            adView.addView(adLogoView);

            bannerLoaderInterface.onAdBannerSuccess(imageView);
        } else {
            bannerLoaderInterface.onAdBannerFailed(AdError.DIS_CONDITION_ERROR);
        }
    }

    @Override
    protected boolean isAdSizeConform(AdSize adSize, Bid bid) {
        return true;
    }
}
