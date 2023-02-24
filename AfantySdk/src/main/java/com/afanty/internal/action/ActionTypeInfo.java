package com.afanty.internal.action;

import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.afanty.utils.Reflector;

public enum ActionTypeInfo {
    GOOGLE_PLAY(ActionConstants.ACTION_GP, "com.afanty.internal.action.type.ActionTypeApp"),
    WEB(ActionConstants.ACTION_WEB, "com.afanty.internal.action.type.ActionTypeWeb"),
    DEEPLINK(ActionConstants.ACTION_DEEPLINK, "com.afanty.internal.action.type.ActionTypeDeeplink"),
    LANDING_PAGE(ActionConstants.ACTION_LANDING_PAGE, "com.afanty.internal.action.type.ActionTypeLandingPage");

    public int actionType;
    public String actionClazzName;

    ActionTypeInfo(int actionType, String actionClazzName) {
        this.actionType = actionType;
        this.actionClazzName = actionClazzName;
    }

    private final static SparseArray<ActionTypeInfo> mValues = new SparseArray<>();

    static {
        for (ActionTypeInfo item : ActionTypeInfo.values()) {
            boolean hasCurrentAction = Reflector.hasNecessaryClazz(item.actionClazzName);
            if (hasCurrentAction) {
                mValues.put(item.actionType, item);
            }
        }
    }

    @Nullable
    public static ActionTypeInfo getActionByType(int actionType) {
        return mValues.get(actionType);
    }

}
