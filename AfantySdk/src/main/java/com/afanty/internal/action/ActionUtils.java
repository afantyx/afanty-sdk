package com.afanty.internal.action;

import android.content.Intent;
import android.text.TextUtils;

import com.afanty.AttributionManager;
import com.afanty.internal.internal.AdConstants;
import com.afanty.models.Bid;
import com.afanty.utils.AppStarter;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.log.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionUtils {
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public static void increaseShowCount(Bid bid) {
        increaseShowCount(bid, AdConstants.Extra.AD_PORTAL);
    }

    public static void increaseShowCount(Bid bid, String portal) {
        increaseShowCount(bid, false, bid == null ? Arrays.asList() : bid.getTrackImpUrls(), portal);
    }

    private static void increaseShowCount(final Bid bid, boolean needAddUp, List<String> trackClickUrls, String portal) {
        if (bid == null) {
            return;
        }
        bid.addCurShowCnt();

        if (bid.isEffectiveShow() || needAddUp) {
            increaseShowCountToday(bid);
            AttributionManager.getInstance().reportShow(trackClickUrls, bid, new AttributionManager.AdReportListener() {
                @Override
                public void reportResult(boolean hasReportFailure) {
                }
            });
        }
    }

    public static void increaseClickCount(Bid adData) {
        increaseClickCount(adData, AdConstants.Extra.AD_PORTAL);
    }

    public static void increaseClickCount(Bid adData, String portal) {
        increaseClickCount(adData, adData.getTrackClickUrls(), portal);
    }

    public static void increaseClickCount(final Bid adData, List<String> trackClickUrls, String portal) {
        AttributionManager.getInstance().reportClick(trackClickUrls, adData, hasReportFailure -> {
            printTracker(trackClickUrls,"click track:",!hasReportFailure);
        });
    }

    public static void increaseShowCountToday(Bid adData) {
        long today = System.currentTimeMillis() / ONE_DAY_IN_MILLIS;
        String countValue = adData.getShowCountToday();
        if (!TextUtils.isEmpty(countValue)) {
            String[] array = countValue.split("_");
            if (array.length == 2) {
                try {
                    long date = Long.parseLong(array[0]);
                    int count = Integer.parseInt(array[1]);
                    if (today == date)
                        adData.setShowCountToday(today + "_" + (count + 1));
                    else
                        adData.setShowCountToday(today + "_" + 1);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private static void printTracker(List<String> trackClickUrls,String type,boolean result){
        StringBuilder strb = new StringBuilder();
        for (String track:trackClickUrls) {
            strb.append(track).append(",");
        }
        strb.deleteCharAt(strb.lastIndexOf(","));
        Logger.d("AD_REPORT","["+result+"]"+type+strb.toString());
    }

    public static void reportActionTracker(final ActionParam actionParam) {
        if (actionParam.mBid != null) {
            List<String> actionAdUrls = (actionParam.mBid).getTrackActionAdvertiserUrls();
            final List<String> actionUrls = new ArrayList<>();
            if (!actionAdUrls.isEmpty())
                actionUrls.addAll(actionAdUrls);
            AttributionManager.getInstance().reportClick(actionUrls, actionParam.mBid, hasReportFailure -> {
                printTracker(actionUrls,"adv tracker:",!hasReportFailure);
            });
        }
    }

    public static boolean startAppByDeeplink(String url) {
        try {
            Intent intent = Intent.parseUri(url, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("need_safe", true);
            ContextUtils.getContext().startActivity(intent);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isGPAction(Bid adData) {
        if (adData == null)
            return false;

        return adData.getAdActionType() == ActionConstants.ACTION_GP;
    }

    private static final int TRIG_DOWNLOAD_INSTALL = -2;
    private static final int TRIG_DOWNLOAD_OPEN = -3;

    private static final int TRIG_WEB = 1;
    private static final int TRIG_APK_OPERATE = 4;
    private static final int TRIG_APP_GP = 5;
    private static final int TRIG_APP_DOWNLOAD = 6;
    private static final int TRIG_DEEPLINK = 7;
    private static final int TRIG_LAND_PAGE = 8;

    public static int getTrig(int actionHandlerType, int actionType, int downloadOptTrig) {
        if (actionHandlerType == ActionExecutor.ActionExecutorListener.ACTION_TYPE_DETAIL_PAGE) {
            return TRIG_LAND_PAGE;
        } else if (actionHandlerType == ActionExecutor.ActionExecutorListener.ACTION_TYPE_DEEPLINL) {
            return TRIG_DEEPLINK;
        } else {
            if (downloadOptTrig == TRIG_DOWNLOAD_INSTALL)
                return TRIG_DOWNLOAD_INSTALL;
            if (downloadOptTrig == TRIG_DOWNLOAD_OPEN)
                return TRIG_DOWNLOAD_OPEN;

            if (actionType == ActionConstants.ACTION_WEB)
                return TRIG_WEB;
            else if (actionType == ActionConstants.ACTION_OPERATE_APK)
                return TRIG_APK_OPERATE;
            else if (actionType == ActionConstants.ACTION_GP)
                return TRIG_APP_GP;
        }
        return -1;
    }
    public static int getDownloadOptTrig(boolean openOpt, boolean installOpt) {
        if (installOpt) return -2;
        if (openOpt) return -3;

        return -1;
    }
}
