package com.afanty.common.oaid;

public interface IGetter {
    void onSuccess(String oaid);

    void onError(Exception error);
}
