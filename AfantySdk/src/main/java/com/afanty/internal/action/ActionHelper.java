package com.afanty.internal.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.utils.Reflector;

import java.util.ArrayList;
import java.util.List;

public class ActionHelper {
    private static final int[] commonActionArray = {ActionConstants.ACTION_GP, ActionConstants.ACTION_WEB};
    private static final int[] webCommonActionArray = {ActionConstants.ACTION_GP, ActionConstants.ACTION_WEB};

    public static List<ActionTypeInterface> getCommonActionList() {
        return getActionTypeListByArray(commonActionArray);
    }

    public static List<ActionTypeInterface> getWebCommonActionTypeList() {
        return getActionTypeListByArray(webCommonActionArray);
    }

    @Nullable
    private static List<ActionTypeInterface> getActionTypeListByArray(@NonNull int[] array) {
        if (array.length == 0) {
            return null;
        }

        List<ActionTypeInterface> commonList = new ArrayList<>();
        for (int type : array) {
            ActionTypeInterface actionTypeInterface = getActionByType(type);
            if (actionTypeInterface != null) {
                commonList.add(actionTypeInterface);
            }
        }

        return commonList;
    }

    @Nullable
    public static ActionTypeInterface getActionByType(int actionType) {
        ActionTypeInfo actionTypeInfo = ActionTypeInfo.getActionByType(actionType);
        if (actionTypeInfo != null) {
            return getExActionInterfaceByName(actionTypeInfo.actionClazzName);
        }

        return null;
    }

    @Nullable
    public static ActionTypeInterface getExActionInterfaceByName(String clazzName) {
        Object object = Reflector.createInstanceOfClassByClazzName(clazzName, null);
        if (object instanceof ActionTypeInterface) {
            return (ActionTypeInterface) object;
        }

        return null;
    }
}
