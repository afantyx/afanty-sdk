package com.afanty.utils;

import com.afanty.models.Bid;
import com.afanty.vast.VastVideoConfig;

public class AdDataUtils {
    private static final String TAG = "AdDataUtils";

    public static VastVideoConfig getVastVideoConfig(Bid adData) {
        VastVideoConfig vastVideoConfig = null;
        try {
            vastVideoConfig = (VastVideoConfig) adData.getVastVideoConfig();
        } catch (Exception e) {
        }
        return vastVideoConfig;
    }
}
