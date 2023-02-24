package com.afanty.request;

public class OutParamsHelper {

    private static CustomBidRequest.DeviceInfo mDeviceInfo;
    private static CustomBidRequest.App mAppInfo;

    public static void setDeviceInfo(CustomBidRequest.DeviceInfo deviceInfo){
        mDeviceInfo = deviceInfo;
    }

    public static void setAppInfo(CustomBidRequest.App appInfo){
        mAppInfo = appInfo;
    }

    public static String getPackageName(){
        return mAppInfo!=null?mAppInfo.getPackageName():"";
    }
    public static String getVerName(){
        return mAppInfo!=null?mAppInfo.getVerName():"";
    }
    public static Integer getVerCode(){
        return mAppInfo!=null?mAppInfo.getVerCode():null;
    }
    public static String getPublisherName(){
        return mAppInfo!=null?mAppInfo.getPublisher():"";
    }

    public static String getUA(){
        return mDeviceInfo!=null?mDeviceInfo.getUA():"";
    }
    public static Integer getW(){
        return mDeviceInfo!=null?mDeviceInfo.getW():null;
    }
    public static Integer getH(){
        return mDeviceInfo!=null?mDeviceInfo.getH():null;
    }
    public static Integer getPPI(){
        return mDeviceInfo!=null?mDeviceInfo.getPPI():null;
    }
    public static String getLangeUage(){
        return mDeviceInfo!=null?mDeviceInfo.getLanguage():"";
    }
    public static String getGAID(){
        return mDeviceInfo!=null?mDeviceInfo.getGAID():null;
    }
    public static String getImei(){
        return mDeviceInfo!=null?mDeviceInfo.getImei():null;
    }
    public static String getMacAdress(){
        return mDeviceInfo!=null?mDeviceInfo.getMacAdress():null;
    }
    public static String getCPUBit(){
        return mDeviceInfo!=null?mDeviceInfo.getCPUBit():null;
    }
    public static String getABI(){
        return mDeviceInfo!=null?mDeviceInfo.getABI():null;
    }

    public static String getAppName(){
        return mAppInfo!=null?mAppInfo.getAppName():null;
    }
}
