package com.afanty.internal;

public interface Ad {
    int AD_TYPE_CPD = 1;
    int AD_TYPE_CAMPAIGN = 5;

    int AD_LOAD_SUCCESS = 1;
    int AD_LOAD_FAILED = 2;
    int AD_LOAD_SHOW = 3;
    int AD_LOAD_CLICK = 4;

    void loadAd();

    void destroy();

}
