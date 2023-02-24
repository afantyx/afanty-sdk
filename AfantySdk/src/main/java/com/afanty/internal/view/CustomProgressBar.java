package com.afanty.internal.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.afanty.R;
import com.afanty.internal.config.AftConfig;
import com.afanty.models.Bid;

@SuppressLint("AppCompatCustomView")
public class CustomProgressBar extends ProgressBar {
    public static String TAG = "AD.TextProgress";

    protected int mTextColor = Color.WHITE;
    private int mTextSizeProgress = 20;
    protected int normalProgress = 100;
    protected int normalFinishProgress = 100;
    protected int mTextMarginLeft;
    protected int mTextMarginRight;
    protected int mTextMarginTop;
    protected int mTextMarginBottom;
    private boolean mBoldTextType = false;
    protected int mDefaultBtnColor;
    protected int mDefaultTextColor;
    protected String mText;
    private int mTextMaxLength = 0;
    protected Paint mPaintText;

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(attrs);
        init();
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(attrs);
        init();
    }

    private void initParams(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.aft_TextProgress);
        if (typedArray != null) {
            mTextSizeProgress = getResources().getDimensionPixelSize(R.dimen.aft_common_dimens_14dp);
            mTextSizeProgress = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_textSize, mTextSizeProgress);
            mTextMarginLeft = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_text_margin_left, 0);
            mTextMarginRight = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_text_margin_right, 0);
            mTextMarginTop = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_text_margin_top, 0);
            mTextMarginBottom = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_text_margin_bottom, 0);
            mBoldTextType = typedArray.getBoolean(R.styleable.aft_TextProgress_text_bold, false);
            mText = splitText(typedArray.getString(R.styleable.aft_TextProgress_text));
            mTextMaxLength = typedArray.getDimensionPixelSize(R.styleable.aft_TextProgress_text_max_length, 0);
            if (mTextMaxLength > 0)
                mText = adapterTextEllipsis(mText, (float) mTextSizeProgress, (float) mTextMaxLength);
            mDefaultTextColor = typedArray.getColor(R.styleable.aft_TextProgress_text_default_color, Color.WHITE);
            mDefaultBtnColor = typedArray.getColor(R.styleable.aft_TextProgress_button_default_color, getResources().getColor(R.color.aft_color_00ca89));
            normalProgress = typedArray.getInteger(R.styleable.aft_TextProgress_normal_progress, 100);
            normalFinishProgress = typedArray.getInteger(R.styleable.aft_TextProgress_normal_finish_progress, normalProgress);
            typedArray.recycle();
        }
    }

    protected void init() {
        setProgress(normalProgress);
        mTextColor = mDefaultTextColor;

        if (mPaintText == null) {
            mPaintText = new Paint();
            mPaintText.setTextSize(mTextSizeProgress);
            mPaintText.setTextAlign(Paint.Align.CENTER);
            mPaintText.setAntiAlias(true);
            if (mBoldTextType)
                mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        }

    }

    private String splitText(String text) {
        if (text != null && text.length() > AftConfig.getBtnCharacterCount()) {
            return text.substring(0, AftConfig.getBtnCharacterCount()) + "...";
        }
        return text;
    }

    private String adapterTextEllipsis(String inputString, float textSize, float fillMaxWidth) {
        if (inputString == null || TextUtils.isEmpty(inputString) || fillMaxWidth <= 0L)
            return inputString;

        String text = inputString;

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        float strWidth = paint.measureText(text);

        try {
            if (strWidth > fillMaxWidth) {
                int showIndex = (int) Math.floor(fillMaxWidth / (strWidth / text.length())) - 1;
                if (showIndex > 0 && showIndex < text.length())
                    text = text.substring(0, showIndex) + "...";
            }
        } catch (Exception e) {
        }

        return text;
    }

    public void setText(String text) {
        this.mText = splitText(text);
        if (mTextMaxLength > 0)
            this.mText = adapterTextEllipsis(text, (float) mTextSizeProgress, (float) mTextMaxLength);
        invalidate();
    }

    public String getText() {
        return mText;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCustomText(canvas);
    }

    protected void drawCustomText(Canvas canvas) {
        if (mPaintText == null)
            return;
        if (getMeasuredWidth() != 0) {
            setSecondaryProgress(0);
        }

        mPaintText.setColor(mTextColor);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float fTextHeightStart = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2f - 2f;
        String text = getText();
        canvas.drawText(text, getWidth() / 2f, fTextHeightStart, mPaintText);

    }

    public void registerClick(Bid bid, final RegisterTextProgressListener registerTextProgressListener) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTextProgressListener.onNormal(false, false);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mPaintText == null)
            return;
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    protected int measureWidth(int defaultWidth, int measureSpec) {
        if (mPaintText == null)
            return defaultWidth;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (mText != null)
                    defaultWidth = (int) mPaintText.measureText(mText) + getPaddingLeft() + getPaddingRight() + mTextMarginLeft + mTextMarginRight;
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

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

    public void destroy() {
    }

    public interface RegisterTextProgressListener {
        void onNormal(boolean openOpt, boolean CTAOpt);
    }


}