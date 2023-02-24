package com.afanty.common;

import com.afanty.internal.webview.jsinterface.ActionInterface;

import org.json.JSONObject;

public class SDKLinkHelper {
    private static final String TAG = "SDKLinkHelper";
    private volatile static ILinkListener actionTypeListener;

    public static void getDownloadStatus(JSONObject jsonObject, String callbackName, ActionInterface.DownloadCallback downloadCallback) {
        getLinkListener().getDownloadStatus(jsonObject, callbackName, downloadCallback);
    }

    public static void startDownloadWakeUp() {
        getLinkListener().startDownloadWakeUp();
    }

    public static void setActionTypeListener(ILinkListener listener) {
        actionTypeListener = listener;
    }

    public static ILinkListener getLinkListener() {
        if (actionTypeListener == null) {
            actionTypeListener = new ActionTypeInnerImpl();
        }
        return actionTypeListener;
    }

    public interface ILinkListener {
        void startDownloadWakeUp();

        void getDownloadStatus(JSONObject jsonObject, String callbackName, ActionInterface.DownloadCallback downloadCallback);
    }

    private static class ActionTypeInnerImpl implements ILinkListener {

        @Override
        public void startDownloadWakeUp() {
        }

        @Override
        public void getDownloadStatus(JSONObject jsonObject, String callbackName, ActionInterface.DownloadCallback resultBack) {
        }
    }
}
