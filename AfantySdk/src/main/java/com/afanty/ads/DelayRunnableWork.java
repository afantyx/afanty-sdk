package com.afanty.ads;

import android.os.Handler;
import java.lang.ref.WeakReference;

public abstract class DelayRunnableWork implements Runnable {

    private static final String TAG = "Task";
    private String threadName;
    private long backgroundDelay;
    private Exception mError;

    public DelayRunnableWork() {
        this(0);
    }

    public DelayRunnableWork(long delay) {
        this.backgroundDelay = delay;
    }

    public DelayRunnableWork(String threadName) {
        this(threadName, 0);
    }


    public DelayRunnableWork(String threadName, long delayTime) {
        this.threadName = threadName;
        this.backgroundDelay = delayTime;
    }

    @Override
    public void run() {
        if (threadName != null) {
            Thread.currentThread().setName(threadName);
        }
        if (backgroundDelay > 0) {
            try {
                Thread.sleep(backgroundDelay);
            } catch (InterruptedException e) {
            }
        }

        exec();
    }

    private void exec() {
        try {
            execute();
        } catch (Exception e) {
            mError = e;
        } catch (Throwable tr) {
            mError = new RuntimeException(tr);
        }

        callBack(mError);
    }

    public abstract void execute() throws Exception;

    public void callBack(Exception exception) {
    }

    public static abstract class UICallBackDelayRunnableWork extends DelayRunnableWork {
        private long uiCallBackDelay;

        public UICallBackDelayRunnableWork() {
            this(0);
        }

        public UICallBackDelayRunnableWork(long delayTime) {
            uiCallBackDelay = delayTime;
        }

        private WeakReference<Handler> tempHandler;

        public void setUIHandler(Handler handler) {
            tempHandler = new WeakReference<>(handler);
        }

        @Override
        public void execute() {
        }

        @Override
        public final void callBack(Exception exception) {
            if (tempHandler != null && tempHandler.get() != null)
                tempHandler.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callBackOnUIThread();
                    }
                }, uiCallBackDelay);
        }

        public abstract void callBackOnUIThread();
    }

}
