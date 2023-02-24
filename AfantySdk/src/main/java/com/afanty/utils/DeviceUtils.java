package com.afanty.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.WindowManager;

import com.afanty.common.UserInfoHelper;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class DeviceUtils {
    private static final String KEY_DEVICE_ID = "DEVICE_ID";
    private static String mMacAddress = null;
    private static String mImei = null;
    private static String GAID = null;
    private static String mAndroidId = null;
    private static String SOC_HOST = "mmc_host";
    private static String SOC_SERIAL_PATH = "/mmc0/mmc0:0001/cid";

    public static String getOrCreateDeviceId(Context ctx) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        SettingsSp settings = new SettingsSp(ctx);
        String id = settings.get(KEY_DEVICE_ID);

        if (!TextUtils.isEmpty(id) && !isBadMacId(id) && !isBadAndroid(id))
            return id;

        IDType type = IDType.MAC;
        try {
            id = getMacAddress(ctx);
            if (TextUtils.isEmpty(id)) {
                type = IDType.ANDROID;
                id = getAndroidID(ctx);

                if (isBadAndroid(id))
                    id = null;
            }
            if (TextUtils.isEmpty(id)) {
                type = IDType.UUID;
                id = getUUID();
            }
        } catch (Exception e) {
            type = IDType.UUID;
            id = getUUID();
        }
        id = type.getTag() + "." + id;
        settings.set(KEY_DEVICE_ID, id);
        return id;
    }

    public static boolean isBadMacId(String id) {
        if (TextUtils.isEmpty(id))
            return false;
        return (IDType.MAC.getTag() + "." + "020000000000").equals(id);
    }

    public static boolean isBadAndroid(String id) {
        if (TextUtils.isEmpty(id))
            return false;
        return (IDType.ANDROID.getTag() + "." + "9774d56d682e549c").equalsIgnoreCase(id);
    }

    private static String getUUID() {
        long r = (long) (Math.random() * Long.MAX_VALUE);
        return new UUID(r, Build.FINGERPRINT.hashCode()).toString();
    }

    public static String getMacAddress(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        if (!TextUtils.isEmpty(mMacAddress))
            return mMacAddress;
        mMacAddress = SettingConfig.getMacAddressId();
        if (!TextUtils.isEmpty(mMacAddress))
            return mMacAddress;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null)
            return null;

        @SuppressLint("HardwareIds") String id = wifiInfo.getMacAddress();
        if (!TextUtils.isEmpty(id))
            id = id.replace(":", "");

        if (!TextUtils.isEmpty(id) && isBadMacId(IDType.MAC.getTag() + "." + id)) {
            id = getMacAddressByNetInterface();
            if (!TextUtils.isEmpty(id))
                id = id.replace(":", "");
        }

        mMacAddress = id;
        if (!TextUtils.isEmpty(mMacAddress))
            SettingConfig.setMacAddressId(mMacAddress);
        return mMacAddress;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static String getMacAddressByNetInterface() {
        try {
            Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
            if (enu == null)
                return "";

            while (enu.hasMoreElements()) {
                NetworkInterface networkInterface = enu.nextElement();
                String name = networkInterface.getName();
                if (TextUtils.isEmpty(name) || !StringUtils.toLowerCaseIgnoreLocale(name).contains("wlan"))
                    continue;

                byte[] addr = networkInterface.getHardwareAddress();
                StringBuilder buf = new StringBuilder();
                for (byte b : addr)
                    buf.append(String.format("%02X:", b));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Throwable e) {
        }

        return "";
    }

    public static String getIMEI(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        if (!TextUtils.isEmpty(mImei))
            return mImei;
        mImei = SettingConfig.getDeviceIMEI();
        if (!TextUtils.isEmpty(mImei))
            return mImei;

        IMSUtils.IMSInfo info = IMSUtils.getIMSInfo(context);
        if (info == null || !info.isAvailable())
            return null;
        mImei = info.getBetterIMEI();
        if (!TextUtils.isEmpty(mImei))
            SettingConfig.setDeviceIMEI(mImei);
        return mImei;
    }

    public static DEVICETYPE detectDeviceType(Context ctx) {
        double screenSize = 0D;
        try {
            DisplayMetrics displaymetrics = ctx.getApplicationContext().getResources().getDisplayMetrics();
            float width = displaymetrics.widthPixels;
            float height = displaymetrics.heightPixels;
            float xdpi = displaymetrics.densityDpi > displaymetrics.xdpi ? displaymetrics.densityDpi : displaymetrics.xdpi;
            float ydpi = displaymetrics.densityDpi > displaymetrics.ydpi ? displaymetrics.densityDpi : displaymetrics.ydpi;
            float inchW = width / xdpi;
            float inchH = height / ydpi;
            screenSize = Math.sqrt(Math.pow(inchW, 2D) + Math.pow(inchH, 2D));
        } catch (Exception exception) {
            return DEVICETYPE.DEVICE_PHONE;
        }
        if (screenSize >= 6.5D)
            return DEVICETYPE.DEVICE_PAD;
        return DEVICETYPE.DEVICE_PHONE;
    }

    public static String getGAID(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }

        if (!TextUtils.isEmpty(GAID))
            return GAID;

        AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        GAID = adInfo == null ? "" : adInfo.getId();

        return GAID;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }
        if (!TextUtils.isEmpty(mAndroidId))
            return mAndroidId;
        mAndroidId = SettingConfig.getAndroidId();
        if (!TextUtils.isEmpty(mAndroidId))
            return mAndroidId;

        String id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(id.trim()))
            return null;

        mAndroidId = id;
        if (!TextUtils.isEmpty(mAndroidId))
            SettingConfig.setAndroidId(mAndroidId);
        return mAndroidId;
    }

    public static String getTimeZoneDisplayName() {
        try {
            return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH);
        } catch (Throwable e) {
            return "";
        }
    }

    public static int getScreenWidthByWindow(Context context) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeightByWindow(Context context) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);
        return size.y;
    }

    public static String getSimCountryIso(Context ctx) {
        if (!UserInfoHelper.canCollectUserInfo()) {
            return "";
        }

        try {
            TelephonyManager telManager = (TelephonyManager) ctx.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return telManager.getSimCountryIso() + "";
        } catch (Exception e) {
            return "";
        }
    }

    public enum DEVICETYPE {
        DEVICE_PHONE("phone"), DEVICE_PAD("pad");

        private final static Map<String, DEVICETYPE> VALUES = new HashMap<String, DEVICETYPE>();

        static {
            for (DEVICETYPE item : DEVICETYPE.values())
                VALUES.put(item.mValue, item);
        }

        private String mValue;

        DEVICETYPE(String value) {
            mValue = value;
        }

        @SuppressLint("DefaultLocale")
        public static DEVICETYPE fromString(String value) {
            return VALUES.get(value.toLowerCase());
        }

        @Override
        public String toString() {
            return mValue;
        }
    }

    public enum IDType {
        IMEI('i'), SOC('s'), MAC('m'), UUID('u'), ANDROID('a'), BUILD('b'), UNKNOWN('u');

        private final static Map<Character, IDType> VALUES = new HashMap<Character, IDType>();

        static {
            for (IDType item : IDType.values())
                VALUES.put(item.mTag, item);
        }

        private char mTag;

        IDType(char tag) {
            mTag = tag;
        }

        public static IDType fromChar(char value) {
            IDType type = VALUES.get(value);
            return (type == null) ? IDType.UNKNOWN : type;
        }

        public char getTag() {
            return mTag;
        }

        public String getName() {
            switch (this) {
                case IMEI:
                    return "imei";
                case SOC:
                    return "soc";
                case MAC:
                    return "mac";
                case UUID:
                    return "uuid";
                case ANDROID:
                    return "android_id";
                case BUILD:
                    return "build";
                default:
                    return "unknown";
            }
        }
    }

    public static IDType parseIDType(String deviceId) {
        if (TextUtils.isEmpty(deviceId) || deviceId.indexOf(".") != 1)
            return IDType.UNKNOWN;

        char value = deviceId.charAt(0);
        return IDType.fromChar(value);
    }

    private static Pair<Integer, Integer> sResolution = null;

    public static Pair<Integer, Integer> getResolution(Context context) {
        if (sResolution == null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null && wm.getDefaultDisplay() != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(metrics);
                sResolution = new Pair<Integer, Integer>(metrics.widthPixels, metrics.heightPixels);
            }
        }
        return sResolution;
    }

    private static IMSUtils.IMSInfo mImsInfo = null;

    public static int supportSimCount(Context context) {
        if(!UserInfoHelper.canCollectUserInfo()){
            return -2;
        }
        if (mImsInfo == null)
            mImsInfo = IMSUtils.getIMSInfo(context);
        if (mImsInfo == null) {
            return -2;
        }
        return mImsInfo.mSimType == IMSUtils.SimType.DUAL_SIM ? 2 : (mImsInfo.mSimType == IMSUtils.SimType.SINGLE_SIM ? 1 : (mImsInfo.mSimType == IMSUtils.SimType.NO_SIM ? 0 : -1));
    }

    public static int activeSimCount(Context context) {
        if(!UserInfoHelper.canCollectUserInfo()){
            return -2;
        }
        if (mImsInfo == null)
            mImsInfo = IMSUtils.getIMSInfo(context);
        if (mImsInfo == null) {
            return -2;
        }
        return mImsInfo.mActiveState == IMSUtils.ActiveState.DOUBLE_ACTIVE ? 2 : (mImsInfo.mActiveState == IMSUtils.ActiveState.SINGLE_ACTIVE ? 1 : (mImsInfo.mActiveState == IMSUtils.ActiveState.NO_ACTIVE ? 0 : -1));
    }

    public static List<String> getIMSIs(Context context) {
        if (mImsInfo == null)
            mImsInfo = IMSUtils.getIMSInfo(context);
        if (mImsInfo == null) {
            return new ArrayList<String>();
        }
        return mImsInfo.getIMSIList();
    }

    private static String mStorageCID = null;

    public static String getStorageCID() {
        if (!TextUtils.isEmpty(mStorageCID))
            return mStorageCID;
        mStorageCID = SettingConfig.getStorageCid();
        if (!TextUtils.isEmpty(mStorageCID))
            return mStorageCID;

        File file = getCIDSerialFile();
        if (file == null)
            return null;

        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(file);
            byte[] buffer = new byte[128];
            int length = fis.read(buffer, 0, 128);
            String sn = new String(buffer, 0, length);
            if (sn.length() >= 32 && !sn.contains("00000000000000000000")) {
                char[] arr = StringUtils.toUpperCaseIgnoreLocale(sn.trim()).toCharArray();
                StringBuilder sb = new StringBuilder();
                sb.append(arr, 0, 6);
                sb.append(arr, 16, 10);
                mStorageCID = sb.toString();
                if (!TextUtils.isEmpty(mStorageCID))
                    SettingConfig.setStorageCid(mStorageCID);
                return mStorageCID;
            }
        } catch (Exception e) {
        } finally {
            CommonUtils.close(fis);
        }
        return null;
    }

    private static File findCIDSerialFile(File dir) {
        if (dir.getName().equals(SOC_HOST)) {
            File f = new File(dir.getAbsolutePath() + SOC_SERIAL_PATH);
            if (f.exists() && f.canRead())
                return f;
        }
        return null;
    }

    private static File getCIDSerialFile() {
        File[] dirs = new File("/sys/devices").listFiles();
        if (dirs == null)
            return null;

        for (File dir : dirs) {
            if (dir.isFile())
                continue;
            File ret = findCIDSerialFile(dir);
            if (ret != null)
                return ret;

            File[] ds1 = dir.listFiles();
            if (ds1 == null)
                continue;

            for (File d1 : ds1) {
                if (d1.isFile())
                    continue;
                if ((ret = findCIDSerialFile(d1)) != null)
                    return ret;

                File[] ds2 = d1.listFiles();
                if (ds2 == null)
                    continue;

                for (File d2 : ds2)
                    if ((ret = findCIDSerialFile(d2)) != null)
                        return ret;
            }
        }
        return null;
    }

    private static String mBuildSN = null;

    public static String getBuildSN() {
        if (!TextUtils.isEmpty(mBuildSN))
            return mBuildSN;
        mBuildSN = SettingConfig.getBuildSn();
        if (!TextUtils.isEmpty(mBuildSN))
            return mBuildSN;

        Class<Build> c = Build.class;
        try {
            java.lang.reflect.Field f = c.getDeclaredField("SERIAL");
            mBuildSN = (String) f.get(c);
            if (!TextUtils.isEmpty(mBuildSN))
                SettingConfig.setBuildSn(mBuildSN);
            return mBuildSN;
        } catch (Exception e) {
        }
        return null;
    }

    private static String mDeviceId;

    public static String getDeviceId(Context ctx) {
        if (TextUtils.isEmpty(mDeviceId)) {
            mDeviceId = getOrCreateDeviceId(ctx);
        }
        return mDeviceId;
    }

    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return formatMemoryInfo(mi.availMem / 1024);
    }

    public static String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            initial_memory = Long.parseLong(arrayOfString[1]);
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return formatMemoryInfo(initial_memory);
    }

    private static String formatMemoryInfo(long size) {
        String memorySize = String.valueOf(size / 1024);
        return memorySize + "MB";
    }

    private static long sTotalMemory = -1;

    public static long getTotalMem() {
        if (sTotalMemory == -1)
            sTotalMemory = readProcMeminfo();
        return sTotalMemory;
    }

    private static long readProcMeminfo() {
        long total = 0;
        FileReader fReader = null;
        BufferedReader bReader = null;
        try {
            fReader = new FileReader("/proc/meminfo");
            bReader = new BufferedReader(fReader);
            String text = bReader.readLine();
            if (!TextUtils.isEmpty(text)) {
                String[] array = text.split("\\s+");
                total = Long.valueOf(array[1]) / 1024;
            }
        } catch (Exception e) {
        } finally {
            CommonUtils.close(bReader);
            CommonUtils.close(fReader);
        }
        return total;
    }

    public static boolean isSimReady(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static boolean setMobileDataEnabled(Context context, boolean enabled) {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Object cmService = Reflector.getFieldValue(cm, "mService");
            if (cmService != null) {
                Reflector.invokeMethod(cmService, "setMobileDataEnabled", new Class[]{Boolean.TYPE}, new Object[]{enabled});
                result = true;
            }
        } catch (Exception e) {
        }
        if (!result) {
            try {
                Reflector.invokeMethod(cm, "setMobileDataEnabled", new Class[]{Boolean.TYPE}, new Object[]{enabled});
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     */
    public static boolean isCPUInfo64() {
        if (Build.VERSION.SDK_INT >= 21) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.contains("64"))
                    return true;
            }
        } else if (Build.CPU_ABI.contains("64")) {
            return true;
        }
        return false;
    }

    public static JSONArray getCpuAbiArray() {
        JSONArray abiArray = new JSONArray();
        if (Build.VERSION.SDK_INT >= 21) {
            for (String abi : Build.SUPPORTED_ABIS)
                abiArray.put(abi);
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI))
                abiArray.put(Build.CPU_ABI);
            if (!TextUtils.isEmpty(Build.CPU_ABI2))
                abiArray.put(Build.CPU_ABI2);
        }
        return abiArray;
    }

    public static boolean isLockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) ContextUtils.getContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            return keyguardManager.inKeyguardRestrictedInputMode();
        }
        return false;
    }

    /**
     * PACKAGE_USAGE_STATS权限检测
     *
     * @return
     */
    public static boolean isUsageStatsPermissionGranted() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return false;
        }
        boolean granted = false;
        try {
            AppOpsManager appOpsManager = (AppOpsManager) ContextUtils.getContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), ContextUtils.getContext().getPackageName());
            if (mode == AppOpsManager.MODE_DEFAULT) {
                if (ContextUtils.getContext().checkCallingPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                }
            } else {
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    granted = true;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return granted;
    }

    /**
     * Overlay权限检测
     *
     * @return
     */
    public static boolean checkOverlayPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(ContextUtils.getContext());
    }

    /**
     * 获取应用安装时间
     */
    public static long getInstalledPackageTime(String packageName) {
        try {
            PackageInfo packageInfo = ContextUtils.getContext().getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                return packageInfo.firstInstallTime;
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取最后更新时间
     */
    public static long getLastUpdateTime(String packageName) {
        try {
            PackageInfo packageInfo = ContextUtils.getContext().getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                return packageInfo.lastUpdateTime;
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }

    public static List<String> listWidgetIds(Context context) {
        AppWidgetManager wm = AppWidgetManager.getInstance(context);
        List<AppWidgetProviderInfo> widgets = wm.getInstalledProviders();
        List<String> widgetIds = new ArrayList<String>();
        for (AppWidgetProviderInfo widget : widgets)
            widgetIds.add(widget.provider.getPackageName());
        return widgetIds;
    }

}
