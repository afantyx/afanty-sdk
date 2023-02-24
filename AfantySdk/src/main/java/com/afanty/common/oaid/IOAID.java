package com.afanty.common.oaid;

public interface IOAID {
    boolean supported();
    void doGet(IGetter getter);
}
