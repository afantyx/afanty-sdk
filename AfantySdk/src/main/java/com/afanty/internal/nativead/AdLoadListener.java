package com.afanty.internal.nativead;

import com.afanty.ads.AdError;
import com.afanty.models.Bid;

public interface AdLoadListener {
    void onDataLoaded(Bid ad);

    void onDataError(AdError error);
}