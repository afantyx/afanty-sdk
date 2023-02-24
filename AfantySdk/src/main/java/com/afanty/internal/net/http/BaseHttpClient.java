package com.afanty.internal.net.http;

import okhttp3.OkHttpClient;

public class BaseHttpClient {

    public final OkHttpClient client;

    private BaseHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .dns(new HttpDns())
                .eventListener(new OkEventListenerStats());

        client = builder.build();
    }

    private static class Holder {
        private static final BaseHttpClient instance = new BaseHttpClient();
    }

    public static BaseHttpClient getInstance() {
        return Holder.instance;
    }
}
