package com.afanty.ads;

public enum AdStyle {
    BANNER("banner"),
    INTERSTITIAL("itl"),
    NATIVE("native"),
    REWARDED_AD("rwd"),
    REWARDED_INTERSTITIAL("rwditl");

    private final String name;
    private AdSize adSize;

    AdStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AdSize getAdSize() {
        return adSize;
    }

    public AdStyle setAdSize(AdSize adSize) {
        this.adSize = adSize;
        return this;
    }

}

