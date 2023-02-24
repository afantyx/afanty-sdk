package com.afanty.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.afanty.common.UserInfoHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IMSUtils {

    private static IMSInfo mImsInfo = null;

    public static IMSInfo getIMSInfo(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return null;
        }
        if (context == null) {
            return null;
        }
        IMSInfo imsInfo = getMtkDoubleSim(context);
        if (imsInfo != null && imsInfo.isAvailable()) {
            return imsInfo;
        }
        imsInfo = getQualcommDoubleSim(context);
        if (imsInfo != null && imsInfo.isAvailable()) {
            return imsInfo;
        }
        imsInfo = getSpreadDoubleSim(context);
        return (imsInfo != null && imsInfo.isAvailable()) ? imsInfo : getDefaultSim(context);
    }

    public static String getIMSI(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        List<String> imsiList = getIMSIs(context);
        return imsiList != null && !imsiList.isEmpty() ? (String) imsiList.get(0) : "";
    }

    public static List<String> getIMSIs(Context context) {
        if (mImsInfo == null) {
            mImsInfo = IMSUtils.getIMSInfo(context);
        }

        if (mImsInfo == null) {
            return new ArrayList();
        } else {
            return mImsInfo.getIMSIList();
        }
    }

    @SuppressLint({"HardwareIds", "MissingPermission", "PrivateApi"})
    private static IMSInfo getMtkDoubleSim(Context context) {
        Integer simId1 = 0, simId2 = 1;
        try {
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            try {
                Field f = c.getField("GEMINI_SIM_1");
                f.setAccessible(true);
                simId1 = (Integer) f.get(null);
                f.setAccessible(false);
            } catch (Throwable e) {
            }
            try {
                Field f = c.getField("GEMINI_SIM_2");
                f.setAccessible(true);
                simId2 = (Integer) f.get(null);
                f.setAccessible(false);
            } catch (Throwable e) {
            }
        } catch (Throwable e) {
        }

        IMSInfo imsInfo = new IMSInfo();
        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("android.provider.MultiSIMUtils");
            Method m = c.getMethod("getDefault", Context.class);
            Object multiSimUtils = m.invoke(c, context);
            if (multiSimUtils != null) {
                Method md = c.getMethod("getDeviceId", int.class);
                Method ms = c.getMethod("getSubscriberId", int.class);
                imsInfo.mSimType = SimType.DUAL_SIM;
                imsInfo.mIMEI1 = (String) md.invoke(multiSimUtils, simId1);
                imsInfo.mIMSI1 = (String) ms.invoke(multiSimUtils, simId1);
                imsInfo.mIMEI2 = (String) md.invoke(multiSimUtils, simId2);
                imsInfo.mIMSI2 = (String) ms.invoke(multiSimUtils, simId2);
                imsInfo.updateStateManual();
                imsInfo.updateTypeManual();
            }
        } catch (Throwable e) {
        }

        if (imsInfo.isAvailable()) {
            return imsInfo;
        }

        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null)
            return null;

        try {
            Method md = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
            Method ms = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
            imsInfo.mSimType = SimType.DUAL_SIM;
            imsInfo.mIMEI1 = (String) md.invoke(tm, simId1);
            imsInfo.mIMSI1 = (String) ms.invoke(tm, simId1);
            imsInfo.mIMEI2 = (String) md.invoke(tm, simId2);
            imsInfo.mIMSI2 = (String) ms.invoke(tm, simId2);
            imsInfo.updateStateManual();
            imsInfo.updateTypeManual();
        } catch (Throwable e) {
        }
        if (imsInfo.isAvailable())
            return imsInfo;

        try {
            Class<?> telephonyClass = Class.forName(tm.getClass().getName());
            Method m = telephonyClass.getMethod("getDefault", int.class);
            TelephonyManager tm1 = (TelephonyManager) m.invoke(tm, simId1);
            TelephonyManager tm2 = (TelephonyManager) m.invoke(tm, simId2);
            if (tm1 != null && tm2 != null) {
                imsInfo.mSimType = SimType.DUAL_SIM;
                imsInfo.mIMEI1 = tm1.getDeviceId();
                imsInfo.mIMSI1 = tm1.getSubscriberId();
                imsInfo.mIMEI2 = tm2.getDeviceId();
                imsInfo.mIMSI2 = tm2.getSubscriberId();
                imsInfo.updateStateManual();
                imsInfo.updateTypeManual();
            }
        } catch (Throwable e) {
        }
        return imsInfo;
    }

    /**
     * 高通芯片
     */
    private static IMSInfo getQualcommDoubleSim(Context context) {
        IMSInfo imsInfo = new IMSInfo();
        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("android.telephony.MSimTelephonyManager");
            @SuppressLint("WrongConstant") Object obj = context.getApplicationContext().getSystemService("phone_msim");
            if (obj == null)
                return null;
            Method md = c.getMethod("getDeviceId", int.class);
            Method ms = c.getMethod("getSubscriberId", int.class);
            Integer simId1 = 0, simId2 = 1;
            imsInfo.mSimType = SimType.DUAL_SIM;
            imsInfo.mIMEI1 = (String) md.invoke(obj, simId1);
            imsInfo.mIMSI1 = (String) ms.invoke(obj, simId1);
            imsInfo.mIMEI2 = (String) md.invoke(obj, simId2);
            imsInfo.mIMSI2 = (String) ms.invoke(obj, simId2);
            imsInfo.updateStateManual();
            imsInfo.updateTypeManual();
        } catch (Throwable e) {
        }
        return imsInfo;
    }

    public static String getQualcommCardName() {
        try {
            @SuppressLint("PrivateApi") Class<?> c = Class.forName("android.telephony.MSimTelephonyManager");
            Method m = c.getDeclaredMethod("getTelephonyProperty", String.class, int.class, String.class);
            return (String) m.invoke(c, "gsm.operator.alpha", 0, null);
        } catch (Throwable e) {
        }
        return null;
    }

    @SuppressLint({"HardwareIds", "PrivateApi", "MissingPermission"})
    private static IMSInfo getSpreadDoubleSim(Context context) {
        IMSInfo imsInfo = new IMSInfo();
        try {
            Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method m = c.getMethod("getServiceName", String.class, int.class);
            String spreadTMService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
            if (spreadTMService == null || spreadTMService.length() == 0) {
                return imsInfo;
            }
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                imsInfo.mSimType = SimType.SINGLE_SIM;
                imsInfo.mIMEI1 = tm.getDeviceId();
                imsInfo.mIMSI1 = tm.getSubscriberId();
            }
            TelephonyManager spreadTM = (TelephonyManager) context.getApplicationContext().getSystemService(spreadTMService);
            if (spreadTM != null) {
                imsInfo.mSimType = SimType.DUAL_SIM;
                imsInfo.mIMEI2 = spreadTM.getDeviceId();
                imsInfo.mIMSI2 = spreadTM.getSubscriberId();
            }
            imsInfo.updateStateManual();
            imsInfo.updateTypeManual();
        } catch (Throwable a) {
        }
        return imsInfo;
    }

    /**
     * Accessed via the system API
     */
    public static IMSInfo getDefaultSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        IMSInfo imsInfo = new IMSInfo();
        try {
            imsInfo.mIMEI1 = getDefaultImei(tm, 0);
            imsInfo.mIMEI2 = getDefaultImei(tm, 1);
            imsInfo.mIMSI1 = getDefaultImsi(context, tm, 0);
            imsInfo.mIMSI2 = getDefaultImsi(context, tm, 1);
            imsInfo.mSimType = getSimType(tm);
            imsInfo.mActiveState = getSimState(context, tm);
            if (imsInfo.mActiveState == ActiveState.UNKNOWN) {
                imsInfo.updateStateManual();
            }
            if (imsInfo.mSimType == SimType.UNKNOWN) {
                imsInfo.updateTypeManual();
            }
        } catch (Exception e) {
        }
        return imsInfo;
    }

    private static SimType getSimType(TelephonyManager tm) {
        SimType simType = SimType.UNKNOWN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int phoneCount = tm.getPhoneCount();
            switch (phoneCount) {
                case 1:
                    simType = SimType.SINGLE_SIM;
                    break;
                case 2:
                    simType = SimType.DUAL_SIM;
                    break;
                default:
                    simType = SimType.NO_SIM;
            }
        }
        return simType;
    }

    private static ActiveState getSimState(Context context, TelephonyManager tm) {
        ActiveState state = ActiveState.UNKNOWN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int sim1State = tm.getSimState(0);
            int sim2State = tm.getSimState(1);
            if (sim1State == TelephonyManager.SIM_STATE_READY
                    && sim2State == TelephonyManager.SIM_STATE_READY) {
                state = ActiveState.DOUBLE_ACTIVE;
            } else if (sim1State == TelephonyManager.SIM_STATE_READY ||
                    sim2State == TelephonyManager.SIM_STATE_READY) {
                state = ActiveState.SINGLE_ACTIVE;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sm = (SubscriptionManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            @SuppressLint("MissingPermission") int activeCount = sm.getActiveSubscriptionInfoCount();
            switch (activeCount) {
                case 1:
                    state = ActiveState.SINGLE_ACTIVE;
                    break;
                case 2:
                    state = ActiveState.DOUBLE_ACTIVE;
                    break;
                default:
                    state = ActiveState.NO_ACTIVE;
            }
        }
        return state;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getDefaultImei(TelephonyManager tm, int slotIndex) {
        String imei = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = tm.getImei(slotIndex);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imei = tm.getDeviceId(slotIndex);
        } else {
            imei = slotIndex == 0 ? tm.getDeviceId() : null;
        }
        return imei;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getDefaultImsi(Context context, TelephonyManager tm, int slotIndex) {
        String imsi = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(slotIndex);
                if (subscriptionInfo != null) {
                    int subId = subscriptionInfo.getSubscriptionId();
                    imsi = tm.createForSubscriptionId(subId).getSubscriberId();
                }
            } else {
                Method ms = TelephonyManager.class.getMethod("getSubscriberId", int.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager sm = (SubscriptionManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    @SuppressLint("MissingPermission") SubscriptionInfo subscriptionInfo = sm.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);
                    if (subscriptionInfo != null) {
                        imsi = (String) ms.invoke(tm, subscriptionInfo.getSubscriptionId()); // Sim slot 1 IMSI
                    }
                } else {
                    imsi = (String) ms.invoke(tm, slotIndex);
                }
                return imsi;
            }
        } catch (Exception e) {
            imsi = (slotIndex == 0) ? tm.getSubscriberId() : null;
        }
        return imsi;
    }

    public enum SimType {
        UNKNOWN, NO_SIM, SINGLE_SIM, DUAL_SIM
    }

    public enum ActiveState {
        UNKNOWN, NO_ACTIVE, SINGLE_ACTIVE, DOUBLE_ACTIVE
    }

    public static class IMSInfo {
        public SimType mSimType = SimType.UNKNOWN;
        public ActiveState mActiveState = ActiveState.UNKNOWN;
        public String mIMEI1;
        public String mIMEI2;
        public String mIMSI1;
        public String mIMSI2;

        public boolean isAvailable() {
            return mSimType != SimType.UNKNOWN && mActiveState != ActiveState.UNKNOWN
                    && ((mIMEI1 != null && mIMEI1.length() >= 10) || (mIMEI2 != null && mIMEI2.length() >= 10));
        }

        private boolean checkValueAvailable(String value) {
            return !TextUtils.isEmpty(value) && value.length() >= 10;
        }

        public String getBetterIMEI() {
            return checkValueAvailable(mIMEI1) ? mIMEI1 : mIMEI2;
        }

        public List<String> getIMSIList() {
            Set<String> imsiSet = new HashSet<>();
            if (checkValueAvailable(mIMSI1)) {
                imsiSet.add(mIMSI1);
            }
            if (checkValueAvailable(mIMSI2)) {
                imsiSet.add(mIMSI2);
            }
            return new ArrayList<String>(imsiSet);
        }

        public List<String> getIMEIList() {
            List<String> imeiSet = new ArrayList<>();
            if (checkValueAvailable(mIMEI1)) {
                imeiSet.add(mIMEI1);
            }
            if (checkValueAvailable(mIMEI2)) {
                imeiSet.add(mIMEI2);
            }
            return new ArrayList<String>(imeiSet);
        }

        public void updateStateManual() {
            List<String> imsiSet = getIMSIList();
            if (imsiSet.isEmpty()) {
                mActiveState = ActiveState.NO_ACTIVE;
            } else if (imsiSet.size() < 2) {
                mActiveState = ActiveState.SINGLE_ACTIVE;
            } else {
                mActiveState = ActiveState.DOUBLE_ACTIVE;
            }
        }

        public void updateTypeManual() {
            List<String> imeiSet = getIMEIList();
            if (imeiSet.isEmpty()) {
                mSimType = SimType.NO_SIM;
            } else if (imeiSet.size() < 2) {
                mSimType = SimType.SINGLE_SIM;
            } else {
                mSimType = SimType.DUAL_SIM;
            }
            if (mActiveState == ActiveState.DOUBLE_ACTIVE) {
                mSimType = SimType.DUAL_SIM;
            }
        }

        public String toString() {
            String ret = "";
            if (isAvailable()) {
                ret += "SIM Type: " + mSimType + "\n";
                ret += "Active state: " + mActiveState + "\n";
                ret += "IMEI1: " + mIMEI1 + "\n";
                ret += "IMEI2: " + mIMEI2 + "\n";
                ret += "IMSI1: " + mIMSI1 + "\n";
                ret += "IMSI2: " + mIMSI2 + "\n";
            }
            return ret;
        }
    }

}