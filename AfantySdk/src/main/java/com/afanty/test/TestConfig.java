package com.afanty.test;

import com.afanty.utils.ContextUtils;
import com.afanty.utils.SettingsSp;

public class TestConfig {
    private static final String TEST_KEY = "test_key";

    public static void setTestAppId(String testAppId){
        final SettingsSp settings = new SettingsSp(ContextUtils.getContext(), TEST_KEY);
        settings.set("test_id",testAppId);
    }

    public static String getTestAppId(){
        final SettingsSp settings = new SettingsSp(ContextUtils.getContext(), TEST_KEY);
        return settings.get("test_id","");
    }
}
