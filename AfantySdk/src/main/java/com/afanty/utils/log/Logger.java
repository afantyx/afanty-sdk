package com.afanty.utils.log;

import android.util.Log;

import java.util.Locale;

public class Logger {
    private static final String TAG = "AFT.";
    private static int mCurrentLevel = 1;

    public static void v(String category, String msg) {
        if (mCurrentLevel > Log.VERBOSE)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.v(TAG + category, printMsg);
    }

    public static void v(String category, String msg, Throwable tr) {
        if (mCurrentLevel > Log.VERBOSE)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s - %s", Thread.currentThread().getId(), msg, Log.getStackTraceString(tr));
        Log.v(TAG + category, printMsg);
    }

    public static void v(String category, String format, Object... args) {
        if (mCurrentLevel > Log.VERBOSE)
            return;
        Log.v(TAG + category, String.format(Locale.US, format, args));
    }

    public static void d(String category, String msg) {
        if (mCurrentLevel > Log.DEBUG)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.d(TAG + category, printMsg);
    }

    public static void d(String category, String msg, Throwable tr) {
        if (mCurrentLevel > Log.DEBUG)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s - %s", Thread.currentThread().getId(), msg, Log.getStackTraceString(tr));
        Log.d(TAG + category, printMsg);
    }

    public static void d(String category, String format, Object... args) {
        if (mCurrentLevel > Log.DEBUG)
            return;
        Log.d(TAG + category, String.format(Locale.US, format, args));
    }

    public static void i(String category, String msg) {
        if (mCurrentLevel > Log.INFO)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.i(TAG + category, printMsg);
    }

    public static void i(String category, String format, Object... args) {
        if (mCurrentLevel > Log.INFO)
            return;
        Log.i(TAG + category, String.format(Locale.US, format, args));
    }

    public static void w(String category, String msg) {
        if (mCurrentLevel > Log.WARN)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.w(TAG + category, printMsg);
    }

    public static void w(String category, Throwable tr) {
        if (mCurrentLevel > Log.WARN)
            return;
        Log.w(TAG + category, Log.getStackTraceString(tr));
    }

    public static void w(String category, String msg, Throwable tr) {
        if (mCurrentLevel > Log.WARN)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s - %s", Thread.currentThread().getId(), msg, Log.getStackTraceString(tr));
        Log.w(TAG + category, printMsg);
    }

    public static void e(String category, String msg) {
        if (mCurrentLevel > Log.ERROR)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.e(TAG + category, printMsg);
    }

    public static void e(String category, Throwable tr) {
        if (mCurrentLevel > Log.ERROR)
            return;
        Log.e(TAG + category, Log.getStackTraceString(tr));
    }

    public static void e(String category, String msg, Throwable tr) {
        if (mCurrentLevel > Log.ERROR)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s - %s", Thread.currentThread().getId(), msg, Log.getStackTraceString(tr));
        Log.e(TAG + category, printMsg);
    }

    public static void f(String category, String msg) {
        if (mCurrentLevel > Log.ASSERT)
            return;
        String printMsg = String.format(Locale.US, "[%d] %s", Thread.currentThread().getId(), msg);
        Log.wtf(TAG + category, printMsg);
    }

    public static void f(String category, Throwable tr) {
        if (mCurrentLevel > Log.ASSERT)
            return;
        Log.wtf(TAG + category, Log.getStackTraceString(tr));
    }

    public static void f(String category, String msg, Throwable tr) {
        if (mCurrentLevel > Log.ASSERT)
            return;
        Log.wtf(TAG + category, msg + Log.getStackTraceString(tr));
    }

    public static void out(String msg) {
        Log.i(TAG + "Info", msg);
    }

    public int getCurrentLevel() {
        return mCurrentLevel;
    }

    public static void setCurrentLevel(int value) {
        mCurrentLevel = value;
    }

    public String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    public static boolean isDebugging() {
        return mCurrentLevel <= Log.DEBUG;
    }
}