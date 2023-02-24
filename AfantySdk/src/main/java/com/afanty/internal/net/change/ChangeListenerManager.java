package com.afanty.internal.net.change;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChangeListenerManager {
    private static ConcurrentHashMap<String, Object> mStickyEvents = new ConcurrentHashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ConcurrentHashMap<String, List<ChangedListener>> mListenersMap = new ConcurrentHashMap();

    public static ChangeListenerManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void notifyChange(String key) {
        notifyChange(key, null);
    }

    public void notifyChange(final String key, long delay) {
        this.mHandler.postDelayed(() -> ChangeListenerManager.this.notifyChange(key, null), delay);
    }

    public <T> void notifyChange(final String key, final T value) {
        if (TextUtils.isEmpty(key))
            return;

        try {
            final List<ChangedListener> listeners = mListenersMap.get(key);
            if (listeners != null) {
                Runnable mNotifyTask = new Runnable() {
                    @Override
                    public void run() {
                        for (final ChangedListener listener : listeners) {
                            listener.onListenerChange(key, value);
                        }
                    }
                };

                if (Looper.myLooper() == Looper.getMainLooper()) {
                    mNotifyTask.run();
                } else {
                    mHandler.post(mNotifyTask);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void notifyStickyEventChange(String key, T value) {
        mStickyEvents.put(key, value);
        this.notifyChange(key, value);
    }

    public void registerChangedListener(String key, ChangedListener listener) {
        if (TextUtils.isEmpty(key) || listener == null)
            return;

        List<ChangedListener> list = mListenersMap.get(key);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            list.add(listener);
            mListenersMap.put(key, list);
        } else {
            if (!list.contains(listener)) {
                list.add(listener);
            }
        }
        if (mStickyEvents.containsKey(key)) {
            Object value = mStickyEvents.remove(key);
            listener.onListenerChange(key, value);
        }
    }

    public void unregisterChangedListener(String key, ChangedListener listener) {
        if (TextUtils.isEmpty(key) || listener == null)
            return;

        try {
            List<ChangedListener> list = mListenersMap.get(key);
            if (list != null) {
                list.remove(listener);
                if (list.isEmpty())
                    mListenersMap.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class InstanceHolder {
        private static final ChangeListenerManager INSTANCE = new ChangeListenerManager();
    }
}
