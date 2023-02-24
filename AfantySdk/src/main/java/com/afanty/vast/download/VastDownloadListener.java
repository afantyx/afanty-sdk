package com.afanty.vast.download;

public interface VastDownloadListener {
    void onDownloadFailed(String url, VastDownloadError error);

    void onDownloadSuccess(String url, String localPath, long size);

    void onDownloading(String url, int progress);
}
