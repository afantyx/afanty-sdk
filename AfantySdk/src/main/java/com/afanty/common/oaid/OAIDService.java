package com.afanty.common.oaid;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class OAIDService implements ServiceConnection {
    private final Context context;
    private final IGetter getter;
    private final RemoteCaller caller;

    public static void bind(Context context, Intent intent, IGetter getter, RemoteCaller caller) {
        new OAIDService(context, getter, caller).bind(intent);
    }

    private OAIDService(Context context, IGetter getter, RemoteCaller caller) {
        if (context instanceof Application) {
            this.context = context;
        } else {
            this.context = context.getApplicationContext();
        }
        this.getter = getter;
        this.caller = caller;
    }

    private void bind(Intent intent) {
        try {
            boolean ret = context.bindService(intent, this, Context.BIND_AUTO_CREATE);
            if (!ret) {
                throw new OAIDException("Service binding failed");
            }
        } catch (Exception e) {
            getter.onError(e);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            String oaid = caller.callRemoteInterface(service);
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID/AAID acquire failed");
            }
            getter.onSuccess(oaid);
        } catch (Exception e) {
            getter.onError(e);
        } finally {
            try {
                context.unbindService(this);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @FunctionalInterface
    public interface RemoteCaller {

        String callRemoteInterface(IBinder binder) throws OAIDException, RemoteException;

    }
}
