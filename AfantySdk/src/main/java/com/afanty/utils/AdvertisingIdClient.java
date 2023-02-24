/*
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.afanty.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * com.facebook.internal is solely for the use of other packages within the Facebook SDK for
 * Android. Use of any of the classes in this package is unsupported, and they may be modified or
 * removed without warning at any time.
 * @link  https://github.com/facebook/facebook-android-sdk/blob/d1d8d06bfdd70b7a925388ea9385f6319141c9d5/facebook-core/src/main/java/com/facebook/internal/AttributionIdentifiers.java
 */
public class AdvertisingIdClient {
    private static final String TAG = AdvertisingIdClient.class.getCanonicalName();

    // com.google.android.gms.common.ConnectionResult.SUCCESS
    private static final int CONNECTION_RESULT_SUCCESS = 0;

    private static final long IDENTIFIER_REFRESH_INTERVAL_MILLIS = 3600 * 1000;

    private static Info recentlyFetchedInfo;

    @Nullable
    public static Info getAdvertisingIdInfo(Context context) {
        if (recentlyFetchedInfo != null && recentlyFetchedInfo.isFresh()) {
            return recentlyFetchedInfo;
        }

        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new Exception("getAttributionIdentifiers cannot be called on the main thread.");
            }

            Info identifiers = getAndroidId(context);
            return cacheAndReturnInfo(identifiers);
        } catch (Exception e) {
            Log.d(TAG, "Caught unexpected exception in getAttributionId(): " + e.toString());
            return null;
        }
    }

    private static Info getAndroidId(Context context) {
        Info identifiers = getAndroidIdViaReflection(context);
        if (identifiers == null) {
            identifiers = getAndroidIdViaService(context);
            if (identifiers == null) {
                identifiers = new Info();
            }
        }
        return identifiers;
    }

    private static Info getAndroidIdViaReflection(Context context) {
        try {
            Method getAdvertisingIdInfo =
                    getMethodQuietly(
                            "com.google.android.gms.ads.identifier.AdvertisingIdClient",
                            "getAdvertisingIdInfo",
                            Context.class);
            if (getAdvertisingIdInfo == null) {
                return null;
            }
            Object advertisingInfo = invokeMethodQuietly(null, getAdvertisingIdInfo, context);
            if (advertisingInfo == null) {
                return null;
            }

            Method getId =
                    getMethodQuietly(advertisingInfo.getClass(), "getId");
            Method isLimitAdTrackingEnabled =
                    getMethodQuietly(advertisingInfo.getClass(), "isLimitAdTrackingEnabled");
            if (getId == null || isLimitAdTrackingEnabled == null) {
                return null;
            }

            Info identifiers = new Info();
            identifiers.androidAdvertiserId =
                    (String) invokeMethodQuietly(advertisingInfo, getId);
            Object o = invokeMethodQuietly(advertisingInfo, isLimitAdTrackingEnabled);
            identifiers.limitTracking = o != null && (Boolean) o;

            return identifiers;
        } catch (Exception ignore) {
        }
        return null;
    }

    private static Info getAndroidIdViaService(Context context) {
        GoogleAdServiceConnection connection = new GoogleAdServiceConnection();
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                GoogleInterface adInfo = new GoogleInterface(connection.getBinder());
                Info identifiers = new Info();
                identifiers.androidAdvertiserId = adInfo.getAdvertiserId();
                identifiers.limitTracking = adInfo.isTrackingLimited();
                return identifiers;
            } catch (Exception ignore) {
            } finally {
                context.unbindService(connection);
            }
        }
        return null;
    }

    private static boolean isGooglePlayServicesAvailable(Context context) {
        Method method =
                getMethodQuietly(
                        "com.google.android.gms.common.GooglePlayServicesUtilLight",
                        "isGooglePlayServicesAvailable",
                        Context.class);

        if (method == null) {
            return false;
        }

        Object connectionResult = invokeMethodQuietly(null, method, context);
        return connectionResult instanceof Integer
                && (Integer) connectionResult == CONNECTION_RESULT_SUCCESS;
    }

    private static Info cacheAndReturnInfo(Info info) {
        info.fetchTime = System.currentTimeMillis();
        recentlyFetchedInfo = info;
        return info;
    }

    private static Method getMethodQuietly(
            Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static Method getMethodQuietly(
            String className, String methodName, Class<?>... parameterTypes) {
        try {
            Class<?> clazz = Class.forName(className);
            return getMethodQuietly(clazz, methodName, parameterTypes);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Object invokeMethodQuietly(Object receiver, Method method, Object... args) {
        try {
            return method.invoke(receiver, args);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }

    public static final class Info {
        private String androidAdvertiserId;
        private boolean limitTracking;
        private long fetchTime;

        public String getId() {
            return this.androidAdvertiserId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.limitTracking;
        }

        public boolean isFresh() {
            return System.currentTimeMillis() - fetchTime < IDENTIFIER_REFRESH_INTERVAL_MILLIS;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "androidAdvertiserId='" + androidAdvertiserId + '\'' +
                    ", limitTracking=" + limitTracking +
                    ", fetchTime=" + fetchTime +
                    '}';
        }
    }

    private static final class GoogleAdServiceConnection implements ServiceConnection {
        private final AtomicBoolean consumed = new AtomicBoolean(false);
        private final BlockingQueue<IBinder> queue = new LinkedBlockingDeque<>();

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                if (service != null) {
                    queue.put(service);
                }
            } catch (InterruptedException ignore) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (consumed.compareAndSet(true, true)) {
                throw new IllegalStateException("Binder already consumed");
            }
            return queue.take();
        }
    }

    private static final class GoogleInterface implements IInterface {
        private static final int FIRST_TRANSACTION_CODE = Binder.FIRST_CALL_TRANSACTION;
        private static final int SECOND_TRANSACTION_CODE = FIRST_TRANSACTION_CODE + 1;

        private final IBinder binder;

        GoogleInterface(IBinder binder) {
            this.binder = binder;
        }

        @Override
        public IBinder asBinder() {
            return binder;
        }

        public String getAdvertiserId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken(
                        "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(FIRST_TRANSACTION_CODE, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isTrackingLimited() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken(
                        "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(1);
                binder.transact(SECOND_TRANSACTION_CODE, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

}