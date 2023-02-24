package com.afanty.internal.internal;

public interface AdRequestListener {
    String BUILD = "BUILD";
    String NETWORK = "Network";
    String SERVER = "Server";

    void onAdRequestError(String errorType, String msg);

    void onAdRequestSuccess(String jsonStr);
}
