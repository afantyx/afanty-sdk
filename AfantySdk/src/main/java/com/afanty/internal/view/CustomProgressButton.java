package com.afanty.internal.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.models.Bid;
import com.afanty.utils.NetworkUtils;
import com.afanty.utils.PackageUtils;
import com.afanty.utils.SettingConfig;

public class CustomProgressButton extends CustomProgressBar implements View.OnClickListener {
    public static String TAG = "AD.TextProgressButton";

    private PorterDuffXfermode mPorterDuffXfermode;
    private Status mState = Status.NORMAL;
    private String pkgName;
    private int versionCode;
    private String mDownUrl;
    private String mOriginalUrl;
    private final int mSecondProgress = 1200;

    private OnStateClickListener mOnStateClickListener;

    private long mLastCheck = 0;
    private int mActionType = 0;
    int azStatus = 0;
    int downloadStatus = -1;

    public CustomProgressButton(Context context) {
        super(context);
    }

    public CustomProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {
        if (mOnStateClickListener == null)
            return;
        mOnStateClickListener.onClick();

        switch (mState) {
            case WAITING:
            case PROCESSING:
                mOnStateClickListener.onDownloading();
                break;
            case AUTO_PAUSE:
            case USER_PAUSE:
            case MOBILE_PAUSE:
                mOnStateClickListener.onPause();
                break;
            case COMPLETED:
            case AZED:
                mOnStateClickListener.onNormal(mState);
                break;
            case NORMAL:
            case UPDATE:
                mOnStateClickListener.onNormal(mState);
                break;
            default:
                break;
        }
    }

    public void setOnStateClickListener(OnStateClickListener stateClickListener) {
        this.mOnStateClickListener = stateClickListener;
    }

    @Override
    protected void drawCustomText(Canvas canvas) {
        if (mPaintText == null)
            return;

        if (getMeasuredWidth() != 0) {
            if (getProgress() > 0 && getProgress() < 100)
                setSecondaryProgress(getProgress() + mSecondProgress / getMeasuredWidth());
            else
                setSecondaryProgress(0);
        }

        mPaintText.setColor(getTextColor());
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float fTextHeightStart = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2f - 2f;
        String text = getText();
        canvas.drawText(text, getWidth() / 2f, fTextHeightStart, mPaintText);

        Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bufferCanvas = new Canvas(bufferBitmap);
        bufferCanvas.drawText(text, getWidth() / 2f, fTextHeightStart, mPaintText);
        mPaintText.setXfermode(mPorterDuffXfermode);
        mPaintText.setColor(getXfermodeTextColor());

        RectF rectF;
        if (getMeasuredWidth() != 0)
            rectF = new RectF(0, 0, getWidth() * (getProgress() + mSecondProgress / getMeasuredWidth()) / 100, getHeight());
        else
            rectF = new RectF(0, 0, getWidth() * getProgress() / 100, getHeight());

        bufferCanvas.drawRect(rectF, mPaintText);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);
        mPaintText.setXfermode(null);
        if (!bufferBitmap.isRecycled()) {
            bufferBitmap.recycle();
        }
    }

    public int getTextColor() {
        if (mState == Status.NORMAL)
            return mDefaultTextColor;

        return mDefaultBtnColor;
    }

    @Override
    public String getText() {
        if (mState == Status.NORMAL && mText != null) {
            return mText;
        }

        if (isNotUseContinueText() && mText != null) {
            return mText;
        }

        if (mState == Status.PROCESSING || mState == Status.WAITING) {
            return String.format("%d%%", getProgress());
        }
        return mState.getResValue();
    }

    private boolean isNotUseContinueText() {
        return mActionType != 7 && (mState == Status.USER_PAUSE || mState == Status.ERROR
                || mState == Status.AUTO_PAUSE || mState == Status.MOBILE_PAUSE || mState == Status.NO_ENOUGH_STORAGE);
    }

    public int getXfermodeTextColor() {
        return mDefaultTextColor;
    }

    @Override
    protected int measureWidth(int defaultWidth, int measureSpec) {
        if (mPaintText == null)
            return defaultWidth;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (mText != null)
                    defaultWidth = Math.max((int) mPaintText.measureText(mText), (int) mPaintText.measureText(Status.USER_PAUSE.getResValue())) + getPaddingLeft() + getPaddingRight() + mTextMarginLeft + mTextMarginRight;
                else
                    defaultWidth = (int) mPaintText.measureText(Status.USER_PAUSE.getResValue()) + getPaddingLeft() + getPaddingRight() + mTextMarginLeft + mTextMarginRight;
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    @Override
    protected int measureHeight(int defaultHeight, int measureSpec) {
        if (mPaintText == null)
            return defaultHeight;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-mPaintText.ascent() + mPaintText.descent()) + getPaddingTop() + getPaddingBottom() + mTextMarginTop + mTextMarginBottom;
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        return defaultHeight;
    }


    public enum Status {
        NORMAL(-1, "DOWNLOAD"),
        WAITING(0, "DOWNLOAD"),
        USER_PAUSE(1, "CONTINUE"),
        PROCESSING(2, "DOWNLOAD"),
        ERROR(3, "CONTINUE"),
        COMPLETED(4, "INSTALL"),
        AUTO_PAUSE(5, "CONTINUE"),
        MOBILE_PAUSE(6, "CONTINUE"),
        NO_ENOUGH_STORAGE(7, "CONTINUE"),
        AZED(8, "OPEN"),
        UPDATE(9, "UPDATE");

        private int mValue;
        private String strValue;

        Status(int value, String resString) {
            mValue = value;
            this.strValue = resString;
        }

        private static SparseArray<Status> mValues = new SparseArray<>();

        static {
            for (Status item : Status.values())
                mValues.put(item.mValue, item);
        }

        public static Status fromInt(int value) {
            return mValues.get(value);
        }

        public String getResValue() {
            return strValue;
        }

        public int toInt() {
            return mValue;
        }
    }

    @Override
    protected void init() {
        super.init();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            checkBottomStatus();
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (mState == Status.AZED && progress == normalFinishProgress) {
            super.setProgress(progress);
            return;
        }
        if (progress >= 100 && mState != Status.NORMAL && mState != Status.UPDATE) {
            setState(Status.COMPLETED);
            progress = normalFinishProgress;
        }
        super.setProgress(progress);
    }

    public void createDownHelper(final String packName, final String url, final int versionCode) {
        destroy();
        registerReceiver();
        this.versionCode = versionCode;
        this.pkgName = packName;
        this.mOriginalUrl = url;
        this.mDownUrl = url;
        updateDownloadUrl();

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            int status;

            @Override
            public void execute() {
                status = PackageUtils.getAppStatus(getContext(), packName, versionCode);
            }

            @Override
            public void callBackOnUIThread() {
                if (status == PackageUtils.APP_STATUS_INSTALLED) {
                    setState(Status.AZED);
                    setProgress(normalFinishProgress);
                } else if (status == PackageUtils.APP_STATUS_NEED_UPGRADE) {
                    setState(Status.UPDATE);
                } else {
                    if (TextUtils.isEmpty(url)) {
                        setState(Status.NORMAL);
                    } else {
                        checkBottomStatus(true);
                    }
                }
            }
        });
    }

    @Override
    public void setText(String text) {
        checkBottomStatus();
        super.setText(text);
    }

    private void setState(Status state) {
        Status oldState = mState;
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(mDownUrl))
            mState = Status.NORMAL;
        else
            mState = state;

        if (mState == Status.NORMAL && getProgress() != normalProgress) {
            setProgress(normalProgress);
        }

        if (oldState != mState) {
            invalidate();
        }
    }

    private void checkBottomStatus() {
        checkBottomStatus(false);
    }

    private void checkBottomStatus(boolean isInitCheck) {
        if (System.currentTimeMillis() - mLastCheck <= 100 && !isInitCheck) {
            return;
        }
        mLastCheck = System.currentTimeMillis();
        updateDownloadUrl();
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(mDownUrl))
            setState(Status.NORMAL);
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }

        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void execute() {
            }

            @Override
            public void callBackOnUIThread() {
                if (azStatus == PackageUtils.APP_STATUS_INSTALLED) {
                    setState(Status.AZED);
                    setProgress(normalFinishProgress);
                    // InnerDownloadManager.STATUS_RUNNING 0
                } else if (azStatus == PackageUtils.APP_STATUS_NEED_UPGRADE) {
                    setState(Status.UPDATE);
                    setProgress(normalProgress);

                } else if (downloadStatus == 1) {//InnerDownloadManager.STATUS_SUCCESSFUL
                    setState(Status.COMPLETED);
                    setProgress(normalFinishProgress);
                } else {
                    setState(Status.NORMAL);
                }
            }
        });
    }

    private int getProgress(long completeSize, long fileSize) {
        int progress = (fileSize <= 0) ? 0 : Math.round(100 * completeSize / fileSize);
        if (progress > 100) {
            progress = 100;
        }
        return progress;
    }

    private void updateDownloadUrl() {
        ThreadManager.getInstance().run(new DelayRunnableWork() {
            @Override
            public void execute() throws Exception {
                mDownUrl = SettingConfig.getFinalUrl(mOriginalUrl);
                if (TextUtils.isEmpty(mDownUrl)) {
                    mDownUrl = mOriginalUrl;
                }
            }
        });
    }

    @Override
    public void destroy() {
        setProgress(normalProgress);
        mDownUrl = null;
        mOriginalUrl = null;
        pkgName = null;
        setState(Status.NORMAL);
        versionCode = 0;
        unRegisterReceiver();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)
                    || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String dataStr = intent.getDataString();
                if (TextUtils.isEmpty(dataStr))
                    return;
                String pkg = dataStr.substring(dataStr.lastIndexOf(":") + 1);
                if (TextUtils.isEmpty(pkg))
                    return;
                if (callback != null) {
                    callback.changedCallback(action, pkg);
                }
            }
        }
    };

    private abstract class PackageChangedCallback {
        public abstract void changedCallback(String action, String pkg);
    }

    private boolean hadRegisterReceiver = false;
    private PackageChangedCallback callback;

    private void registerReceiver() {
        if (hadRegisterReceiver)
            return;
        callback = new PackageChangedCallback() {
            @Override
            public void changedCallback(String action, String pkg) {
                if (!TextUtils.isEmpty(pkg) && pkg.equals(pkgName)) {
                    checkBottomStatus();
                }
            }
        };

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            getContext().registerReceiver(mReceiver, intentFilter);
            hadRegisterReceiver = true;
        } catch (Exception e) {
        }
    }

    private void unRegisterReceiver() {
        try {
            callback = null;
            hadRegisterReceiver = false;
            getContext().unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void registerClick(final Bid bid, final RegisterTextProgressListener registerTextProgressListener) {
        mActionType = bid.getActionType();
        setOnClickListener(this);
        destroy();

        setOnStateClickListener(new OnStateClickListener() {
            @Override
            public void onDownloading() {
            }

            @Override
            public void onNormal(Status state) {

                if (state != Status.COMPLETED && state != Status.AZED)
                    isShowNetGuideDialog(getContext(), bid);

                registerTextProgressListener.onNormal(state == Status.AZED, state == Status.COMPLETED);
            }

            @Override
            public void onPause() {
                isShowNetGuideDialog(getContext(), bid);
            }

            @Override
            public void onClick() {
            }
        });
    }

    private static boolean sIsNetWorkAvailable;

    private static void isShowNetGuideDialog(final Context context, final Bid adData) {
        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            @Override
            public void execute() {
                sIsNetWorkAvailable = NetworkUtils.isNetworkAvailable(context);
            }

            @Override
            public void callBackOnUIThread() {
            }
        });
    }

    public interface OnStateClickListener {
        void onClick();

        void onDownloading();

        void onNormal(Status state);

        void onPause();
    }
}
