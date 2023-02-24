package com.afanty.internal.action;

import android.content.Context;

import com.afanty.models.Bid;

public interface ActionTypeInterface {

    void resolveUrl(String deepLink, String landingPage, ResolveResultListener resolveResultListener);

    boolean shouldTryHandlingAction(Bid bid, int actionType);

    ActionResult performAction(final Context context, Bid bid, final String url, ActionParam actionParam);

    ActionResult performActionWhenOffline(final Context context, Bid bid, final String url, ActionParam actionParam);

    interface ResolveResultListener {
        void onSuccess(boolean result, final String resolvedUrl);
    }

    int getActionType();
}
