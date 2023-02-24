package com.afanty.internal.action;

import android.content.Context;

import androidx.annotation.Nullable;

public class ActionExecutor {
    private static final String TAG = "RTB.Executor";

    private long mLastActionTriggerTime = 0;
    private ActionHandler mDetailActionHandler;
    private ActionHandler mDeepLinkActionHandler;
    private ActionHandler mCommonActionHandler;
    private boolean mClickInProgress;

    @Nullable
    public static ActionHandler getActionHandlerByType(int type) {
        ActionTypeInterface actionTypeInterface = ActionHelper.getActionByType(type);
        return actionTypeInterface == null ? null : new ActionHandler.Builder().withSupportedActions(actionTypeInterface).build();
    }

    public static ActionHandler newCommonActionHandler() {
        return new ActionHandler.Builder()
                .withSupportedActions(ActionHelper.getCommonActionList())
                .build();
    }

    public void resetSupportActionHandler() {
        mDeepLinkActionHandler = null;
        mDetailActionHandler = null;
        mCommonActionHandler = null;
    }

    public void addDetailActionHandler(ActionHandler actionHandler) {
        mDetailActionHandler = actionHandler;
    }

    public void addDeepLinkActionHandler(ActionHandler actionHandler) {
        mDeepLinkActionHandler = actionHandler;
    }

    public void addCommonActionHandler(ActionHandler actionHandler) {
        mCommonActionHandler = actionHandler;
    }

    public void execute(final Context context, final ActionParam actionParam, final ActionExecutorListener actionExecutorListener) {
        if (mLastActionTriggerTime != 0 && (System.currentTimeMillis() - mLastActionTriggerTime) < 1000) {
            return;
        }

        mLastActionTriggerTime = System.currentTimeMillis();
        if (mClickInProgress) {
            return;
        }

        mClickInProgress = true;
        if (actionExecutorListener != null)
            actionExecutorListener.onStart();

        if (mDeepLinkActionHandler != null) {
            mDeepLinkActionHandler.handleDeepLinkAction(context, actionParam, new ActionHandler.ActionResultListener() {
                @Override
                public void onResult(boolean success, boolean isDownloadAction, String actionUrl) {
                    if (success) {
                        mClickInProgress = false;
                        if (actionExecutorListener != null) {
                            actionExecutorListener.onResult(true, isDownloadAction, actionUrl, ActionExecutorListener.ACTION_TYPE_DEEPLINL);
                            actionExecutorListener.onDeepLink(true, isDownloadAction, actionUrl);
                        }
                        return;
                    } else if (actionExecutorListener != null) {
                        actionExecutorListener.onDeepLink(false, isDownloadAction, actionUrl);
                    }

                    if (dealDetailActionHandler(context, actionParam, actionExecutorListener)) {
                        return;
                    }

                    dealCommonActionHandler(context, actionParam, actionExecutorListener);
                }
            });
        } else {
            if (dealDetailActionHandler(context, actionParam, actionExecutorListener)) {
                return;
            }

            dealCommonActionHandler(context, actionParam, actionExecutorListener);
        }
    }

    private boolean dealDetailActionHandler(Context context, ActionParam actionParam, ActionExecutorListener actionExecutorListener) {
        if (mDetailActionHandler == null || actionParam.mForceGpAction) {
            return false;
        }

        boolean result = executeLandingPageAction(context, actionParam);
        if (result) {
            mClickInProgress = false;
            if (actionExecutorListener != null)
                actionExecutorListener.onResult(true, false, null, ActionExecutorListener.ACTION_TYPE_DETAIL_PAGE);
        }
        return result;
    }

    private boolean executeLandingPageAction(Context context, ActionParam actionParam) {
        if (mDetailActionHandler == null)
            return false;

        return mDetailActionHandler.handleDetailPageAction(context, actionParam).mActionResult;
    }

    private void dealCommonActionHandler(Context context, ActionParam actionParam, final ActionExecutorListener actionExecutorListener) {
        if (mCommonActionHandler == null) {
            return;
        }
        mCommonActionHandler.handleAction(context, actionParam, new ActionHandler.ActionResultListener() {
            @Override
            public void onResult(boolean success, boolean isDownloadAction, String actionUrl) {
                mClickInProgress = false;
                if (actionExecutorListener != null)
                    actionExecutorListener.onResult(success, isDownloadAction, actionUrl, ActionExecutorListener.ACTION_TYPE_LANDINGPAGE);
            }
        });
    }

    public interface ActionExecutorListener {
        int ACTION_TYPE_DETAIL_PAGE = 1;
        int ACTION_TYPE_DEEPLINL = 2;
        int ACTION_TYPE_LANDINGPAGE = 3;

        void onStart();

        void onResult(boolean success, boolean isDownloadAction, String resultUrl, int actionHandlerType);

        void onDeepLink(boolean success, boolean isDownloadAction, String resultUrl);
    }
}
