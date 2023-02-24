package com.afanty.internal.host;

public class AfantyHosts {
    private static String sBidHost = "";

    public static void setBidHost(String host) {
        sBidHost = host;
    }

    public static String getBidHostUrl() {
        return sBidHost;
    }
}
