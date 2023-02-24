package com.afanty.internal.webview.jsinterface;

public interface ActionInterface {

    interface DownloadCallback {
        void onDownloadStart(String name, String url);

        void onDownloadProgress(String name, String url, long total, long completed);

        void onDownloadFailed(String name, String url);

        void onDownloadComplate(String name, String url);

        void onDownloadPause(String name, String url, long total, long completed);

        void onDownloadResume(String name, String url, long total, long completed);

        void onDownloadDelete(String name, String url);
    }
}
