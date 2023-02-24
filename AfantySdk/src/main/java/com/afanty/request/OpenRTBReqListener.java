package com.afanty.request;

public interface OpenRTBReqListener {
    String BUILD = "BUILD";
    String NETWORK = "Network";
    String SERVER = "Server";


    String ERROR_REQBODY = "ERROR_REQBODY";

    void onRequestError(String errorType, String msg);

    void onRequestSuccess(String jsonStr);
}
