package com.x.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.utils.SettingsSp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingConfig {
    private static SettingsSp settingsSp = null;
    private static final String KEYS = "test_kyes";
    private static List<String> keyList = new ArrayList<>();

    public static List<String> getKeys(){
        return keyList;
    }

    public static String getValue(String key){
        return settingsSp.get(key);
    }

    public static void setValue(String key,String value){
        settingsSp.set(key,value);
    }

    public static void init(Context context) {
    }



}
