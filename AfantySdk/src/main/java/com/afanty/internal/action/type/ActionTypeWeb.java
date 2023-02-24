package com.afanty.internal.action.type;

import android.content.Context;
import android.net.Uri;

import com.afanty.internal.action.ActionConstants;
import com.afanty.internal.action.ActionParam;
import com.afanty.internal.action.ActionResult;
import com.afanty.internal.action.ActionTypeInterface;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.models.Bid;
import com.afanty.utils.AppStarter;
import com.afanty.utils.PackageUtils;

public class ActionTypeWeb implements ActionTypeInterface {

    @Override
    public void resolveUrl(String deepLink, String landingPage, ResolveResultListener resolveResultListener) {
        resolveResultListener.onSuccess(true, landingPage);
    }

    @Override
    public boolean shouldTryHandlingAction(Bid ad, int actionType) {
        return getActionType() == actionType;
    }

    @Override
    public ActionResult performAction(Context context, Bid adData, String url, ActionParam actionParam) {
        boolean result;
        if (AdsUtils.isGPDetailUrl(url)) {
            // Get packageName If the gp landing page is already installed lift the app directly, otherwise jump to gp
            String packageName = "";
            String adId = "";
            try {
                packageName = Uri.parse(url).getQueryParameter("id");
                adId = adData.getAdId();
            } catch (Exception e) {
            }
            if (PackageUtils.isAppInstalled(context, packageName)) {
                result = AppStarter.startInstalledApp(context, adId, url, packageName);
            } else {
                result = AppStarter.startAppMarketWithUrl(context, url, packageName, adId);
            }
        } else {
            result = AppStarter.startBrowserNoChoice(context, url, true, 0);
        }
        return new ActionResult.Builder(result).build();
    }

    @Override
    public ActionResult performActionWhenOffline(Context context, Bid ad, String url, ActionParam actionParam) {
        boolean result;
        if (AdsUtils.isGPDetailUrl(url)) {
            result = AppStarter.startAppMarketWithUrl(context, url, "", ad.getAdId());
        } else {
            result = AppStarter.startBrowserNoChoice(context, url, true, 0);
        }
        return new ActionResult.Builder(result).offlineAction(true).build();
    }

    @Override
    public int getActionType() {
        return ActionConstants.ACTION_WEB;
    }
}
