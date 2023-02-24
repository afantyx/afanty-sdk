package com.afanty.utils.location;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.afanty.common.UserInfoHelper;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.SettingConfig;
import com.afanty.utils.SettingsSp;

public class LocationUtils {
    private static final String LOCATION_REFRESH_INTERVAL_CONFIG = "location_refresh_interval";
    @Nullable
    public static String getDeviceIP(Context context) {
        String testLocation = getTestLocation();
        if (!TextUtils.isEmpty(testLocation))
            return testLocation;
        if (!UserInfoHelper.canCollectUserInfo()) {
            return null;
        }
        return null;
    }

    private static String getTestLocation() {
        SettingsSp settingsSp = new SettingsSp(ContextUtils.getContext());
        return settingsSp.get("testIp");
    }

    public static void setTestIPByCode(String countryCode) {
        if (TextUtils.equals(countryCode, "NUL")) {
            setTestIP("");
            return;
        }
        CountryCode country = CountryCode.getCountryCode(countryCode);
        if (country == null) {
            Log.w("AFT", "countryCode not found, pls use #setTestLocation(double lat, double lng)");
            return;
        }
        setTestIP(country.ip);
    }

    public static void setTestIP(String ip) {
        SettingsSp settingsSp = new SettingsSp(ContextUtils.getContext());
        settingsSp.set("testIp",ip);
    }
}
