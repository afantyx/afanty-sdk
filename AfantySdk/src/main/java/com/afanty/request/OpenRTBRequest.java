package com.afanty.request;

import android.os.Looper;
import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.internal.host.AfantyHosts;
import com.afanty.internal.internal.AdConstants;
import com.afanty.internal.internal.AdRequestListener;
import com.afanty.internal.net.http.UrlResponse;
import com.afanty.request.headers.OpenRTBHeaders;
import com.afanty.request.models.App;
import com.afanty.request.models.Device;
import com.afanty.request.models.Imp;
import com.afanty.request.models.Regs;
import com.afanty.request.models.Site;
import com.afanty.request.models.Source;
import com.afanty.request.models.User;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.HttpUtils;
import com.afanty.utils.NetworkUtils;
import com.afanty.utils.log.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class OpenRTBRequest {

    private static final String TAG = "OpenRTBRequest";
    private BidRequest mBidRequest;
    private static final int mHostConnectTimeout = 30 * 1000;
    private static final int mHostReadTimeout = 60 * 1000;

    public OpenRTBRequest(Builder builder) {
        mBidRequest = builder.mBidRequest;
    }

    public void loadAd(final OpenRTBReqListener listener) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            ThreadManager.getInstance().run(new DelayRunnableWork() {
                @Override
                public void execute() {
                    loadAdData(listener);
                }
            }, ThreadManager.TYPE_NETWORK_REQUEST);
        } else {
            loadAdData(listener);
        }
    }

    public String loadAdData(OpenRTBReqListener listener){
        if (!NetworkUtils.isConnected(ContextUtils.getContext())) {
            if (listener != null) {
                listener.onRequestError(AdRequestListener.NETWORK, "Network not connected...");
                Logger.d(TAG, "#LoadAdData Failed, Network not connected...");
            }
            return null;
        }
        Map<String, String> headers = OpenRTBHeaders.headers();
        String postData = mBidRequest.toString();
        String hostUrl = AfantyHosts.getBidHostUrl()+"/sunlight/v1";
        Logger.d(TAG, "#LoadAdData url:" + hostUrl);
        Logger.d(TAG, "#LoadAdData postData:" + postData);
        if (TextUtils.isEmpty(postData)) {
            Logger.d(TAG, "#LoadAdData Failed, postData is null");
            if (listener != null)
                listener.onRequestError(AdRequestListener.BUILD, "request body error");
            return null;
        }

        UrlResponse response;
        try {
            response = HttpUtils.okPostData(AdConstants.PortalKey.GET_AD, hostUrl, headers, postData.getBytes(), mHostConnectTimeout, mHostReadTimeout);
        } catch (IOException e) {
            Logger.d(TAG, "#LoadAdData error : " + e.getMessage());
            if (listener != null)
                listener.onRequestError(AdRequestListener.NETWORK, e.getMessage());
            return null;
        }

        if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
            Logger.d(TAG, "#LoadAdData Failed, StatusCode : " + response.getStatusCode());
            if (listener != null)
                listener.onRequestError(AdRequestListener.SERVER, "error status code, code =" + response.getStatusCode());
            return null;
        }

        String jsonStr = response.getContent();
        Logger.d(TAG,"#LoadAdResponse:"+jsonStr);
        if (TextUtils.isEmpty(jsonStr)) {
            Logger.d(TAG, "#LoadAdData Failed ,response content is null");
            if (listener != null)
                listener.onRequestError(AdRequestListener.SERVER, "response content is null");
            return null;
        }
        Logger.i(TAG, "#LoadAdData success.");
        listener.onRequestSuccess(jsonStr);
        return jsonStr;
    }

    public static class Builder {

        public BidRequest mBidRequest = new BidRequest();

        public Builder() {
        }

        public Builder appendId(String id) {
            mBidRequest.id = id;
            return this;
        }

        public Builder appendImp(List<Imp> imp) {
            mBidRequest.imp = imp;
            return this;
        }

        public Builder appendSite(Site site) {
            mBidRequest.site = site;
            return this;
        }

        public Builder appendApp(App app) {
            mBidRequest.app = app;
            return this;
        }

        public Builder appendDevice(Device device) {
            mBidRequest.device = device;
            return this;
        }

        public Builder appendUser(User user) {
            mBidRequest.user = user;
            return this;
        }

        public Builder appendTest(int test) {
            mBidRequest.test = test;
            return this;
        }

        public Builder appendAt(int at) {
            mBidRequest.at = at;
            return this;
        }

        public Builder appendTmax(int tmax) {
            mBidRequest.tmax = tmax;
            return this;
        }

        public Builder appendWseat(List<String> wseat) {
            mBidRequest.wseat = wseat;
            return this;
        }

        public Builder appendBseat(List<String> bseat) {
            mBidRequest.bseat = bseat;
            return this;
        }

        public Builder appendAllimps(int allimps) {
            mBidRequest.allimps = allimps;
            return this;
        }

        public Builder appendCur(List<String> cur) {
            mBidRequest.cur = cur;
            return this;
        }

        public Builder appendWlang(List<String> wlang) {
            mBidRequest.wlang = wlang;
            return this;
        }

        public Builder appendBcat(List<String> bcat) {
            mBidRequest.bcat = bcat;
            return this;
        }

        public Builder appendBadv(List<String> badv) {
            mBidRequest.badv = badv;
            return this;
        }

        public Builder appendBapp(List<String> bapp) {
            mBidRequest.bapp = bapp;
            return this;
        }

        public Builder appendSource(Source source) {
            mBidRequest.source = source;
            return this;
        }

        public Builder appendRegs(Regs regs) {
            mBidRequest.regs = regs;
            return this;
        }

        public Builder appendExt(Object ext) {
            mBidRequest.ext = ext;
            return this;
        }

        public OpenRTBRequest build() {
            return new OpenRTBRequest(this);
        }
    }
}
