package com.afanty.internal.banner.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afanty.R;
import com.afanty.ads.AdError;
import com.afanty.ads.AdSize;
import com.afanty.ads.AftImageLoader;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.banner.AbsBaseBanner;
import com.afanty.internal.banner.AdView;
import com.afanty.internal.banner.BannerLoaderInterface;
import com.afanty.internal.view.CustomProgressBar;
import com.afanty.models.AdmBean;
import com.afanty.models.Bid;

public class NativeBanner extends AbsBaseBanner {
    private static final String TAG = "Banner.Native";
    private CustomProgressBar mButton;

    @SuppressLint("InflateParams")
    @Override
    public void loadBanner(final Context context, AdSize adSize, AdView adView, Bid bid, BannerLoaderInterface bannerLoaderInterface) {
        setBidAndListener(bid, bannerLoaderInterface);
        if (bid == null) {
            bannerLoaderInterface.onAdBannerFailed(AdError.DIS_CONDITION_ERROR);
            return;
        }

        if (isAdSizeConform(adSize, bid)) {
            adView.removeAllViews();

            int layoutId = BridgeManager.getResourceId(context, "aft_banner_native_image_ex");
            if (layoutId == 0) {
                layoutId = R.layout.aft_banner_native_image;
            }
            ViewGroup adContainer = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
            ImageView imageView = adContainer.findViewById(R.id.icon);
            TextView title = adContainer.findViewById(R.id.title);
            TextView message = adContainer.findViewById(R.id.message);
            mButton = adContainer.findViewById(R.id.btn_stereo_progress);
            AftImageLoader.getInstance().loadLandingRoundCornerUrl(context, bid.getUrlByType(AdmBean.IMG_ICON_TYPE), imageView, R.drawable.aft_icon_bg, context.getResources().getDimensionPixelSize(R.dimen.aft_common_dimens_7dp));
            title.setText(bid.getTitle());
            if (TextUtils.isEmpty(bid.getDataByType(AdmBean.TEXT_DESC))) {
                message.setVisibility(View.GONE);
            } else {
                message.setText(bid.getDataByType(AdmBean.TEXT_DESC));
                message.setVisibility(View.VISIBLE);
            }
            mButton.setText(bid.getDataByType(AdmBean.TEXT_BTN));
            adView.addView(adContainer, 0);
            bannerLoaderInterface.onAdBannerSuccess(adContainer);

            imageView.setOnClickListener(mClickListener);
            title.setOnClickListener(mClickListener);
            message.setOnClickListener(mClickListener);
            mButton.registerClick(bid, new CustomProgressBar.RegisterTextProgressListener() {
                @Override
                public void onNormal(boolean openOpt, boolean CTAOpt) {
                    performActionForAdClicked(context, "cardbutton", ActionUtils.getDownloadOptTrig(openOpt, CTAOpt));
                }
            });
        } else {
            bannerLoaderInterface.onAdBannerFailed(AdError.DIS_CONDITION_ERROR);
        }
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            performActionForInternalClick(v.getContext());
        }
    };

    @Override
    protected boolean isAdSizeConform(AdSize adSize, Bid bid) {
        return true;
    }

    @Override
    protected Point resolvedAdSize(AdSize adSize) {
        return new Point(320, 50);
    }

    @Override
    public void destroy() {
        if (mButton != null)
            mButton.destroy();
    }
}
