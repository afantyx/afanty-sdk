package com.afanty.internal.internal;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.afanty.utils.ContextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppInfo {
    private static final String KEY_APP_TITLES = "app_titles";
    private static final String KEY_APP_PKG_NAME = "app_package_name";
    private static final String KEY_APP_VERSION_CODE = "app_version_code";
    private static final String KEY_EXIST_APP_VERSION_NAME = "exist_version_name";
    private static final String KEY_EXIST_APP_VERSION_CODE = "exist_version_code";

    private final List<String> mTitles = new ArrayList<>();
    private  String mPkgName = "";
    private  int mPkgVersion = 0;
    private String mExistVerName;
    private int mExistVerCode = 0;

    public AppInfo(String pkg,int ver){
        this.mPkgName = pkg;
        this.mPkgVersion = ver;
    }

    public AppInfo(JSONObject jsonObject) throws JSONException {
        mPkgName = jsonObject.optString(KEY_APP_PKG_NAME);
        mPkgVersion = jsonObject.optInt(KEY_APP_VERSION_CODE, 0);

        try {
            if (jsonObject.has(KEY_APP_TITLES) && !TextUtils.equals("null", jsonObject.optString(KEY_APP_TITLES))){
                JSONArray array = jsonObject.getJSONArray(KEY_APP_TITLES);
                for (int i = 0; i < array.length(); i++) {
                    mTitles.add(array.getString(i));
                }
            }
        } catch (Exception e) {
        }

        try {
            if (!TextUtils.isEmpty(mPkgName)) {
                PackageInfo pkgInfo = ContextUtils.getContext().getPackageManager().getPackageInfo(mPkgName, PackageManager.GET_CONFIGURATIONS);
                mExistVerName = pkgInfo.versionName;
                mExistVerCode = pkgInfo.versionCode;
            }
        } catch (Exception e) {
        }
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_APP_PKG_NAME, mPkgName);
        jsonObject.put(KEY_APP_VERSION_CODE, mPkgVersion);

        if (!TextUtils.isEmpty(mExistVerName))
            jsonObject.put(KEY_EXIST_APP_VERSION_NAME, mExistVerName);
        if (mExistVerCode != 0)
            jsonObject.put(KEY_EXIST_APP_VERSION_CODE, mExistVerCode);

        return jsonObject;
    }

    public List<String> getTitles() {
        return mTitles;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public int getPkgVersion() {
        return mPkgVersion;
    }
}
