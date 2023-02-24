package com.afanty.utils;

import com.afanty.common.constants.Settings;
import com.afanty.constant.SettingsNameConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingConfig {
    public static String getMacAddressId() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_MAC_ADDRESS_ID);
    }

    public static void setMacAddressId(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_MAC_ADDRESS_ID, source);
    }

    public static String getDeviceIMEI() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_IMEI);
    }

    public static void setDeviceIMEI(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_IMEI, source);
    }

    public static String getAndroidId() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_ANDROID_ID);
    }

    public static void setAndroidId(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_ANDROID_ID, source);
    }

    public static String getStorageCid() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_STORAGE_CID);
    }

    public static void setStorageCid(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_STORAGE_CID, source);
    }

    public static String getBuildSn() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_BUILD_SN);
    }

    public static void setBuildSn(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_BUILD_SN, source);
    }

    public static String getWebUA() {
        return new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).get(Settings.KEY_WEB_UA, "");
    }

    public static void setWebUA(String source) {
        new SettingsSp(ContextUtils.getContext(), Settings.DEVICE_SETTINGS).set(Settings.KEY_WEB_UA, source);
    }

    public static boolean getEUGdpr(boolean defaultVal) {
        return new SettingsSp(ContextUtils.getContext(), Settings.GDPR_SETTINGS)
                .getBoolean(Settings.KEY_AD_GDPR_CONSENT, defaultVal);
    }

    public static void setEUGdpr(boolean consent) {
        setSiEUGdpr(consent);
        new SettingsSp(ContextUtils.getContext(), Settings.GDPR_SETTINGS).setBoolean(Settings.KEY_AD_GDPR_CONSENT, consent);
    }

    public static void setSiEUGdpr(boolean consent) {
        new SettingsSp(ContextUtils.getContext()).setBoolean(Settings.KEY_AD_GDPR_CONSENT, consent);
    }

    public static String getBaseStations() {
        return new SettingsSp(ContextUtils.getContext(), Settings.AD_SETTINGS).get(Settings.KEY_BASE_STATIONS);
    }

    public static void setAutoStartInfo(String pkgName, String adId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pkgName", pkgName);
            jsonObject.put("adId", adId);
            jsonObject.put("saveTime", System.currentTimeMillis());
            String autoStartInfo = jsonObject.toString();

            new SettingsSp(ContextUtils.getContext(), Settings.AD_SETTINGS).set(Settings.KEY_AUTO_START, autoStartInfo);
        } catch (JSONException e) {
        }
    }


    public static void setFinalUrl(String downloadUrl, String finalUrl) {
        new SettingsSp(ContextUtils.getContext(), Settings.KEY_FINAL_URL).set(downloadUrl, finalUrl);
    }

    public static String getFinalUrl(String downloadUrl) {
        return new SettingsSp(ContextUtils.getContext(), Settings.KEY_FINAL_URL).get(downloadUrl);
    }

    public static void removeFinalUrl(String downloadUrl) {
        new SettingsSp(ContextUtils.getContext(), Settings.KEY_FINAL_URL).remove(downloadUrl);
    }

    public static void setCachedVastUrlByAdcId(String adId, String url) {
        new SettingsSp(ContextUtils.getContext(), Settings.OFFLINE_SETTING).set(Settings.OFFLINE_CACHED_VAST_AD + adId, url);
    }

    public static String getCachedVastVideoUrlByAdcId(String adId) {
        return new SettingsSp(ContextUtils.getContext(), Settings.OFFLINE_SETTING).get(Settings.OFFLINE_CACHED_VAST_AD + adId);
    }

    public static void setOAID(String value) {
        new SettingsSp(ContextUtils.getContext(), Settings.AD_SETTINGS).set(Settings.OAID, value);
    }

    public static String getOAID() {
        return new SettingsSp(ContextUtils.getContext(), Settings.AD_SETTINGS).get(Settings.OAID);
    }

    public static String getSaveReceivePkg() {
        return new SettingsSp(ContextUtils.getContext(), Settings.PKG_SETTINGS).get(Settings.KEY_PKG_NAME_SAVE);
    }

    public static void setHadShownInAppLifeCycle(boolean hasShown) {
        new SettingsSp(ContextUtils.getContext(), SettingsNameConstant.KEY_PROINSTALL_SETTING).setBoolean("has_shown", hasShown);
    }
}
