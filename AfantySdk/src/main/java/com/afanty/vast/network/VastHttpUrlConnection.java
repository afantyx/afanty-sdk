// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast.network;

import androidx.annotation.NonNull;

import com.afanty.vast.utils.Preconditions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

public abstract class VastHttpUrlConnection extends HttpURLConnection {
    private static final String TAG = "Ad.MoPubHttpUrlConnection";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    private VastHttpUrlConnection(URL url) {
        super(url);
    }

    public static HttpURLConnection getHttpUrlConnection(@NonNull final String url)
            throws IOException {
        Preconditions.checkNotNull(url);
        if (isUrlImproperlyEncoded(url)) {
            throw new IllegalArgumentException("URL is improperly encoded: " + url);
        }
        String getUrl;
        try {
            getUrl = urlEncode(url);
        } catch (Exception e) {
            getUrl = url;
        }
        final HttpURLConnection urlConnection =
                (HttpURLConnection) new URL(getUrl).openConnection();
//        urlConnection.setRequestProperty("user-agent", AttributionManager.getInstance().getKeyUserAgent());
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);

        return urlConnection;
    }

    /**
     * This method constructs a properly encoded and valid URI adhering to legal characters for
     * each component. See Android docs on these classes for reference.
     */
    @NonNull
    public static String urlEncode(@NonNull final String url) throws Exception {
        Preconditions.checkNotNull(url);

        if (isUrlImproperlyEncoded(url)) {
            throw new UnsupportedEncodingException("URL is improperly encoded: " + url);
        }
        URI uri;
        if (isUrlUnEncoded(url)) {
            uri = encodeUrl(url);
        } else {
            uri = new URI(url);
        }

        return uri.toURL().toString();
    }

    /**
     * This method tries to decode the URL and returns false if it can't due to improper encoding.
     */
    static boolean isUrlImproperlyEncoded(@NonNull String url) {
        try {
            URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return true;
        }
        return false;
    }

    /**
     * This method tries to construct a URI and returns true if it can't due to illegal characters
     * in the url.
     */
    static boolean isUrlUnEncoded(@NonNull String url) {
        try {
            new URI(url);
        } catch (URISyntaxException e) {
            return true;
        }
        return false;
    }

    /**
     * This method encodes each component of the URL into a valid URI.
     */
    @NonNull
    static URI encodeUrl(@NonNull String urlString) throws Exception {
        URI uri;
        try {
            URL url = new URL(urlString);
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                    url.getPath(), url.getQuery(), url.getRef());
        } catch (Exception e) {
            throw e;
        }
        return uri;
    }
}
