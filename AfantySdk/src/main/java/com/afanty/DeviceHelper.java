package com.afanty;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.afanty.request.OutParamsHelper;
import com.afanty.request.models.Device;
import com.afanty.request.models.Geo;
import com.afanty.utils.CommonUtils;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.DeviceUtils;
import com.afanty.utils.location.LocationUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class DeviceHelper {

    private static int width = 0;
    private static int height = 0;
    private static String ua = "";
    private static Resources resources;
    private static String mImeiSha1 = "";
    private static String mImeiMd5 = "";


    public static Device createDevice(Context context,int supportJs){
        Device device = new Device();
        device.ua= !TextUtils.isEmpty(OutParamsHelper.getUA())?OutParamsHelper.getUA():CommonUtils.getWebViewUA();
        device.geo = createGeo(context);
        device.dnt = 0;
        device.lmt = 1;
        device.ip = TextUtils.isEmpty(LocationUtils.getDeviceIP(context)) ? "154.140.49.51" : LocationUtils.getDeviceIP(context);//MY
        device.ipv6 = null;
        device.devicetype = 4;
        device.make = android.os.Build.MANUFACTURER;
        device.model = android.os.Build.MODEL;
        device.os = "android";
        device.osv = Build.VERSION.SDK_INT+"";
        device.hwv = System.getProperty("ro.boot.hardware.revision");
        if (resources == null)
            resources = ContextUtils.getContext().getResources();
        device.h = OutParamsHelper.getH()!=null?OutParamsHelper.getH():resources.getDisplayMetrics().heightPixels;
        device.w = OutParamsHelper.getW()!=null?OutParamsHelper.getW():resources.getDisplayMetrics().widthPixels;
        device.ppi = OutParamsHelper.getPPI()!=null?OutParamsHelper.getPPI():resources.getDisplayMetrics().densityDpi;
        device.pxratio = null;
        device.js= supportJs;//0不支持 ，1 支持
        device.geofetch = null;
        device.flashver = null;
        device.language = !TextUtils.isEmpty(OutParamsHelper.getLangeUage())?OutParamsHelper.getLangeUage():Locale.getDefault().getLanguage();
        device.carrier = null;
        device.mccmnc = mccmnc(context);
        device.connectiontype = 2;
        device.ifa = !TextUtils.isEmpty(OutParamsHelper.getGAID())?OutParamsHelper.getGAID():DeviceUtils.getGAID(context);
        if (TextUtils.isEmpty(mImeiSha1))
            mImeiSha1 = SHA1(!TextUtils.isEmpty(OutParamsHelper.getImei())?OutParamsHelper.getImei():DeviceUtils.getIMEI(context));
        device.didsha1 = mImeiSha1;
        if (TextUtils.isEmpty(mImeiMd5))
            mImeiMd5 = strMd5(!TextUtils.isEmpty(OutParamsHelper.getImei())?OutParamsHelper.getImei():DeviceUtils.getIMEI(context));
        device.didmd5 = mImeiMd5;
        device.dpidsha1 = null;
        device.macsha1 = SHA1(!TextUtils.isEmpty(OutParamsHelper.getMacAdress())?OutParamsHelper.getMacAdress():DeviceUtils.getMacAddress(context));
        device.macmd5 = strMd5(!TextUtils.isEmpty(OutParamsHelper.getMacAdress())?OutParamsHelper.getMacAdress():DeviceUtils.getMacAddress(context));
        device.c_bit = !TextUtils.isEmpty(OutParamsHelper.getCPUBit())?OutParamsHelper.getCPUBit():getDeviceByte();
        device.abi = !TextUtils.isEmpty(OutParamsHelper.getABI())?OutParamsHelper.getABI():Build.CPU_ABI;
        device.ext = null;
        return device;
    }

    public static String getDeviceByte() {
        if (Build.CPU_ABI.contains("64")) {
            return "64";
        } else {
            return "32";
        }
    }

    private static String mccmnc(Context context) throws SecurityException{
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String networkOperator = telManager.getNetworkOperator();

            int mcc = 0;
            int mnc = 0;
            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = Integer.parseInt(networkOperator.substring(0, 3));
                mnc = Integer.parseInt(networkOperator.substring(3));
            }
            if (mcc>0 && mnc >0)
                return mcc+"-"+mnc;
        return null;
    }

    private static boolean hasPermission(Context context,String perm){
        return ContextCompat.checkSelfPermission(context, perm) ==  PackageManager.PERMISSION_GRANTED;
    }


    private static String getLocalIPAddress (Context context){
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int ipAddressInt = wifiManager.getDhcpInfo().netmask;
            byte[] ipAddress = BigInteger.valueOf(ipAddressInt).toByteArray();
            InetAddress myaddr = InetAddress.getByAddress(ipAddress);
            String hostaddr = myaddr.getHostAddress();
            return hostaddr;
        }catch (UnknownHostException e){}
        return "";
    }


    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text){
        try {
            if (TextUtils.isEmpty(text))
                return null;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = text.getBytes("iso-8859-1");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        }catch (NoSuchAlgorithmException ignore){
        }catch (UnsupportedEncodingException ignore){
        }
        return null;
    }

    private static String strMd5(String param) {
        if (TextUtils.isEmpty(param)) {
            return null;
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(param.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Geo createGeo(Context context){
        Geo geo = new Geo();
        if (geo.type==null)
            geo.type = 2;
        geo.lastfix = null;
        geo.ipservice = null;
        // ISO-3166-1-alpha-3标准
//        geo.country = Locale.getDefault().getCountry();
        // ISO-3166-1-alpha-2标准
        geo.region = Locale.getDefault().getCountry();
        geo.regionfips104 = null;
        geo.metro = null;
        geo.city = null;
        geo.zip = null;
        geo.utcoffset = null;
        geo.ext = null;
        return geo;
    }
}
