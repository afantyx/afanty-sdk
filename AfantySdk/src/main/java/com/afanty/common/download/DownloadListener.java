package com.afanty.common.download;

public interface DownloadListener {

   void onStart(String url);
   void onFail(String url,String reason);
   void onSuccess(String url,boolean iscache);

}
