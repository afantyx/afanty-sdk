package com.afanty.internal.webview.jsinterface;

import android.content.Context;
import android.text.TextUtils;
import com.afanty.common.SDKLinkHelper;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.internal.webview.JsonUtils;
import com.afanty.utils.AppStarter;
import com.afanty.utils.PackageUtils;
import com.afanty.utils.SettingConfig;
import com.afanty.utils.SettingsSp;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ActionManager {
    public static final String ACTION_UNIFIED_DOWNLOADER = "unifiedDownloader";
    public static final String ACTION_GET_AD_PARAM = "getAdParam";
    public static final String ACTION_DOWNLOAD_STATUS = "downloadStatus";
    private static final String TAG = "Aft.ActionManager";
    private Context mContext;

    public ActionManager(Context context) {
        mContext = context;
    }

    public String findAndExec(String portal, String action, final String callbackName, final String jsonParam, final ResultBack resultBack) {
        String code = "";
        try {
            if (ACTION_GET_AD_PARAM.equals(action)) {
                return getAdParam(mContext);
            }

            if (TextUtils.isEmpty(jsonParam)) {
                return ResultBack.ERROR_PARAM;
            }

            JSONObject jsonObject = new JSONObject(jsonParam);
            if (jsonObject == null)
                return ResultBack.ERROR_PARAM;

            if (ACTION_DOWNLOAD_STATUS.equals(action)) {
                final String pkgName = jsonObject.optString("pkgName");
                if (!TextUtils.isEmpty(pkgName) && PackageUtils.isAppInstalled(mContext, pkgName)) {
                    JSONObject paramsObj = new JSONObject(jsonParam);
                    paramsObj.put("action", "installed");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                    SettingsSp settings = new SettingsSp(mContext);
                    settings.remove(pkgName);
                    return ResultBack.SUCCEED;
                }
                getDownloadStatus(jsonObject, callbackName, resultBack);
            } else if (ACTION_UNIFIED_DOWNLOADER.equals(action)) {
                invokeDownload(jsonObject, callbackName, resultBack);
            } else {
                code = ResultBack.NO_METHOD;
            }
        } catch (Exception e) {
            code = ResultBack.ERROR_EXCEPTION;
        }
        return code;
    }

    private void getDownloadStatus(JSONObject jsonObject, final String callbackName, final ResultBack resultBack) {
        final String pkgName = jsonObject.optString("pkgName");
        SDKLinkHelper.getDownloadStatus(jsonObject, callbackName, new ActionInterface.DownloadCallback() {
            SettingsSp settings = new SettingsSp(mContext);

            @Override
            public void onDownloadStart(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "start");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadProgress(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "progress");
                    settings.set(pkgName, jsonObject.toString());
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadFailed(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "failed");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadComplate(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "complete");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadPause(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "pause");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadResume(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "resume");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadDelete(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject jsonObject = new JSONObject(params);

                    if ("delete".equals(name)) {
                        jsonObject.put("action", "delete");
                    } else {
                        jsonObject.put("action", "download");
                    }
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            private Map<String, String> buildParams(String name, String url) {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("url", url);
                return params;
            }
        });
    }

    private void invokeDownload(JSONObject jsonParam, final String callbackName, final ResultBack resultBack) {
        final String pkgName = jsonParam.optString("pkgName");
        final SettingsSp settings = new SettingsSp(mContext);
        unifiedDownloader(mContext, jsonParam, new ActionInterface.DownloadCallback() {
            @Override
            public void onDownloadStart(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "start");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadProgress(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "progress");
                    settings.set(pkgName, jsonObject.toString());
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadFailed(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "failed");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadComplate(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject paramsObj = new JSONObject(params);
                    paramsObj.put("action", "complete");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, paramsObj.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadPause(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "pause");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadResume(String name, String url, long total, long completed) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    params.put("total", String.valueOf(total));
                    params.put("completed", String.valueOf(completed));
                    JSONObject jsonObject = new JSONObject(params);
                    jsonObject.put("action", "resume");
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onDownloadDelete(String name, String url) {
                try {
                    Map<String, String> params = buildParams(name, url);
                    JSONObject jsonObject = new JSONObject(params);

                    if ("delete".equals(name)) {
                        jsonObject.put("action", "delete");
                    } else {
                        jsonObject.put("action", "download");
                    }
                    if (resultBack != null) {
                        resultBack.onResult(callbackName, jsonObject.toString());
                    }
                } catch (Exception e) {
                }
            }

            private Map<String, String> buildParams(String name, String url) {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("url", url);
                return params;
            }
        });
    }

    private void unifiedDownloader(final Context context, JSONObject jsonParams, final ActionInterface.DownloadCallback callback) {
        try {
            if (jsonParams == null)
                return;

            final String downloadUrl = jsonParams.optString("url");
            String name = jsonParams.optString("name");
            long fileSize = jsonParams.optLong("fileSize");
            boolean autoStart = jsonParams.optBoolean("autoStart");
            final String pkgName = jsonParams.optString("pkgName");
            if (TextUtils.isEmpty(name)) {
                name = pkgName;
            }

            String adPortal = jsonParams.optString("business");
            String pid = jsonParams.optString("pid");
            String adId = jsonParams.optString("ad_id");
            String cid = jsonParams.optString("cid");
            String did = jsonParams.optString("did");
            String siParam = jsonParams.optString("siparam");

            String actionTypeStr = jsonParams.optString("actionType");
            int actionType = TextUtils.isEmpty(actionTypeStr) ? 0 : Integer.parseInt(actionTypeStr);
            String minVersionCodeStr = jsonParams.optString("minVersionCode");
            int minVersionCode = TextUtils.isEmpty(minVersionCodeStr) ? 0 : Integer.parseInt(minVersionCodeStr);
            String subPortal = jsonParams.optString("subPortal");
            String urlStrs = jsonParams.optString("trackUrls");
            String[] trackUrls = TextUtils.isEmpty(urlStrs) ? null : urlStrs.split(",");
            String versionCodeStr = jsonParams.optString("versionCode");
            int versionCode = TextUtils.isEmpty(versionCodeStr) ? 0 : Integer.parseInt(versionCodeStr);
            String versionName = jsonParams.optString("versionName");
            String gpUrl = jsonParams.optString("gpUrl");
            final int downloadMode = jsonParams.optInt("downloadMode");
            String url = TextUtils.isEmpty(gpUrl) ? downloadUrl : gpUrl;

            if (!TextUtils.isEmpty(pkgName) && PackageUtils.isAppInstalled(mContext, pkgName)) {
                AppStarter.startInstalledApp(mContext, adId, url, pkgName);
                return;
            }
            if (!TextUtils.isEmpty(url) && AdsUtils.isGPDetailUrl(url))
                AppStarter.startAppMarketWithUrl(mContext, url, pkgName, adId);
        } catch (Exception e) {
        }
    }

    private String getAdParam(Context context) {
        try {
            JSONObject jsonObject = JsonUtils.toJSONObject(ResultBack.SUCCEED);

            String result = getWebViewAdParam(context);
            jsonObject.put("result", result);
            return jsonObject.toString();
        } catch (Exception e) {
            return JsonUtils.toJSONObject(ResultBack.ERROR_EXCEPTION, e).toString();
        }
    }

    private String getWebViewAdParam(Context context) {
        return "";
    }
}
