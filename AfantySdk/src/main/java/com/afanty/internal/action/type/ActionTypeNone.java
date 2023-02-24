package com.afanty.internal.action.type;

import android.content.Context;

import com.afanty.internal.action.ActionConstants;
import com.afanty.internal.action.ActionParam;
import com.afanty.internal.action.ActionResult;
import com.afanty.internal.action.ActionTypeInterface;
import com.afanty.models.Bid;

public class ActionTypeNone implements ActionTypeInterface {
    @Override
    public void resolveUrl(String deepLink, String landingPage, ResolveResultListener resolveResultListener) {
        resolveResultListener.onSuccess(true, landingPage);
    }

    @Override
    public boolean shouldTryHandlingAction(Bid bid, int actionType) {
        return getActionType() == actionType;
    }

    @Override
    public ActionResult performAction(Context context, Bid bid, String url, ActionParam actionParam) {
        return new ActionResult.Builder(false).build();
    }

    @Override
    public ActionResult performActionWhenOffline(Context context, Bid bid, String url, ActionParam actionParam) {
        return new ActionResult.Builder(false).build();
    }

    @Override
    public int getActionType() {
        return ActionConstants.ACTION_NONE;
    }
}
