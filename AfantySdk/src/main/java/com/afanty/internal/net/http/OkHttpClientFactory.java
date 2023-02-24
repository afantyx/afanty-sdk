package com.afanty.internal.net.http;

import android.text.TextUtils;

import com.afanty.internal.internal.AdConstants;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class OkHttpClientFactory {
    private static final ConcurrentHashMap<String, OkHttpClient> sApiClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ConnectionPool> sPoolMap = new ConcurrentHashMap<>();

    public synchronized static OkHttpClient obtainApiClient(String portal, String host, boolean retryOnConnectionFailure, int connectTimeout, int readTimeout) {
        if (sApiClients.get(portal) != null)
            return sApiClients.get(portal);
        OkHttpClient.Builder builder = BaseHttpClient.getInstance().client.newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(retryOnConnectionFailure);
        if (TextUtils.equals(portal, AdConstants.PortalKey.TRACK_HELPER)) {
            builder.followRedirects(false);
            builder.followSslRedirects(false);
        } else {
            boolean canRedirect = canRedirectByPortal(portal);
            builder.followRedirects(canRedirect);
            builder.followSslRedirects(canRedirect);
        }
        if (portalInBlacklist(portal)) {
            builder.connectionPool(new ConnectionPool());
        } else {
            ConnectionPool pool = sPoolMap.get(host);
            if (pool == null) {
                int connPoolSize = 5;
                pool = new ConnectionPool(connPoolSize, 5, TimeUnit.MINUTES);
                sPoolMap.put(host, pool);
            }
            builder.connectionPool(pool);
        }
        sApiClients.put(portal, builder.build());
        return sApiClients.get(portal);

    }

    private static final List<String> sPortalBlacklist = Arrays.asList(
            AdConstants.PortalKey.TRACK_HELPER,
            AdConstants.PortalKey.MULTI_DOWNLOAD
    );

    private static boolean portalInBlacklist(String portal) {
        return sPortalBlacklist.contains(portal);
    }

    private static boolean canRedirectByPortal(String portal) {
        return true;
    }
}
