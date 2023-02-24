package com.afanty.api;

import com.afanty.request.CustomBidRequest;

public class AftAdSettings {

    private Builder builder;

    private AftAdSettings(Builder builder) {
        this.builder = builder;
    }

    public String getBidHost() {
        return builder.bidHost;
    }

    public CustomBidRequest.App getApp(){
        return builder.app;
    }

    public CustomBidRequest.DeviceInfo getDeviceInfo(){
        return builder.deviceInfo;
    }

    public static class Builder {
        private String bidHost;

        private CustomBidRequest.App app;

        private CustomBidRequest.DeviceInfo deviceInfo;

        public Builder setBidHost(String host) {
            this.bidHost = host;
            return this;
        }

        public Builder setApp(CustomBidRequest.App app) {
            this.app = app;
            return this;
        }

        public Builder setDeviceInfo(CustomBidRequest.DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public AftAdSettings build() {
            return new AftAdSettings(this);
        }
    }

}
