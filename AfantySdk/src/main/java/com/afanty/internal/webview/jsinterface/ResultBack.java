package com.afanty.internal.webview.jsinterface;


public interface ResultBack {
    String HAD_DONE = "4";
    String TIME_OUT = "3";
    String PENDING = "2";
    String CANCEL = "1";
    String SUCCEED = "0";
    String NO_METHOD = "-1";
    String NO_AUTH = "-2";
    String NO_INIT_FINISH = "-3";
    String ERROR_PARAM = "-4";
    String ERROR_EXCEPTION = "-5";
    String NO_CALLBACK_NAME = "-6";
    String LOSE_SCREEN = "-7";
    String IO_FAIL = "-8";
    String SHOW_DIALOG = "-9";
    String AD_FAILED = "-10";


    void onResult(String callbackName, String response);
}
