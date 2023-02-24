package com.afanty.common.download;

import android.text.TextUtils;

import com.afanty.utils.ContextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Downloader {

    private String getFileNameByUrl(boolean isTemp,String url){
        String fileName = "";
        if (isTemp) {
            fileName = url.hashCode() + ".temp";
        } else {
            String suffix = url.substring(url.lastIndexOf("."));
            fileName = url.hashCode() + suffix;
        }
        return fileName;
    }

    public String getCachePath(String url){
        if (TextUtils.isEmpty(url))
            return "";
        return ContextUtils.getContext().
                getExternalFilesDir("./aft")+File.separator+getFileNameByUrl(false,url);
    }

    public File getCache(String url){
        String path = getCachePath(url);
        if (TextUtils.isEmpty(path))
            return null;
        return new File(path);
    }

    private String getTmpCachePath(String url){
        if (TextUtils.isEmpty(url))
            return "";
        return ContextUtils.getContext().
                getExternalFilesDir("./aft")+File.separator+getFileNameByUrl(true,url);
    }

    public void downnload(String downloadUrl,final DownloadListener listener){

        if (TextUtils.isEmpty(downloadUrl)) {
            if (listener!=null)
                listener.onFail(downloadUrl,"url empty");
        }
        if (listener!=null)
            listener.onStart(downloadUrl);
        String targetPath =getCachePath(downloadUrl);
        File file = new File(targetPath);
        if (file.exists()) {
            if (listener!=null)
                listener.onSuccess(downloadUrl,true);
            return;
        }

        String tmpPath = getTmpCachePath(downloadUrl);
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File tmpFile = new File(tmpPath);
        long downloadLength = 0;  //记录已经下载的文件长度
        long contentLength = getContentLength(downloadUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength) //断点续传要用到的，指示下载的区间
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(tmpPath, "rw");
                savedFile.seek(downloadLength);//跳过已经下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    total += len;
                    savedFile.write(b, 0, len);
                }
                response.body().close();
                if (tmpFile.exists()){
                    tmpFile.renameTo(file);
                    if (file.exists()) {
                        if (listener!=null)
                            listener.onSuccess(downloadUrl, false);
                        return;
                    }
                }
                if (listener!=null)
                    listener.onFail(downloadUrl,"file exception");
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener!=null)
                listener.onFail(downloadUrl,e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long getContentLength(String downloadUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.body().close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
