package com.afanty.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.common.SafeToast;
import com.afanty.config.BasicAftConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class AppStarter {
    private static final String TAG = "AppStarter";

    private static final List<String> mBrowserPackages = new ArrayList<>();

    static {
        mBrowserPackages.add("com.android.chrome");
        mBrowserPackages.add("com.android.browser");
        mBrowserPackages.add("com.sec.android.app.sbrowser");
        mBrowserPackages.add("com.opera.browser");
        mBrowserPackages.add("com.opera.mini.android");
        mBrowserPackages.add("com.opera.mini.native");
        mBrowserPackages.add("com.UCMobile");
        mBrowserPackages.add("com.UCMobile.intl");
        mBrowserPackages.add("com.uc.browser.en");
        mBrowserPackages.add("com.UCMobile.internet.org");
        mBrowserPackages.add("com.uc.browser.hd");
        mBrowserPackages.add("org.mozilla.firefox");
        mBrowserPackages.add("com.tencent.mtt");
        mBrowserPackages.add("com.qihoo.browser");
        mBrowserPackages.add("com.baidu.browser.apps");
        mBrowserPackages.add("sogou.mobile.explorer");
        mBrowserPackages.add("com.zui.browser");
        mBrowserPackages.add("com.oupeng.browser");
        mBrowserPackages.add("com.oupeng.mini.android");
        mBrowserPackages.add("com.vivo.browser");
    }


    public static boolean startAppMarketWithUrl(Context context, String url, String packageName, String adId) {
        try {
            if (TextUtils.isEmpty(url) && TextUtils.isEmpty(packageName)) {
                return false;
            }

            if (TextUtils.isEmpty(url) && !TextUtils.isEmpty(packageName)) {
                url = "https://play.google.com/store/apps/details?id=" + packageName;
            }
            SettingConfig.setAutoStartInfo(packageName, adId);

            PackageManager packageManager = context.getPackageManager();
            Intent intentOpen = packageManager.getLaunchIntentForPackage(packageName);
            if (intentOpen != null) {
                return IntentUtils.startActivityWithTopActivity(context, intentOpen);
            }

            return startAppMarket(context, packageName, url);
        } catch (Exception e) {
            try {
                String gpUrl = handleGpDetailUrl(url);
                collectStartAppMarket(context, gpUrl, packageName, true, null);
                return startBrowserNoChoice(context, gpUrl, true);
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    private static boolean startGooglePlay(Context context, String url, String packageName) {
        String gpUrl = handleGpDetailUrl(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gpUrl));
        boolean hasGooglePlay = hasActivity(context, intent, "com.android.vending");
        collectStartAppMarket(context, gpUrl, packageName, !hasGooglePlay, intent);
        if (hasGooglePlay) {
            intent.setPackage("com.android.vending");
            return IntentUtils.startActivityWithTopActivity(context, intent);
        } else {
            return startBrowserNoChoice(context, gpUrl, true);
        }
    }

    private static boolean startAppMarket(Context context, String packageName, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        collectStartAppMarket(context, url, packageName, false, intent);
        return IntentUtils.startActivityWithTopActivity(context, intent);
    }

    public static String handleGpDetailUrl(String url) {
        if (url.contains("market://details"))
            url = url.replace("market://details", "https://play.google.com/store/apps/details");
        return url;
    }

    private static String handleGpToMarket(String url) {
        if (url.contains("https://play.google.com/store/apps/details"))
            url = url.replace("https://play.google.com/store/apps/details", "market://details");
        else if (url.contains("http://play.google.com/store/apps/details"))
            url = url.replace("http://play.google.com/store/apps/details", "market://details");
        return url;
    }

    private static boolean hasActivity(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : appList) {
            if (info.activityInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    private static void collectStartAppMarket(Context context, String url, String pkgName, boolean isBrowser, Intent openIntent) {
        long jumpDelay = isBrowser ? 0 : BasicAftConfig.getJumpMarketDelayTime();
        ThreadManager.getInstance().run(new DelayRunnableWork(jumpDelay) {
            @Override
            public void execute() {
            }
        });
    }

    public static boolean startBrowserNoChoice(Context context, String url, boolean newTask) {
        return startBrowserNoChoice(context, url, newTask, 0);
    }

    public static boolean startBrowserNoChoice(Context context, String url, boolean newTask, int failedResId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (newTask)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return startActivityNoChoice(context, intent, failedResId, mBrowserPackages);
    }

    private static boolean startActivityNoChoice(Context context, Intent intent, int failedResId, List<String> recommendPackages) {
        boolean success = false;
        try {
            ResolveInfo resolve = findResolveInfoByPkg(context, "com.android.chrome");
            if (resolve != null) {
                try {
                    intent.setPackage(resolve.activityInfo.packageName);
                    context.startActivity(intent);
                    success = true;
                } catch (Exception ae) {
                    startBrowser(context, intent, recommendPackages);
                }
            } else {
                startBrowser(context, intent, recommendPackages);
            }
        } catch (Exception e) {
        }
        if (!success && failedResId > 0)
            SafeToast.showToast(failedResId, Toast.LENGTH_SHORT);
        return success;
    }

    private static ResolveInfo findResolveInfoByPkg(Context context, String pkg) {
        ResolveInfo newAppInfo = null;
        try {
            List<ResolveInfo> mTempAllApps;
            PackageManager TempPackageManager = context.getPackageManager();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.setPackage(pkg);
            mTempAllApps = TempPackageManager.queryIntentActivities(mainIntent, 0);
            newAppInfo = mTempAllApps.get(0);
        } catch (Exception e) {

        }
        return newAppInfo;
    }

    private static void startBrowser(Context context, Intent intent, List<String> recommendPackages) {
        PackageManager pm = context.getPackageManager();
        ResolveInfo resolve = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ResolveInfo startResolve;
        if (!hasDefaultActivity(resolve, appList)) {
            startResolve = getResolveInfo(appList, recommendPackages);
        } else {
            if (resolve.activityInfo.packageName.equals(context.getPackageName()))
                startResolve = getResolveInfo(appList, recommendPackages);
            else
                startResolve = resolve;
        }
        if (startResolve != null) {
            intent.setPackage(startResolve.activityInfo.packageName);
        }
        try {
            context.startActivity(intent);
        } catch (Exception ae) {
        }
    }

    private static boolean hasDefaultActivity(ResolveInfo resolved, List<ResolveInfo> appList) {
        if (resolved == null || appList == null || appList.size() < 1)
            return false;
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo tmp = appList.get(i);
            if (tmp.activityInfo.name.equals(resolved.activityInfo.name)
                    && tmp.activityInfo.packageName.equals(resolved.activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    private static ResolveInfo getResolveInfo(List<ResolveInfo> appList, List<String> recommendPackages) {
        if (appList != null && appList.size() > 0) {
            if (recommendPackages != null && appList.size() > 1)
                Collections.sort(appList, new ResolveComparator(recommendPackages));
            return appList.get(0);
        }
        return null;
    }

    public static boolean startInstalledApp(Context context, String adId, String url, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intentOpen = packageManager.getLaunchIntentForPackage(packageName);
        boolean startResult = IntentUtils.startActivityWithTopActivity(context, intentOpen);
        if (startResult) {
        } else {
            startBrowserNoChoice(context, url, true);
        }
        return startResult;
    }


    private static class ResolveComparator implements Comparator<ResolveInfo> {
        private List<String> mRecommendPackages;

        public ResolveComparator(List<String> recommendPackages) {
            mRecommendPackages = recommendPackages;
        }

        public int compare(ResolveInfo info1, ResolveInfo info2) {
            int priority1 = mRecommendPackages.contains(info1.activityInfo.packageName)
                    ? mRecommendPackages.indexOf(info1.activityInfo.packageName)
                    : mRecommendPackages.size();
            int priority2 = mRecommendPackages.contains(info2.activityInfo.packageName)
                    ? mRecommendPackages.indexOf(info2.activityInfo.packageName)
                    : mRecommendPackages.size();
            return priority1 - priority2;
        }
    }

}
