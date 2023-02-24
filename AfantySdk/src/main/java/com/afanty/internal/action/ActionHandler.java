package com.afanty.internal.action;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.afanty.internal.action.type.ActionTypeNone;
import com.afanty.internal.net.utils.AdsUtils;
import com.afanty.utils.AppStarter;
import com.afanty.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionHandler {
    private static final String TAG = "RTB.Action";
    private final List<ActionTypeInterface> mSupportedActions;

    public ActionHandler(Builder builder) {
        this.mSupportedActions = builder.mSupportedUrlActions;
    }

    public void handleDeepLinkAction(final Context context, final ActionParam actionParam, final ActionResultListener actionResultListener) {
        if (mSupportedActions == null)
            return;
        Pair<Boolean, Boolean> pair = NetworkUtils.checkConnected(context);
        boolean hasNet = pair.first || pair.second;
        for (final ActionTypeInterface actionType : mSupportedActions) {
            if (actionType.shouldTryHandlingAction(actionParam.mBid, actionParam.mActionType)) {
                if (hasNet) {
                    actionType.resolveUrl(actionParam.mDeepLink, actionParam.mLandingPage, new ActionTypeInterface.ResolveResultListener() {
                        @Override
                        public void onSuccess(boolean result, String resolvedUrl) {
                            ActionResult performActionResult = actionType.performAction(context, actionParam.mBid, resolvedUrl, actionParam);
                            if (actionResultListener != null) {
                                if (performActionResult.mActionResult && performActionResult.mSupportActionReport)
                                    ActionUtils.reportActionTracker(actionParam);

                                actionResultListener.onResult(performActionResult.mActionResult, performActionResult.mIsDownloadAction, resolvedUrl);
                            }
                        }

                    });
                } else {
                    if (actionResultListener != null)
                        actionResultListener.onResult(false, false, null);
                }
            } else {
                if (actionResultListener != null)
                    actionResultListener.onResult(false, false, null);
            }
        }
    }

    public ActionResult handleDetailPageAction(final Context context, final ActionParam actionParam) {
        if (mSupportedActions == null)
            return new ActionResult.Builder(false).build();
        Pair<Boolean, Boolean> pair = NetworkUtils.checkConnected(context);
        boolean hasNet = pair.first || pair.second;
        for (final ActionTypeInterface actionType : mSupportedActions) {
            if (actionType.shouldTryHandlingAction(actionParam.mBid, actionParam.mActionType)) {
                if (hasNet)
                    return actionType.performAction(context, actionParam.mBid, null, actionParam);
                else
                    return actionType.performActionWhenOffline(context, actionParam.mBid, null, actionParam);
            }
        }
        return new ActionResult.Builder(false).build();
    }

    public void handleAction(final Context context, final ActionParam actionParam, final ActionResultListener actionResultListener) {
        if (mSupportedActions == null)
            return;
        Pair<Boolean, Boolean> pair = NetworkUtils.checkConnected(context);
        boolean hasNet = pair.first || pair.second;

        if (!checkBottomActionDeal(actionParam)) {
            dealBottomAction(context, actionParam, actionResultListener);
            return;
        }

        for (final ActionTypeInterface ACTION_TYPE : mSupportedActions) {
            if (ACTION_TYPE.shouldTryHandlingAction(actionParam.mBid, actionParam.mActionType)) {
                if (hasNet) {
                    ACTION_TYPE.resolveUrl(actionParam.mDeepLink, actionParam.mLandingPage, new ActionTypeInterface.ResolveResultListener() {
                        @Override
                        public void onSuccess(boolean result, String resolvedUrl) {
                            ActionResult performActionResult = ACTION_TYPE.performAction(context, actionParam.mBid, resolvedUrl, actionParam);
                            if (actionResultListener != null) {
                                if (performActionResult.mSupportActionReport)
                                    ActionUtils.reportActionTracker(actionParam);

                                actionResultListener.onResult(performActionResult.mActionResult, performActionResult.mIsDownloadAction, resolvedUrl);
                            }
                        }
                    });
                } else {
                    ActionResult performActionResult = ACTION_TYPE.performActionWhenOffline(context, actionParam.mBid, actionParam.mLandingPage, actionParam);
                    if (actionResultListener != null)
                        actionResultListener.onResult(performActionResult.mActionResult, performActionResult.mIsDownloadAction, actionParam.mLandingPage);
                }
            }
        }
    }

    private boolean checkBottomActionDeal(ActionParam actionParam) {
        boolean hasActionType = false;
        for (ActionTypeInterface actionTypeInterface : mSupportedActions) {
            if (actionTypeInterface.getActionType() == actionParam.mActionType) {
                hasActionType = true;
                break;
            }
        }
        return hasActionType;
    }

    private void dealBottomAction(final Context context, final ActionParam actionParam, final ActionResultListener actionResultListener) {
        try {
            String pkgName = null;
            if (actionParam.mBid != null && actionParam.mBid.getProductData() != null) {
                pkgName = actionParam.mBid.getProductData().getPkgName();
            }
            if (TextUtils.isEmpty(pkgName)) {
                AppStarter.startBrowserNoChoice(context, actionParam.mLandingPage, true);
                invokeActionResultCallBack(actionParam, actionResultListener);
                return;
            }

            jumpToGPWithPkgName(context, pkgName, actionParam, actionResultListener);
        } catch (Exception exception) {
        }
    }

    private void jumpToGPWithPkgName(Context context, String pkgName, ActionParam actionParam, ActionResultListener actionResultListener) {

        String url;
        if (AdsUtils.isGPDetailUrl(actionParam.mLandingPage)) {
            url = actionParam.mLandingPage;
        } else {
            url = "https://play.google.com/store/apps/details?id=" + pkgName;
        }
        AppStarter.startAppMarketWithUrl(context, url, pkgName, actionParam.mBid != null ? actionParam.mBid.getAdId() : "");

        invokeActionResultCallBack(actionParam, actionResultListener);
    }

    private void invokeActionResultCallBack(ActionParam actionParam, ActionResultListener actionResultListener) {
        if (actionResultListener != null) {
            ActionUtils.reportActionTracker(actionParam);
            actionResultListener.onResult(true, false, actionParam.mLandingPage);
        }
    }

    public interface ActionResultListener {
        void onResult(boolean success, boolean isDownloadAction, String actionUrl);
    }

    public static class Builder {
        private List<ActionTypeInterface> mSupportedUrlActions = Collections.singletonList(new ActionTypeNone());

        public Builder withSupportedActions(final ActionTypeInterface actionTypeInterface) {
            this.mSupportedUrlActions = Collections.singletonList(actionTypeInterface);
            return this;
        }

        public Builder withSupportedActions(final List<ActionTypeInterface> actionTypeInterface) {
            this.mSupportedUrlActions = new ArrayList<>(actionTypeInterface);
            return this;
        }

        public ActionHandler build() {
            return new ActionHandler(this);
        }
    }

}
