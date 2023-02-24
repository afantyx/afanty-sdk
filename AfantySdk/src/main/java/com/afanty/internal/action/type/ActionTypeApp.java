package com.afanty.internal.action.type;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.afanty.internal.action.ActionConstants;
import com.afanty.internal.action.ActionParam;
import com.afanty.internal.action.ActionResult;
import com.afanty.internal.action.ActionTypeInterface;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.models.Bid;
import com.afanty.utils.AppStarter;
import com.afanty.utils.PackageUtils;

public class ActionTypeApp implements ActionTypeInterface {
    private static final String TAG = "APP";

    @Override
    public void resolveUrl(String deepLink, String landingPage, final ResolveResultListener resolveResultListener) {
        landingPage = AdsUtils.replaceMacroUrls(landingPage);
        if (AdsUtils.isGPDetailUrl(landingPage)) {
            resolveResultListener.onSuccess(true, landingPage);
            return;
        }
    }

    @Override
    public boolean shouldTryHandlingAction(Bid adData, int actionType) {
        return getActionType() == actionType;
    }

    @Override
    public ActionResult performAction(final Context context, final Bid adData, final String url, ActionParam actionParam) {
        boolean actionResult = false;
        String packageName = "";
        String adid = "";
        try {
            if (AdsUtils.isGPDetailUrl(url)) {
                packageName = Uri.parse(url).getQueryParameter("id");
            }
            adid = adData.getAdId();

            if (TextUtils.isEmpty(packageName) && actionParam.mBid != null && actionParam.mBid.getProductData() != null) {
                packageName = actionParam.mBid.getProductData().getPkgName();
            }
        } catch (Exception e) {
        }

        if (PackageUtils.isAppInstalled(context, packageName)) {
            actionResult = AppStarter.startInstalledApp(context, adid, url, packageName);
        } else if (AdsUtils.isGPDetailUrl(url)) {
            actionResult = AppStarter.startAppMarketWithUrl(context, url, packageName, adData.getAdId());
        } else if (!TextUtils.isEmpty(packageName)) {
            String jumpUrl = "https://play.google.com/store/apps/details?id=" + packageName;
            actionResult = AppStarter.startAppMarketWithUrl(context, jumpUrl, packageName, adData.getAdId());
        }
        return new ActionResult.Builder(actionResult).build();
    }

    @Override
    public ActionResult performActionWhenOffline(final Context context, final Bid adData, final String url, ActionParam actionParam) {
        boolean actionResult = AppStarter.startAppMarketWithUrl(context, adData.getLandingPage(), adData.getMatchAppPkgName(), adData.getAdId());
        return new ActionResult.Builder(actionResult).offlineAction(true).build();
    }

    @Override
    public int getActionType() {
        return ActionConstants.ACTION_GP;
    }
}
