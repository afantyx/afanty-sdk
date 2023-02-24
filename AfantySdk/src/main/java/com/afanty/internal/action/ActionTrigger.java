package com.afanty.internal.action;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import com.afanty.bridge.BridgeManager;
import com.afanty.internal.Ad;
import com.afanty.models.Bid;

public class ActionTrigger {
    private static final String TAG = "RTB.ActionTrigger";
    protected ActionExecutor mActionExecutor = new ActionExecutor();

    protected Handler mHandler;
    protected Bid mBid;

    public ActionTrigger(Bid bid, Handler handler) {
        this.mBid = bid;
        this.mHandler = handler;
    }


    public void performClick(Context context, Rect rect) {
        performClick(context, rect, "cardnonbutton");
    }

    public void performClick(Context context, Rect rect, final String sourceType) {
        performClick(context, rect, sourceType, ActionData.EFFECT_ACTION_CARD);
    }

    public void performClick(Context context, Rect rect, final String sourceType, int effectType) {
        if (mBid == null) {
            return;
        }
        mActionExecutor.resetSupportActionHandler();
        mActionExecutor.addDetailActionHandler(ActionExecutor.getActionHandlerByType(ActionConstants.ACTION_LANDING_PAGE));
        mActionExecutor.addDeepLinkActionHandler(ActionExecutor.getActionHandlerByType(ActionConstants.ACTION_DEEPLINK));
        mActionExecutor.addCommonActionHandler(ActionExecutor.newCommonActionHandler());
        ActionParam actionParam = mBid.getActionParam();

        ActionParam rtbActionParam = mBid.getActionParam();
        if (rect != null) {
            rtbActionParam.mViewCenterX = rect.centerX();
            rtbActionParam.mViewCenterY = rect.centerY();
        }
        rtbActionParam.mSourceType = sourceType;
        rtbActionParam.mEffectType = effectType;

        actionParam.mForceGpAction = ActionUtils.isGPAction(mBid) && !mBid.isNeedLandPage();
        mActionExecutor.execute(context, rtbActionParam, new ActionExecutor.ActionExecutorListener() {
            long mClickStartTime = -1;

            @Override
            public void onStart() {
                mClickStartTime = System.currentTimeMillis();
                mHandler.sendMessage(mHandler.obtainMessage(Ad.AD_LOAD_CLICK));
                increaseClickCount();
            }

            @Override
            public void onResult(boolean success, boolean isDownloadAction, String resultUrl, int actionHandlerType) {
            }

            @Override
            public void onDeepLink(boolean success, boolean isDownloadAction, String resultUrl) {

            }
        });
    }

    private void increaseClickCount() {
        if (!mBid.isLoaded())
            return;

        BridgeManager.increaseClickCount(mBid);
    }
    public void performActionForAdClicked(Context context, final String sourceType, final int downloadOptTrig) {
        mActionExecutor.resetSupportActionHandler();
        mActionExecutor.addDeepLinkActionHandler(ActionExecutor.getActionHandlerByType(ActionConstants.ACTION_DEEPLINK));
        mActionExecutor.addCommonActionHandler(ActionExecutor.newCommonActionHandler());
        ActionParam actionParam = mBid.getActionParam();
        actionParam.mSourceType = sourceType;
        actionParam.mEffectType = ActionData.EFFECT_ACTION_CARD;
        mActionExecutor.execute(context, actionParam, new ActionExecutor.ActionExecutorListener() {
            long mClickStartTime = -1;

            @Override
            public void onStart() {
                mClickStartTime = System.currentTimeMillis();
                mHandler.sendMessage(mHandler.obtainMessage(Ad.AD_LOAD_CLICK));
                increaseClickCount();
            }

            @Override
            public void onResult(boolean success, boolean isDownloadAction, String resultUrl, int actionHandlerType) {
            }

            @Override
            public void onDeepLink(boolean success, boolean isDownloadAction, String resultUrl) {

            }
        });
    }

}
