package com.afanty.internal.net.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.afanty.common.UserInfoHelper;
import com.afanty.common.lang.DynamicValue;
import com.afanty.internal.net.change.ChangeListenerManager;
import com.afanty.internal.net.change.ChangedKeys;
import com.afanty.internal.net.change.ChangedListener;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.IMSUtils;

public class NetworkStatus {
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /**
     * Current network is GSM
     */
    public static final int NETWORK_TYPE_GSM = 16;
    /**
     * Current network is TD_SCDMA
     */
    public static final int NETWORK_TYPE_TD_SCDMA = 17;
    /**
     * Current network is IWLAN
     */
    public static final int NETWORK_TYPE_IWLAN = 18;
    /**
     * Current network is LTE_CA
     */
    public static final int NETWORK_TYPE_LTE_CA = 19;
    public static final int NET_UNKNOWN = -1001;
    private static final String TAG = "NetworkStatus";
    private static AftHotInterface mAftHotInterface;
    private static DynamicValue sNetworkStatus;
    private static final ChangedListener mOnNetworkChangedListener = new ChangedListener() {
        @Override
        public void onListenerChange(String key, Object value) {
            if (sNetworkStatus != null)
                sNetworkStatus.updateValue(NetworkStatus.getNetworkStatus(ContextUtils.getContext()));
        }
    };
    private NetType mNetType;
    private String mNetTypeDetail;
    private MobileDataType mMobileDataType;
    private int mMobileNet = NET_UNKNOWN;
    private String mCarrier;
    private String mName;
    private String mNumeric;
    private Boolean mIsWifiHot = false;
    private boolean mIsConnected = true;

    public NetworkStatus(NetType netType, MobileDataType mobileDataType, String carrier, String name, String numeric) {
        mNetType = netType;
        mMobileDataType = mobileDataType;
        mCarrier = carrier;
        mName = name;
        mNumeric = numeric;
    }

    public static void setAftHotInterface(AftHotInterface aftHotInterface) {
        mAftHotInterface = aftHotInterface;
    }

    public static NetworkStatus getNetworkStatusEx(Context context) {
        if (sNetworkStatus == null) {
            ChangeListenerManager.getInstance().unregisterChangedListener(ChangedKeys.KEY_CONNECTIVITY_CHANGE, mOnNetworkChangedListener);
            sNetworkStatus = new DynamicValue(NetworkStatus.getNetworkStatus(context), true, 1000);
            ChangeListenerManager.getInstance().registerChangedListener(ChangedKeys.KEY_CONNECTIVITY_CHANGE, mOnNetworkChangedListener);
        } else if (sNetworkStatus.isNeedUpdate())
            sNetworkStatus.updateValue(NetworkStatus.getNetworkStatus(context));

        return (NetworkStatus) sNetworkStatus.getObjectValue();
    }

    public static NetworkStatus getNetworkStatus(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkStatus status = new NetworkStatus(NetType.OFFLINE, MobileDataType.UNKNOWN, null, null, null);
        if (telManager == null || connManager == null) {
            status.mNetTypeDetail = getNetTypeDetail(status);
            return status;
        }

        status.mCarrier = telManager.getSimOperatorName();
        status.mNumeric = telManager.getSimOperator();
        if (status.mCarrier == null || status.mCarrier.length() <= 0 || status.mCarrier.equals("null"))
            status.mCarrier = IMSUtils.getQualcommCardName();

        NetworkInfo networkInfo = null;
        try {
            networkInfo = connManager.getActiveNetworkInfo();
        } catch (Exception e) {
        }
        if (networkInfo == null || !networkInfo.isAvailable()) {
            status.mNetTypeDetail = getNetTypeDetail(status);
            return status;
        }

        int netType = networkInfo.getType();
        status.mIsConnected = networkInfo.isConnected();
        if (netType == ConnectivityManager.TYPE_MOBILE) {
            status.mNetType = NetType.MOBILE;
            int netSubtype = getNetworkType(telManager);
            status.mMobileNet = netSubtype;
            status.mMobileDataType = getNetworkClass(netSubtype);
        }
        else if (netType == ConnectivityManager.TYPE_WIFI) {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null) {
                    String ssid = wi.getSSID();
                    status.mName = (ssid != null && ssid.length() > 0) ? ssid : null;
                    String ipAddress = intIP2StringIP(wi.getIpAddress());
                    if (mAftHotInterface != null && ssid != null)
                        status.mIsWifiHot = mAftHotInterface.isAftHot(ipAddress, ssid.replace("\"", ""));
                }
            }
            status.mNetType = NetType.WIFI;
        } else
            status.mNetType = NetType.UNKNOWN;
        status.mNetTypeDetail = getNetTypeDetail(status);

        return status;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static NetType getNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null)
            return NetType.OFFLINE;

        NetworkInfo networkInfo = null;
        try {
            networkInfo = connManager.getActiveNetworkInfo();
        } catch (Exception e) {
        }
        if (networkInfo == null || !networkInfo.isAvailable())
            return NetType.OFFLINE;

        int netType = networkInfo.getType();
        return (netType == ConnectivityManager.TYPE_MOBILE) ? NetType.MOBILE : (netType == ConnectivityManager.TYPE_WIFI ? NetType.WIFI : NetType.UNKNOWN);
    }

    private static String getNetTypeDetail(NetworkStatus status) {
        switch (status.getNetType()) {
            case OFFLINE:
                return "OFFLINE";
            case WIFI:
                return status.mIsWifiHot ? "WIFI_HOT" : "WIFI";
            case MOBILE:
                switch (status.mMobileDataType) {
                    case MOBILE_2G:
                        return "MOBILE_2G";
                    case MOBILE_3G:
                        return "MOBILE_3G";
                    case MOBILE_4G:
                        return "MOBILE_4G";
                    default:
                        return "MOBILE_UNKNOWN";
                }
            default:
                return "UNKNOWN";
        }
    }

    public static MobileDataType getNetworkClass(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return MobileDataType.MOBILE_2G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:
                return MobileDataType.MOBILE_3G;
            case NETWORK_TYPE_LTE:
            case NETWORK_TYPE_IWLAN:
            case NETWORK_TYPE_LTE_CA:
                return MobileDataType.MOBILE_4G;
            default:
                return MobileDataType.UNKNOWN;
        }
    }

    public NetType getNetType() {
        return mNetType;
    }

    public int getMobileNet() {
        if (mNetType == NetType.MOBILE)
            return mMobileNet;
        return NET_UNKNOWN;
    }

    public String getNetTypeDetail() {
        return mNetTypeDetail;
    }

    public String getNetTypeDetailForStats() {
        if (NetType.OFFLINE.equals(mNetType))
            return mNetTypeDetail;
        String isConnected;
        if (mIsConnected)
            isConnected = "_CONNECT";
        else
            isConnected = "_OFFLINE";
        return mNetTypeDetail + isConnected;
    }

    public boolean isIsConnected() {
        return mIsConnected;
    }

    public MobileDataType getMobileDataType() {
        return mMobileDataType;
    }

    public String getCarrier() {
        return mCarrier;
    }

    public String getNumeric() {
        return mNumeric;
    }

    public static String getImsi(Context context){
        if(!UserInfoHelper.canCollectUserInfo()){
            return "";
        }
        return getNetworkStatus(context).getNumeric();
    }

    @SuppressLint("MissingPermission")
    private static int getNetworkType(TelephonyManager telManager) {
        if (Build.VERSION.SDK_INT < 30) {
            return telManager.getNetworkType();
        } else {
            try {
                return telManager.getDataNetworkType();
            } catch (Exception e) {
                return NETWORK_TYPE_UNKNOWN;
            }
        }
    }

    public enum NetType {
        UNKNOWN(0), OFFLINE(1), WIFI(2), MOBILE(3);

        private final static SparseArray<NetType> VALUES = new SparseArray<NetType>();

        static {
            for (NetType item : NetType.values())
                VALUES.put(item.mValue, item);
        }

        private final int mValue;

        NetType(int value) {
            mValue = value;
        }

        public static NetType fromInt(int value) {
            return VALUES.get(value);
        }

        public int getValue() {
            return mValue;
        }
    }

    public enum MobileDataType {
        UNKNOWN(0), MOBILE_2G(1), MOBILE_3G(2), MOBILE_4G(3);

        private final static SparseArray<MobileDataType> VALUES = new SparseArray<MobileDataType>();

        static {
            for (MobileDataType item : MobileDataType.values())
                VALUES.put(item.mValue, item);
        }

        private final int mValue;

        MobileDataType(int value) {
            mValue = value;
        }

        public static MobileDataType fromInt(int value) {
            return VALUES.get(value);
        }

        public int getValue() {
            return mValue;
        }
    }

    public interface AftHotInterface {
        boolean isAftHot(String ipAddress, String ssid);
    }

}
