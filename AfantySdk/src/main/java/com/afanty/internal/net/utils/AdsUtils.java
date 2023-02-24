package com.afanty.internal.net.utils;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;

import com.afanty.common.oaid.OAIDHelper;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.DeviceUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdsUtils {

    public static String replaceMacroUrlsForSite(String url, String posId, String sid) {
        if (TextUtils.isEmpty(url))
            return url;

        if (url.contains("{POS_ID}") || url.contains("{pos_id}")) {
            if (!TextUtils.isEmpty(posId))
                url = url.replace("{POS_ID}", posId).replace("{pos_id}", posId);
        }
        if (url.contains("{adpos_id}") || url.contains("{ADPOS_ID}")) {
            if (!TextUtils.isEmpty(posId))
                url = url.replace("{adpos_id}", posId).replace("{ADPOS_ID}", posId);
        }
        if (url.contains("__SID__") || url.contains("__sid__")) {
            if (!TextUtils.isEmpty(sid))
                url = url.replace("__SID__", sid).replace("__sid__", sid);
        }
        if (url.contains("{placement}") || url.contains("{PLACEMENT}")) {
            if (!TextUtils.isEmpty(posId)) {
                url = url.replace("{placement}", posId).replace("{PLACEMENT}", posId);
            }
        }
        return url;
    }

    public static String replaceMarcoUrls(String url, String macro, String value) {
        if (!TextUtils.isEmpty(macro) && !TextUtils.isEmpty(value) && url.contains(macro)) {
            url = url.replace(macro, value);
        }
        return url;
    }

    public static String replaceMacroUrlsForImmerse(String url, String is_oneshot, String splashisimg) {
        if (url.contains("__ISONESHOT__") || url.contains("__is_oneshot__")) {
            if (!TextUtils.isEmpty(is_oneshot))
                url = url.replace("__ISONESHOT__", is_oneshot).replace("__is_oneshot__", is_oneshot);
        }
        if (url.contains("__SPLASHISIMG__") || url.contains("__splashisimg__")) {
            if (!TextUtils.isEmpty(splashisimg))
                url = url.replace("__SPLASHISIMG__", splashisimg).replace("__splashisimg__", splashisimg);
        }
        return url;
    }

    public static String replaceMacroUrls(String url) {
        if (TextUtils.isEmpty(url))
            return url;

        if (url.contains("{TIMESTAMP}") || url.contains("{timestamp}")) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            url = url.replace("{TIMESTAMP}", timestamp).replace("{timestamp}", timestamp);
        }
        if (url.contains("{GAID}") || url.contains("{gaid}")) {
            String gaid = DeviceUtils.getGAID(ContextUtils.getContext());
            if (!TextUtils.isEmpty(gaid))
                url = url.replace("{GAID}", gaid).replace("{gaid}", gaid);
        }
        if (url.contains("{OAID}") || url.contains("{oaid}")) {
            String oaid = OAIDHelper.getOAID(ContextUtils.getContext());
            if (!TextUtils.isEmpty(oaid))
                url = url.replace("{OAID}", oaid).replace("{oaid}", oaid);
        }
        if (url.contains("{clickid}") || url.contains("{CLICKID}")) {
            String uuid = UUID.randomUUID().toString();
            url = url.replace("{clickid}", uuid).replace("{CLICKID}", uuid);
        }
        if (url.contains("{os_version}") || url.contains("{OS_VERSION}")) {
            String os_version = Build.VERSION.RELEASE;
            url = url.replace("{os_version}", os_version).replace("{OS_VERSION}", os_version);
        }
        return url;
    }

    public static boolean isGPDetailUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.startsWith("market://") || lowerCaseUrl.startsWith("https://play.google.com/") || lowerCaseUrl.startsWith("http://play.google.com/");
    }

    public static String processHtml(String jstag) {
        String htmlData = Html.fromHtml(jstag).toString();
        if (!isNormalHtml(htmlData)) {
            htmlData = jstag;
        }

        return replaceMacroJsTagUrls(htmlData);
    }

    private static boolean isNormalHtml(String str) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(str);
        return m.matches();
    }

    private static String replaceMacroJsTagUrls(String htmlData) {
        if (TextUtils.isEmpty(htmlData))
            return htmlData;

        if (htmlData.contains("{TIMESTAMP}") || htmlData.contains("{timestamp}")) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            htmlData = htmlData.replace("{TIMESTAMP}", timestamp).replace("{timestamp}", timestamp);
        }
        if (htmlData.contains("{GAID}") || htmlData.contains("{gaid}")) {
            String gaid = DeviceUtils.getGAID(ContextUtils.getContext());
            htmlData = htmlData.replace("{GAID}", gaid).replace("{gaid}", gaid);
        }
        if (htmlData.contains("{OAID}") || htmlData.contains("{oaid}")) {
            String oaid = OAIDHelper.getOAID(ContextUtils.getContext());
            if (!TextUtils.isEmpty(oaid))
                htmlData = htmlData.replace("{OAID}", oaid).replace("{oaid}", oaid);
        }
        return htmlData;
    }

    public static String getFinalUrl(String strUrl, String userAgent) {
        HttpURLConnection urlConnect = null;
        try {
            URL url = new URL(strUrl);
            urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setConnectTimeout(15000);
            urlConnect.setReadTimeout(15000);
            urlConnect.setInstanceFollowRedirects(false);
            urlConnect.setRequestProperty("User-Agent", userAgent);
            urlConnect.getContent();
            if (urlConnect.getResponseCode() == 302) {
                String location = urlConnect.getHeaderField("Location");
                return getFinalUrl(location, userAgent);
            }
            return strUrl;
        } catch (Exception e) {
            return strUrl;
        } finally {
            if (urlConnect != null)
                urlConnect.disconnect();
        }
    }

}
