package com.afanty.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsSp {
    private static final String TAG = "SettingsEx";

    private static final String DEFAULT_NAME = "Settings";
    private static HashMap<String, WeakReference<Pair<SharedPreferences, SharedPreferences.Editor>>> mSPHashMap = new HashMap<String, WeakReference<Pair<SharedPreferences, SharedPreferences.Editor>>>();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    /**
     * construct application settings with specified name.
     *
     * @param context
     * @param name    Desired preferences file.
     */
    public SettingsSp(@NonNull Context context, @NonNull String name) {
        Pair<SharedPreferences, SharedPreferences.Editor> spPair = getSettings(context, name);
        if (spPair == null) {
            return;
        }

        mSharedPreferences = spPair.first;
        if (mSharedPreferences == null) {
            return;
        }
        mEditor = spPair.second;
    }

    @Nullable
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    /**
     * construct the default application settings (SettingsEx.xml).
     *
     * @param context
     */
    public SettingsSp(@NonNull Context context) {
        this(context, DEFAULT_NAME);
    }

    private static synchronized Pair<SharedPreferences, SharedPreferences.Editor> getSettings(Context context, String name) {
        if (context == null)
            return null;

        synchronized (mSPHashMap) {
            Pair<SharedPreferences, SharedPreferences.Editor> pair;
            WeakReference<Pair<SharedPreferences, SharedPreferences.Editor>> wr = mSPHashMap.get(name);
            if (wr == null || (pair = wr.get()) == null) {
                mSPHashMap.remove(name);
                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
                    if (sharedPreferences == null)
                        return null;

                    pair = new Pair<SharedPreferences, SharedPreferences.Editor>(sharedPreferences, null);
                } catch (Exception e) {
                    return null;
                }
                mSPHashMap.put(name, new WeakReference<Pair<SharedPreferences, SharedPreferences.Editor>>(pair));
            }
            return pair;
        }
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean set(@NonNull String key, @Nullable String value) {
        return set(key, value, true);
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     *
     * @param key      The name of the preference to modify.
     * @param value    The new value for the preference.
     * @param isCommit true is support apply,false is not support apply
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean set(@NonNull String key, @Nullable String value, boolean isCommit) {
        if (mSharedPreferences != null) {
            String getVal = mSharedPreferences.getString(key, "");
            if (mSharedPreferences.contains(key) && getVal.equals(value))
                return true;
        }
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            mEditor.putString(key, value);
            if (isCommit)
                return mEditor.commit();
        }
        return false;
    }

    /**
     * Mark in the editor that a preference value should be removed
     *
     * @param key The name of the preference to remove.
     */
    public void remove(@NonNull String key) {
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            mEditor.remove(key);
            mEditor.apply();
        }
    }

    /**
     * remove all keys from preference
     *
     * @param keys
     * @return 0: success, 1: not find pref
     */
    public int removeAll(@NonNull List<String> keys) {
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            for (String key : keys) {
                mEditor.remove(key);
            }
            return mEditor.commit() ? 0 : 2;
        } else {
            return 1;
        }
    }

    /**
     * get key-value pair's value, return empty string if not found (or error)
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue.
     */
    public @Nullable
    String get(@NonNull String key) {
        return get(key, "");
    }

    /**
     * get key-value pair's value, return empty string if not found (or error)
     *
     * @param key    The name of the preference to retrieve.
     * @param defVal Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.
     */
    public @Nullable
    String get(@NonNull String key, @Nullable String defVal) {
        if (mSharedPreferences != null) {
            try {
                return mSharedPreferences.getString(key, defVal);
            } catch (ClassCastException e) {
            }
        }
        return defVal;
    }

    public boolean getBoolean(@NonNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NonNull String key, boolean defVal) {
        String value = get(key, null);
        if (value != null) {
            try {
                return Boolean.valueOf(value);
            } catch (Exception e) {
            }
        }
        return defVal;
    }

    public boolean setBoolean(@NonNull String key, boolean value) {
        return setBoolean(key, value, true);
    }

    /**
     * set boolean key-value pair
     *
     * @param key      The name of the preference to modify.
     * @param value    The new value for the preference.
     * @param isCommit true is support apply,false is not support apply
     * @return
     */
    public boolean setBoolean(@NonNull String key, boolean value, boolean isCommit) {
        return set(key, Boolean.toString(value), isCommit);
    }

    public int getInt(@NonNull String key) {
        return getInt(key, 0);
    }

    public int getInt(@NonNull String key, int defVal) {
        String value = get(key, null);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
            }
        }
        return defVal;
    }

    public boolean setInt(@NonNull String key, int value) {
        return setInt(key, value, true);
    }

    public boolean setInt(@NonNull String key, int value, boolean isCommit) {
        return set(key, Integer.toString(value), isCommit);
    }


    public long getLong(@NonNull String key) {
        return getLong(key, 0);
    }

    public long getLong(@NonNull String key, long defVal) {
        String value = get(key, null);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
            }
        }
        return defVal;
    }


    public boolean setLong(@NonNull String key, long value) {
        return setLong(key, value, true);
    }

    public boolean setLong(@NonNull String key, long value, boolean isCommit) {
        return set(key, Long.toString(value), isCommit);
    }

    public boolean contains(@NonNull String key) {
        return (mSharedPreferences != null && mSharedPreferences.contains(key));
    }


    public @NonNull
    Map<String, ?> getAll() {
        if (mSharedPreferences == null) {
            return new HashMap<String, String>();
        }
        return (mSharedPreferences.getAll());
    }

    public void clear() {
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            mEditor.clear();
            mEditor.apply();
        }
    }

    public void commit() {
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            mEditor.apply();
        }
    }

    public void apply() {
        if (mEditor == null & mSharedPreferences != null)
            mEditor = mSharedPreferences.edit();
        if (mEditor != null) {
            mEditor.apply();
        }
    }
}
