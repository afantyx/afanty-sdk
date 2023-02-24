package com.afanty.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.afanty.internal.net.change.ChangeListenerManager;
import com.afanty.internal.net.change.ChangedKeys;

public class NetWorkChangReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ChangeListenerManager.getInstance().notifyChange(ChangedKeys.KEY_CONNECTIVITY_CHANGE);
        }
    }
}
